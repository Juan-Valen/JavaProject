package org.example.framework;

import java.util.Arrays;
import java.util.PriorityQueue;

public class EventList {
    private PriorityQueue<Event> eventList;

    public EventList() {
        eventList = new PriorityQueue<>();
    }



    public double getNextEventTime() {
        if (eventList.isEmpty())
            return 0;
        return eventList.peek().getTime();
    }


    public void add(Event e) {
        System.out.printf("Adding to the event list %s at %d %n", e.getType(), e.getTime());
        eventList.add(e);
    }

    public Event remove() {
        if (eventList.isEmpty()) return null;
        System.out.printf("Removing from the event list %s at %d %n",
                eventList.peek().getType(), eventList.peek().getTime() );
        return eventList.remove();
    }

    public Event poll() {
        if (eventList.isEmpty()) return null;
        Event e = eventList.poll();
        System.out.println(" ");
        System.out.printf("Polling from the event list %s at %d %n", e.getType(), e.getTime());
        return e;
    }


    public boolean isEmpty(){
        return eventList.isEmpty();
    }

    public void print() {
        Object[] tmp = eventList.toArray();
        Arrays.sort(tmp);
        for (Object e : tmp)
            System.out.println(e);
    }
}
