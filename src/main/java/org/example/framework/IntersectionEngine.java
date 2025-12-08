package org.example.framework;

import javafx.application.Platform;
import org.example.controller.TrafficLightController;
import org.example.distributions.Normal;
import org.example.model.*;

import java.util.List;
import java.util.function.Consumer;


// MAIN SIMULATION
public class IntersectionEngine extends Engine{
    // First intersection in chain
    private TrafficLightIntersection intersection1;

    // List of all intersections
    private List<Intersection> intersectionList = new java.util.ArrayList<>();

    // Event list
    private EventList el;

    // Pause flag
    private volatile boolean paused = false;

    // Time program was paused
    private long pausedStart;

    // Total duration of pauses
    private long totalPausedDuration = 0;

    // Traffic light controller shared by intersections
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

    // temporary variables and methods for checking arrivals and completions
    public static int carsSent = 0;
    public static int carsArrived = 0;
    static List<Long> passThroughTimes = new java.util.ArrayList<>();

    public static int getCarsSent() {
        return carsSent;
    }

    public static int getCarsArrived() {
        return carsArrived;
    }

    public static void setCarsSent(int carsSent) {
        IntersectionEngine.carsSent = carsSent;
    }

    public static void setCarsArrived(int carsArrived) {
        IntersectionEngine.carsArrived = carsArrived;
    }

    public static void addPassThroughTime(long time) {
        passThroughTimes.add(time);
    }

    private double simulationSpeed = 3; // How many seconds to sleep between events

    private static int timeToCrossIntersection = 10; // time to cross intersection once started

    private static int avgArrivalInterval = 120; // average arrival interval for car groups
    private static int maxCarGroupSize = 8; // maximum size of car groups arriving, average is half of this
    private static int avgReactionTime = 20; // average time for driver to react and accelerate when light turns green or other car gives way

    private static Normal carGroupSizeDist = new Normal((double) maxCarGroupSize /2, (double) maxCarGroupSize /2); // average size of car groups arriving
    private static Normal driverReactionTimeDist = new Normal((double)avgReactionTime, (double)avgReactionTime); // average driver reaction time before starting to pass intersection
    private static Normal carArrivalIntervalDist = new Normal((double) avgArrivalInterval, (double) avgArrivalInterval); // average arrival interval for car groups


    private int greendelay = 30; // seconds after next light change after turning green
    private int yellowdelay = 6; // seconds after next light change after turning yellow

    @Override
    protected void initialize() {
        getTrafficLightController().setGreenDelay(greendelay);
        getTrafficLightController().setYellowDelay(yellowdelay);

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
//        for (int i = 0; i < 8; i++) {
//            long tA = i * 30; // arrivals to direction A
//            long tB = i * 45 + 10; // arrivals to direction B
//            el.add(new Event(tA, Event.EventType.ARRIVAL, new Arrival(new Car(i*2), true, intersection1),"Arrival of Car at time: " + (i*2) + " to direction A"));
//            el.add(new Event(tB, Event.EventType.ARRIVAL, new Arrival(new Car(i*2+1), false, intersection1),"Arrival of Car at time: " + (i*2+1) + " to direction B"));
//
//            carsSent++;
//        }

        // schedule initial QUEUE_ARRIVALS events for direction A
        el.add(new Event(30, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersection1, true), "Initial QUEUE_ARRIVALS Event for direction A at " + intersection1.getName()));

        // schedule initial QUEUE_ARRIVALS events for direction B
        el.add(new Event(45, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersection1, false), "Initial QUEUE_ARRIVALS Event for direction B at " + intersection1.getName()));
        el.add(new Event(45, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersection2, false), "Initial QUEUE_ARRIVALS Event for direction B at " + intersection2.getName()));
        el.add(new Event(45, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersection3, false), "Initial QUEUE_ARRIVALS Event for direction B at " + intersection3.getName()));


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

            case QUEUE_ARRIVALS -> {
                QueueArrivals qa = (QueueArrivals) e.getPayload();
                qa.getIntersection().queueArrivals(qa, now, el);
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
        System.out.println("Total cars sent: " + carsSent);
        System.out.println("Total cars arrived at destination: " + carsArrived);
        System.out.println("Average pass-through time: " + passThroughTimes.stream().mapToLong(Long::longValue).average().orElse(0.0));
    }


    // main loop of the simulation with callback for GUI updates
    public void runWithCallback(Consumer<Event> callback) {
        initialize();

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

                if (callback != null) {
                    Platform.runLater(() -> callback.accept(e));
                }
            }

            // To do: adjust sleep time based on speed settings
            // if needed make sleep less when loop takes longer than expected
            try {
                Thread.sleep((long) (simulationSpeed* 100));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        results();
    }


        public Intersection getIntersection1(){
        return intersection1;
    }


    public static void main(String[] args) {
        IntersectionEngine eng = new IntersectionEngine();
        eng.run();
    }

    public long getCurrentTime(){
        return Clock.getInstance().getClock();
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

    public static int getAvgArrivalInterval() {
        return avgArrivalInterval;
    }

    public static int getMaxCarGroupSize() {
        return maxCarGroupSize;
    }

    public static int getAvgReactionTime() {
        return avgReactionTime;
    }

    public static Normal getCarGroupSizeDist() {
        return carGroupSizeDist;
    }

    public static void setCarGroupSizeDist(int maxCarGroupSize) {
        carGroupSizeDist = new Normal((double) maxCarGroupSize /2, (double) maxCarGroupSize /2);
    }

    public static Normal getDriverReactionTimeDist() {
        return driverReactionTimeDist;
    }

    public static void setDriverReactionTimeDist(int avgReactionTime) {
    	driverReactionTimeDist = new Normal((double)avgReactionTime, (double)avgReactionTime);
    }

    public static Normal getCarArrivalIntervalDist() {
        return carArrivalIntervalDist;
    }

    public static int getTimeToCrossIntersection() {
        return timeToCrossIntersection;
    }

    public static void setTimeToCrossIntersection(int timeToCrossIntersection) {
        IntersectionEngine.timeToCrossIntersection = timeToCrossIntersection;
    }

    public static void setCarArrivalIntervalDist(int avgArrivalInterval) {
    	carArrivalIntervalDist = new Normal((double) avgArrivalInterval, (double) avgArrivalInterval);
    }


}