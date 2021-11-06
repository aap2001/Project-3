import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity
{
    private EntityKind kind;
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

    public Entity(
            EntityKind kind,
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit)
    {
        this.setKind(kind);
        this.setId(id);
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.setHealth(health);
        this.healthLimit = healthLimit;
    }

    public void executeSaplingActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        this.setHealth(this.getHealth() + 1);
        if (!this.transformPlant(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod, this);
        }
    }
    public void executeTreeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {

        if (!this.transformPlant( world, scheduler, imageStore)) {

            scheduler.scheduleEvent(
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod, this);
        }
    }
    public void executeFairyActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> fairyTarget =
                world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {
                Entity sapling = Functions.createSapling("sapling_" + this.getId(), tgtPos,
                        Functions.getImageList(imageStore, Functions.SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(
                this.createActivityAction(world, imageStore),
                this.actionPeriod, this);
    }

    public void executeDudeNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> target =
                world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !this.moveToNotFull(world,
                target.get(),
                scheduler)
                || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod, this);
        }
    }

    public void executeDudeFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> fullTarget =
                world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && this.moveToFull(world,
                fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod, this);
        }
    }
    private boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = Functions.createDudeFull(this.getId(),
                    this.getPosition(), this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        Entity miner = Functions.createDudeNotFull(this.getId(),
                this.getPosition(), this.actionPeriod,
                this.animationPeriod,
                this.resourceLimit,
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    private Point nextPositionFairy(
            WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    private Point nextPositionDude(
            WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getKind() != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getKind() != EntityKind.STUMP) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        switch (this.getKind()) {
            case DUDE_FULL:
                scheduler.scheduleEvent(
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod, this);
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            case DUDE_NOT_FULL:
                scheduler.scheduleEvent(
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod, this);
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            case OBSTACLE:
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            case FAIRY:
                scheduler.scheduleEvent(
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod, this);
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            case SAPLING:
                scheduler.scheduleEvent(
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod, this);
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            case TREE:
                scheduler.scheduleEvent(
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod, this);
                scheduler.scheduleEvent(
                        this.createAnimationAction(0),
                        this.getAnimationPeriod(), this);
                break;

            default:
        }
    }
    public PImage getCurrentImage() {
            return (this.getImages().get((this.getImageIndex())));
        }

    private boolean transformPlant(WorldModel world,
                                  EventScheduler scheduler,
                                  ImageStore imageStore) {
        if (this.getKind() == EntityKind.TREE) {
            return this.transformTree(world, scheduler, imageStore);
        } else if (this.getKind() == EntityKind.SAPLING) {
            return this.transformSapling(world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(
                    String.format("transformPlant not supported for %s", this));
        }
    }

    private boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Functions.createStump(this.getId(),
                    this.getPosition(),
                    Functions.getImageList(imageStore, Functions.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean transformSapling(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Functions.createStump(this.getId(),
                    this.getPosition(),
                    Functions.getImageList(imageStore, Functions.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        } else if (this.getHealth() >= this.healthLimit) {
            Entity tree = Functions.createTree("tree_" + this.getId(),
                    this.getPosition(),
                    Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
                    Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
                    Functions.getNumFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN),
                    Functions.getImageList(imageStore, Functions.TREE_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean moveToFairy(          //removed Entity fairy

            WorldModel world, Entity target,      // do i remove this?? divya said don't
            EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.getPosition());

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

    private boolean moveToNotFull(            // removed Entity dude

            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            this.resourceCount += 1;
            target.setHealth(target.getHealth() - 1);
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

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

    private boolean moveToFull(         // removed Entity dude

            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

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
    public int getAnimationPeriod() {
        switch (this.getKind()) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return this.animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                this.getKind()));
        }
    }

    public void nextImage() {
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size());
    }


    public Action createAnimationAction(int repeatCount) {
        return new Action(ActionKind.ANIMATION, this, null, null,
                repeatCount);
    }

    private Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, this, world, imageStore, 0);
    }


    public EntityKind getKind() {
        return kind;
    }

    private void setKind(EntityKind kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    private List<PImage> getImages() {
        return images;
    }

    private void setImages(List<PImage> images) {
        this.images = images;
    }

    private int getImageIndex() {
        return imageIndex;
    }

    private void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public int getHealth() {
        return health;
    }

    private void setHealth(int health) {
        this.health = health;
    }
}