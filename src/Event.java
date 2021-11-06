/**
 * An event is made up of an Entity that is taking an
 * Action a specified time.
 */
public final class Event
{
    private Action action;
    private long time;
    private Entity entity;

    public Event(Action action, long time, Entity entity) {
        this.setAction(action);
        this.setTime(time);
        this.setEntity(entity);
    }

    public Action getAction() {
        return action;
    }

    private void setAction(Action action) {
        this.action = action;
    }

    public long getTime() {
        return time;
    }

    private void setTime(long time) {
        this.time = time;
    }

    public Entity getEntity() {
        return entity;
    }

    private void setEntity(Entity entity) {
        this.entity = entity;
    }
}
