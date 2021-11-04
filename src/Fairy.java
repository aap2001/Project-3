import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Fairy implements Entity, Executable, Active, Movable, Positionable {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;
    private int animationPeriod;
    public Fairy(
            String id,
            Point position,
            List<PImage> images,
            int imageIndex,
            int actionPeriod,
            int animationPeriod
    )
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;

    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> fairyTarget =
                world.findNearest(this.getPosition(), new ArrayList(Arrays.asList(House.class.getName())));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {
                Entity sapling = Functions.createSapling("sapling_" + this.getId(), tgtPos,
                        Functions.getImageList(imageStore, Functions.SAPLING_KEY));

                world.addEntity(sapling);
                this.scheduleActions(scheduler, world, imageStore);
            }
        }
    }
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
    public Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return null;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setId() {
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

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public PImage getCurrentImage() {
        return (this).getImages().get(
                (this).getImageIndex());

    }


    @Override
    public boolean moveTo(

            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
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
}