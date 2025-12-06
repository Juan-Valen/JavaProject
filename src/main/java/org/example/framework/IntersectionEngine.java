package org.example.framework;

import javafx.application.Platform;
import org.example.controller.TrafficLightController;
import org.example.model.*;

import java.util.List;
import java.util.function.Consumer;


// MAIN SIMULATION
public class IntersectionEngine extends Engine{
    private TrafficLightIntersection intersection1;

    private List<Intersection> intersectionList = new java.util.ArrayList<>();

    private EventList el;
    private volatile boolean paused = false;

    private long pausedStart;
    private long totalPausedDuration = 0;

    private TrafficLightController trafficLightController;

    public IntersectionEngine() {
        this.trafficLightController = new TrafficLightController();
    }


    public TrafficLightController getTrafficLightController() {
        if (trafficLightController == null) {
            throw new IllegalStateException("TrafficLightController has not been initialized. " +
                    "Make sure to create it in the IntersectionEngine constructor or via an init method.");
        }
        return trafficLightController;
    }


    @Override
    protected void initialize() {
        el = eventList;
        // chain one intersection (can add more)
        BareIntersection intersection3 = new BareIntersection("Intersection-3", null, 30, 120, trafficLightController);
        TrafficLightIntersection intersection2 = new TrafficLightIntersection("Intersection-2", intersection3, 50, 150, trafficLightController);
        intersection1 = new TrafficLightIntersection("Intersection-1", intersection2, 20, 100, trafficLightController);

        intersectionList.add(intersection1);
        intersectionList.add(intersection2);
        intersectionList.add(intersection3);

        // schedule arrivals on both directions
        //TEMP: fixed arrivals for testing, make random continuous generation later.
        for (int i = 0; i < 8; i++) {
            long tA = i * 30; // arrivals to direction A
            long tB = i * 45 + 10; // arrivals to direction B
            el.add(new Event(tA, Event.EventType.ARRIVAL, new Arrival(new Car(i*2), true, intersection1),"Arrival of Car at time: " + (i*2) + " to direction A"));
            el.add(new Event(tB, Event.EventType.ARRIVAL, new Arrival(new Car(i*2+1), false, intersection1),"Arrival of Car at time: " + (i*2+1) + " to direction B"));
        }

        // schedule initial traffic light change
        el.add(new Event(600, Event.EventType.LIGHT_CHANGE, new TrafficLightChange(intersection1), "Initial Traffic Light Change Event for: " + intersection1.getName()));

        //set initial traffic light states
        trafficLightController.setNSGreen();

        setSimulationTime(10000);
    }

    @Override
    protected void runEvent(Event e) {
        long now = e.getTime();
        switch (e.getType()) {
            case ARRIVAL -> {
                Arrival a = (Arrival) e.getPayload();
                a.getIntersection().handleArrival(a);
            }
            case DEPARTURE -> {
                Departure d = (Departure) e.getPayload();
                d.getIntersection().completeService(d, now, el);
            }
            case LIGHT_CHANGE -> {
                TrafficLightChange tlc = (TrafficLightChange) e.getPayload();
                tlc.getIntersection().ChangeTrafficLights(now, el);
            }

        }
    }

    @Override
    protected void tryCEvents() {
        // called each C-phase at current clock: allow intersection to start at most one service
        long now = Clock.getInstance().getClock();
        for (Intersection i : intersectionList) {
            i.startPassingIntersection(now, eventList);
        }
//        intersection1.startPassingIntersection(now, eventList);
    }

    @Override
    protected void results() {
        System.out.println(" ");
        System.out.println("Simulation finished at " + Clock.getInstance().getClock());
    }


    // main loop of the simulation with callback for GUI updates
    public void runWithCallback(Consumer<Event> callback) {
        initialize();


//        for (long t = 0; t < getSimulationTime(); t += 100) {
//            el.add(new Event(t, Event.EventType.TICK, null, "Simulation tick"));
//        }


        while (Clock.getInstance().getClock() < getSimulationTime()) {
            if (paused) {
                synchronized (this) {
                    while (paused) {
                        System.out.println("Simulation paused...");
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            Event e = el.poll();
            if (e != null) {
                Clock.getInstance().setClock(e.getTime());
                runEvent(e);
                tryCEvents();

                System.out.println("--- Queue States at time " + Clock.getInstance().getClock() + " ---");
                for (Intersection i : intersectionList) {
                    System.out.println("queueA: " + i.getQueueA().size() + " queueB: " + i.getQueueB().size());
                }

//                duplicate please delete later
//                if (e.getType() == Event.EventType.TICK) {
//                    trafficLightController.update(0.1);
//                }

                if (callback != null) {
                    Platform.runLater(() -> callback.accept(e));
                }
            }
            // THIS SHOULD NOT HAPPEN
//            else {
//                // No event? Advance clock manually
//                Clock.getInstance().setClock(Clock.getInstance().getClock() + 100);
//            }

            // To do: adjust sleep time based on speed settings
            // if needed make sleep less when loop takes longer than expected
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
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



    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            pausedStart = System.currentTimeMillis();
        } else {
            totalPausedDuration += System.currentTimeMillis() - pausedStart;
            notifyAll();
        }
    }


    public void setSimulationTime(double addedTime) {
        super.setSimulationTime(this.getSimulationTime()+addedTime);
        System.out.println("Remaining simulation time: "+ getRemainingSimulationTime());
    }
}