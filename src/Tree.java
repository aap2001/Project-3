import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Tree implements Executable, Entity, Transformable, Positionable, Active, Health, Animate{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private int health;
    private int healthLimit;

    public Tree(
            String id,
            Point position,
            List<PImage> images,
            int imageIndex,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit
    )
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    @Override
    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {

        if (!this.transform( world, scheduler, imageStore)) {

            scheduler.scheduleEvent(
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod, this);
        }
    }


    @Override
    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        scheduler.scheduleEvent(
                this.createActivityAction(world, imageStore),
                this.actionPeriod, this);
        scheduler.scheduleEvent(
                this.createAnimationAction(0,world,imageStore),
                this.getAnimationPeriod(), this);
    }

    public Action createAnimationAction(int repeatCount,WorldModel world,ImageStore imageStore) {
        return new Animation((Animate) this,world,imageStore,repeatCount);
    }

    public int getAnimationPeriod() {
        return this.animationPeriod;
    }

    @Override
    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    @Override
    public Action createActivityAction(
            WorldModel world, ImageStore imageStore) {
        return new Activity( this, world, imageStore, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public List<PImage> getImages() {
        return images;
    }

    public void setImages(List<PImage> images) {
        this.images = images;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public PImage getCurrentImage() {
        return (this).getImages().get(
                (this).getImageIndex());

    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public Point nextPosition(
            WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos) && !world.getOccupancyCell(newPos).getClass().getName().equals(Stump.class.getName())) {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass().getName().equals(Stump.class.getName())) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity miner = Functions.createDudeNotFull(this.getId(),
                this.getPosition(), this.getImages(), this.resourceLimit, this.resourceCount, this.actionPeriod,
                this.animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        this.scheduleActions(scheduler, world, imageStore);
        return true;
    }
}
