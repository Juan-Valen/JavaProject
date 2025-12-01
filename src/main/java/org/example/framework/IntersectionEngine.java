package org.example.framework;

import org.example.model.*;


// MAIN SIMULATION
public class IntersectionEngine extends Engine{
    private Intersection intersection1;
    private EventList el;

    @Override
    protected void initialize() {
        el = eventList;
        // chain one intersection (can add more)
        Intersection intersection2 = new Intersection("Intersection-2", null, 50, 150);
        intersection1 = new Intersection("Intersection-1", intersection2, 20, 100);

        // schedule arrivals on both directions
        for (int i = 0; i < 8; i++) {
            long tA = i * 30; // arrivals to direction A
            long tB = i * 45 + 10; // arrivals to direction B
            el.add(new Event(tA, Event.EventType.ARRIVAL, new Arrival(new Car(i*2), true),"Arrival of Car at time: " + (i*2) + " to direction A"));
            el.add(new Event(tB, Event.EventType.ARRIVAL, new Arrival(new Car(i*2+1), false),"Arrival of Car at time: " + (i*2+1) + " to direction B"));
        }

        setSimulationTime(1000);
    }

    @Override
    protected void runEvent(Event e) {
        long now = e.getTime();
        switch (e.getType()) {
            case ARRIVAL -> {
                Arrival a = (Arrival) e.getPayload();
                intersection1.handleArrival(a);
            }
            case DEPARTURE -> {
                Departure d = (Departure) e.getPayload();
                intersection1.completeService(d, now, el);
            }
        }
    }

    @Override
    protected void tryCEvents() {
        // called each C-phase at current clock: allow intersection to start at most one service
        long now = Clock.getInstance().getClock();
        intersection1.tryStartService(now, eventList);
    }

    @Override
    protected void results() {
        System.out.println("Simulation finished.");
    }

    public void runWithCallback(Runnable callback) {
        initialize();
        while (!el.isEmpty() && Clock.getInstance().getClock() < getSimulationTime()) {
            Event e = el.poll();
            Clock.getInstance().setClock(e.getTime());
            runEvent(e);
            tryCEvents();
            callback.run(); // Update UI after each event
        }
        results();
    }

    public Intersection getIntersection1(){
        return intersection1;
    }

    public long getCurrentTime(){
        return Clock.getInstance().getClock();
    }

    public static void main(String[] args) {
        IntersectionEngine eng = new IntersectionEngine();
        eng.run();
    }



}