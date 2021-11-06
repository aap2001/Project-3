import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler
{
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double timeScale;

    public EventScheduler(double timeScale) {
        this.setEventQueue(new PriorityQueue<>(new EventComparator()));
        this.setPendingEvents(new HashMap<>());
        this.setTimeScale(timeScale);
    }

    public void updateOnTime(long time) {
        while (!this.getEventQueue().isEmpty()
                && this.getEventQueue().peek().getTime() < time) {
            Event next = this.getEventQueue().poll();

            this.removePendingEvent(next);

            next.getAction().executeAction(this);
        }
    }

    private void removePendingEvent(
            Event event) {
        List<Event> pending = this.getPendingEvents().get(event.getEntity());

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void unscheduleAllEvents(
            Entity entity)
    {
        List<Event> pending = this.getPendingEvents().remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                this.getEventQueue().remove(event);
            }
        }
    }

    public void scheduleEvent(
            Action action,
            long afterPeriod, Entity entity) {
        long time = System.currentTimeMillis() + (long) (afterPeriod
                * getTimeScale());
        Event event = new Event(action, time, entity);

        getEventQueue().add(event);

        // update list of pending events for the given entity
        List<Event> pending = getPendingEvents().getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        getPendingEvents().put(entity, pending);
    }

    private PriorityQueue<Event> getEventQueue() {
        return eventQueue;
    }

    private void setEventQueue(PriorityQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    private Map<Entity, List<Event>> getPendingEvents() {
        return pendingEvents;
    }

    private void setPendingEvents(Map<Entity, List<Event>> pendingEvents) {
        this.pendingEvents = pendingEvents;
    }

    private double getTimeScale() {
        return timeScale;
    }

    private void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }
}
