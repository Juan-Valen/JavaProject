package org.example.framework;

public class Event implements Comparable<Event> {
    public enum  EventType {
        ARRIVAL,
        DEPARTURE,
        EXIT,
        LIGHT_CHANGE,
        QUEUE_ARRIVALS,
    }

    private EventType type;
    private final long time;
    private final Object payload;
    private String description;

    public Event(long time, EventType type, Object payload, String description) {
        this.type = type;
        this.time = time;
        this.payload = payload;
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public EventType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

    public  String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return time + " [" + type + "]" + " : " + description;
    }

    @Override
    public int compareTo(Event e) {
//        if (time < e.time)
//            return -1;
//        else if (time > e.time)
//            return 1;
//        return 0;
        return Double.compare(this.time, e.time);
    }
}