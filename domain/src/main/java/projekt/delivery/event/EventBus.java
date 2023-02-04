package projekt.delivery.event;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class for storing all {@link Event}s that occurred during a tick.
 */
public class EventBus {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Event> queuedEvents = new ArrayList<>();
    private final Map<Long, List<Event>> log = new HashMap<>();
    private final Map<Long, List<Event>> unmodifiableLog = Collections.unmodifiableMap(log);

    /**
     * Adds an {@link Event} to the bus.
     * @param event The {@link Event} to add.
     */
    public void queuePost(Event event) {
        lock.readLock().lock();
        try {
            queuedEvents.add(event);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Add all given {@link Event}s to the bus.
     * @param events The {@link Event}s to add.
     */
    public void queuePost(Collection<Event> events) {
        lock.readLock().lock();
        try {
            queuedEvents.addAll(events);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns all {@link Event}s that have been added since the last time this bus has been cleared and clears this bus.
     * @param tick The current Tick.
     * @return All {@link Event}s that have been added since the last time this bus has been cleared
     */
    public List<Event> popEvents(long tick) {
        // is not a read lock because the queue has to be cleared too
        lock.writeLock().lock();
        try {
            log.put(tick, queuedEvents);
            System.out.printf("Tick: %s - %s\n", tick, queuedEvents);
            List<Event> copy = new ArrayList<>(queuedEvents);
            clear();
            return copy;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes all added {@link Event}s.
     */
    public void clear() {
        queuedEvents.clear();
    }

    /**
     * Returns a log of this bus.
     * @return A log of this bus containing all {@link Event}s popped at a specific tick.
     */
    public Map<Long, List<Event>> getLog() {
        return unmodifiableLog;
    }
}
