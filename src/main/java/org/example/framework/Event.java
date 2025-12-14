package org.example.framework;

/**
 * Represents an event in the simulation.
 *
 * <p>Each event has a type, a scheduled time, an optional payload, and a description.
 * Events are comparable based on their scheduled time to facilitate event scheduling.
 */
public class Event implements Comparable<Event> {

    /** Types of events in the simulation. */
    public enum  EventType {
        ARRIVAL,
        DEPARTURE,
        EXIT,
        LIGHT_CHANGE,
        QUEUE_ARRIVALS,
    }

    /** Type of the event. */
    private EventType type;

    /** Scheduled time of the event. */
    private final long time;

    /** Optional payload associated with the event. */
    private final Object payload;

    /** Description of the event. */
    private String description;

    /** Constructs an Event with the specified parameters.
     * @param time The scheduled time of the event.
     * @param type The type of the event.
     * @param payload The event object associated with the event.
     * @param description The description of the event.
     */
    public Event(long time, EventType type, Object payload, String description) {
        this.type = type;
        this.time = time;
        this.payload = payload;
        this.description = description;
    }

    /** Gets the scheduled time of the event.
     * @return The scheduled time.
     */
    public long getTime() {
        return time;
    }

    /** Gets the type of the event.
     * @return The event type.
     */
    public EventType getType() {
        return type;
    }

    /** Gets the event object payload.
     * @return The event payload.
     */
    public Object getPayload() {
        return payload;
    }

    /**
     *  Gets the string representation of the event.
     * @return The string representation.
     */
    @Override
    public String toString() {
        return time + " [" + type + "]" + " : " + description;
    }

    /** Compares this event with another event based on their scheduled times.
     * @param e The other event to compare with.
     * @return A negative integer, zero, or a positive integer as this event is less than,
     *         equal to, or greater than the specified event.
     */
    @Override
    public int compareTo(Event e) {
        return Double.compare(this.time, e.time);
    }
}