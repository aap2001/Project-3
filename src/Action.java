/**
 * An action that can be taken by an entity
 */
public final class Action
{
    private ActionKind kind;
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Action(
            ActionKind kind,
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        switch (this.kind) {
            case ACTIVITY:
                this.executeActivityAction(scheduler);
                break;

            case ANIMATION:
                this.executeAnimationAction(scheduler);
                break;
        }
    }

    private void executeAnimationAction(
            EventScheduler scheduler) {
        entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(
                    entity.createAnimationAction(
                            Math.max(this.repeatCount - 1,
                                    0)),
                    entity.getAnimationPeriod(), entity);
        }
    }

    private void executeActivityAction(
            EventScheduler scheduler) {
        switch (this.entity.getKind()) {
            case SAPLING:
                this.entity.executeSaplingActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case TREE:
                this.entity.executeTreeActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case FAIRY:
                this.entity.executeFairyActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case DUDE_NOT_FULL:
                this.entity.executeDudeNotFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case DUDE_FULL:
                this.entity.executeDudeFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        this.entity.getKind()));
        }
    }

}