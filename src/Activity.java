public class Activity implements Action {
    private ActionKind kind;
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(

            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        this.executeActivityAction(scheduler);
    }

    private void executeActivityAction(
            EventScheduler scheduler) {
        Executable executableEntity = (Executable) this.entity;

        executableEntity.executeActivity(this.world,
                this.imageStore, scheduler);
        /*
        switch (this.entity.getKind()) {
            case SAPLING:
                this.entity.executeActivity(this.world,
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
                        this.entity.getKind()));*/
        }



}
