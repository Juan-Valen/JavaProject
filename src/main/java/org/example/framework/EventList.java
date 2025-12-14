package org.example.framework;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * EventList class that manages a priority queue of events.
 */
public class EventList {

    /** Priority queue to hold the events. */
    private PriorityQueue<Event> eventList;

    /** Constructs an EventList with an empty priority queue. */
    public EventList() {
        eventList = new PriorityQueue<>();
    }

    /** Adds an event to the event list.
     * @param e The event to be added.
     */
    public void add(Event e) {
        System.out.printf("Adding to the event list %s at %d %n", e.getType(), e.getTime());
        eventList.add(e);
    }

    /** Polls (removes and returns) the next event from the event list.
     * @return The next event, or null if the list is empty.
     */
    public Event poll() {
        if (eventList.isEmpty()) return null;
        Event e = eventList.poll();
        System.out.println(" ");
        System.out.printf("Polling from the event list %s at %d %n", e.getType(), e.getTime());
        return e;
    }

    /** Checks if the event list is empty.
     * @return true if the event list is empty, false otherwise.
     */
    public boolean isEmpty(){
        return eventList.isEmpty();
    }
}
