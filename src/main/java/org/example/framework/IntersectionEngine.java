package org.example.framework;

import javafx.application.Platform;
import org.example.controller.TrafficLightController;
import org.example.distributions.Normal;
import org.example.model.*;
import org.example.view.HomeView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The IntersectionEngine class manages the simulation of traffic intersections,
 * handling events such as vehicle arrivals, departures, and traffic light changes.
 * It extends the Engine class to utilize its event-driven simulation capabilities.
 */
// MAIN SIMULATION
public class IntersectionEngine extends Engine{

    /**
     * The HomeView instance for GUI updates.
     */
    private HomeView homeview;

    /**
     * List of intersections managed by the engine.
     */
    // List of all intersections
    private List<Intersection> intersectionList = new ArrayList<>();

    /**
     * The event list that stores all simulation events. Events are processed in chronological order.
     */
    // Event list
    private EventList el;

    /**
     * Pause management variables.
     */
    // Pause flag
    private volatile boolean paused = false;

    /**
     * Timestamp when the program was paused.
     */
    // Time program was paused
    private long pausedStart;

    /**
     * Total duration the program has been paused.
     */
    // Total duration of pauses
    private long totalPausedDuration = 0;

    /**
     * The TrafficLightController instance shared by intersections.
     */
    // Traffic light controller shared by intersections
    private TrafficLightController trafficLightController;

    /**
     * Constructor for IntersectionEngine.
     *
     * @param trafficLightController The TrafficLightController instance to be used by intersections.
     * @param homeview The HomeView instance for GUI updates.
     */
    public IntersectionEngine(TrafficLightController trafficLightController, HomeView homeview) {
        this.trafficLightController = trafficLightController;
        this.homeview = homeview;
        trafficLightController.setHomeView(homeview);
    }

    /**
     * Gets the TrafficLightController instance.
     *
     * @return The TrafficLightController instance.
     * @throws IllegalStateException if the TrafficLightController has not been initialized.
     */
    public TrafficLightController getTrafficLightController() {
        if (trafficLightController == null) {
            throw new IllegalStateException("TrafficLightController has not been initialized. " +
                    "Make sure to create it in the IntersectionEngine constructor or via an init method.");
        }
        return trafficLightController;
    }


    // temporary variables and methods for checking arrivals and completions
    /**
     * AtomicInteger to track the number of cars sent in the simulation for testing purposes.
     */
    public static AtomicInteger carsSent = new AtomicInteger(0);

    /**
     * AtomicInteger to track the number of cars that have arrived at their destination in the simulation for testing purposes.
     */
    private static AtomicInteger carsArrived = new AtomicInteger(0);

    /**
     * List to store the pass-through times of cars for calculating average times.
     */
    private static final List<Long> passThroughTimes = new ArrayList<>();

    /**
     * Gets the AtomicInteger tracking the number of cars sent.
     *
     * @return The AtomicInteger tracking the number of cars sent.
     */
    public static AtomicInteger getCarsSent() {
        return carsSent;
    }

    /**
     * Gets the AtomicInteger tracking the number of cars that have arrived at their destination.
     *
     * @return The AtomicInteger tracking the number of cars that have arrived.
     */
    public static AtomicInteger getCarsArrived() {
        return carsArrived;
    }

    /**
     * Adds a pass-through time to the list for average calculation.
     *
     * @param time The pass-through time to add.
     */
    public static void addPassThroughTime(long time) {
        passThroughTimes.add(time);
    }

    /**
     * Simulation speed factor. The simulation sleeps for (simulationSpeed * 100) milliseconds between events.
     */
    private static double simulationSpeed = 2.5; // How many seconds to sleep between events

    /**
     * Time taken for a car to cross an intersection once started. Default is 10 seconds, but is modified by slider in runtime.
     */
    private static int timeToCrossIntersection = 10; // time to cross intersection once started

    /**
     * Average arrival interval for car groups. Default is 120 seconds, but is modified by user in starting view.
     */
    private static int avgArrivalInterval = 120; // average arrival interval for car groups

    /**
     * Maximum size of car groups arriving. Average is half of that number.
     */
    private static int maxCarGroupSize = 8; // maximum size of car groups arriving, average is half of this

    /**
     * Average reaction time for drivers to react and accelerate when light turns green or other car gives way.
     */
    private static int avgReactionTime = 20; // average time for driver to react and accelerate when light turns green or other car gives way

    /**
     * Time taken for a car to travel between intersections.
     */
    private static int timeBetweenIntersections = 120; // time for car to travel between intersections

    /**
     * Normal distribution for car group sizes arriving at intersections.
     */
    private static Normal carGroupSizeDist = new Normal((double) maxCarGroupSize /2, (double) maxCarGroupSize /2); // average size of car groups arriving

    /**
     * Normal distribution for driver reaction times.
     */
    private static Normal driverReactionTimeDist = new Normal(avgReactionTime, avgReactionTime); // average driver reaction time before starting to pass intersection

    private static Normal carArrivalIntervalDist = new Normal(avgArrivalInterval, avgArrivalInterval); // average arrival interval for car groups

    /**
     * Delay in seconds until the next light change after turning green.
     */
    private int greendelay = 50; // seconds after next light change after turning green

    /**
     * Delay in seconds until the next light change after turning yellow.
     */
    private int yellowdelay = 10; // seconds after next light change after turning yellow

    /**
     * Initializes the simulation by setting traffic light delays and scheduling initial events.
     * Schedules initial QUEUE_ARRIVALS events for both directions and initial LIGHT_CHANGE events for traffic light intersections.
     */
    @Override
    protected void initialize() {
        getTrafficLightController().setGreenDelay(greendelay);
        getTrafficLightController().setYellowDelay(yellowdelay);

        el = eventList;

        // schedule initial QUEUE_ARRIVALS events for direction A
        el.add(new Event(30, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersectionList.get(0), true), "Initial QUEUE_ARRIVALS Event for direction A at " + intersectionList.get(0).getName()));

        // schedule initial QUEUE_ARRIVALS events for direction B

        for (Intersection intersection : intersectionList) {
            el.add(new Event(45, Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(intersection, false), "Initial QUEUE_ARRIVALS Event for direction B at " + intersection.getName()));
        }

        int light_delay = 50;
        for (Intersection intersection : intersectionList) {
            if (intersection instanceof TrafficLightIntersection) {

                //set initial traffic light states
                trafficLightController.setNSGreen((TrafficLightIntersection) intersection);

                // schedule initial traffic light change
                el.add(new Event(light_delay, Event.EventType.LIGHT_CHANGE, new TrafficLightChange(intersection), "Initial Traffic Light Change Event for: " + intersection.getName()));

                light_delay += 25; // stagger initial light changes for multiple intersections
            }
        }

    }

    /**
     * Processes an event based on its type.
     *
     * @param e The event to process.
     */
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

    /**
     * Attempts to start passing intersections for each intersection at the current clock time.
     * This method is called during each C-phase of the simulation.
     */
    @Override
    protected void tryCEvents() {
        // called each C-phase at current clock: allow intersection to start at most one service
        long now = Clock.getInstance().getClock();
        for (Intersection i : intersectionList) {
            i.startPassingIntersection(now, eventList);
        }
//        intersection1.startPassingIntersection(now, eventList);
    }

    /**
     * Outputs the results of the simulation, including total cars sent, total cars arrived,
     * and average pass-through time.
     */
    @Override
    protected void results() {
        System.out.println(" ");
        System.out.println("Simulation finished at " + Clock.getInstance().getClock());
        System.out.println("Total cars sent: " + carsSent);
        System.out.println("Total cars arrived at destination: " + carsArrived);
        System.out.println("Average pass-through time: " + passThroughTimes.stream().mapToLong(Long::longValue).average().orElse(0.0));
    }

    /**
     * Runs the main loop of the simulation with a callback for GUI updates.
     *
     * @param callback A Consumer Event to handle GUI updates after each event is processed.
     */
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

    /**
     * Gets the list of intersections managed by the engine.
     *
     * @return The list of intersections.
     */
    public List<Intersection> getIntersectionList(){
        return intersectionList;
    }

    /**
     * Sets the intersections based on the list of intersection types provided by the homeView.
     *
     * @param intersectionTypeList A list of strings representing the types of intersections to create.
     *                             Valid types are "Bare Intersection", "Traffic Light Intersection", and "Don't show intersection".
     * @throws IllegalArgumentException if the provided list is null or empty.
     */
    // Set intersections based on provided types in start view
    public void setIntersections(List<String> intersectionTypeList) {

        if (intersectionTypeList == null || intersectionTypeList.isEmpty()) {
            throw new IllegalArgumentException("intersectionTypeList cannot be null or empty");
        }

        List<Intersection> tempIntersectionList = new ArrayList<>();

        // Build intersections in reverse order to set 'next' pointers correctly
        for (int i = intersectionTypeList.size() -1; i >= 0; i--) {
            String type = intersectionTypeList.get(i);
            Intersection next = tempIntersectionList.isEmpty() ? null : tempIntersectionList.get(tempIntersectionList.size() -1);
            switch (type) {
                case "Bare Intersection" -> {
                    BareIntersection bareIntersection = new BareIntersection("Intersection-" + (i+1), next, trafficLightController);
                    tempIntersectionList.add(bareIntersection);
                }
                case "Traffic Light Intersection" -> {
                    TrafficLightIntersection trafficLightIntersection = new TrafficLightIntersection("Intersection-" + (i+1), next, trafficLightController);
                    tempIntersectionList.add(trafficLightIntersection);
                }
                default -> {
                    // Skip adding an intersection for "Don't show intersection"
                }
            }
        }
        // Clear the original list and populate it in correct order
        intersectionList.clear();
        for (int i = tempIntersectionList.size() -1; i >= 0; i--) {
            intersectionList.add(tempIntersectionList.get(i));
        }

        // debug: print linkage to verify next pointers
        for (int i = 0; i < intersectionList.size(); i++) {
            Intersection cur = intersectionList.get(i);
            String nextName = (cur.getNext() != null) ? cur.getNext().getName() : "null";
            System.out.println("Built chain: " + cur.getName() + " -> " + nextName);
        }
    }

    /**
     * Sets the paused state of the simulation.
     *
     * @param paused true to pause the simulation, false to resume.
     */
    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            pausedStart = System.currentTimeMillis();
        } else {
            totalPausedDuration += System.currentTimeMillis() - pausedStart;
            notifyAll();
        }
    }

    /**
     * Adds time to the current simulation time.
     * @param addedTime The amount of time to add to the simulation time.
     */
    public static void addSimulationTime(double addedTime) {
        Engine.setSimulationTime(getSimulationTime()+addedTime);
        System.out.println("Remaining simulation time: "+ getRemainingSimulationTime());
    }

    public static int getAvgArrivalInterval() {
        return avgArrivalInterval;
    }

    public static int getMaxCarGroupSize() {
        return maxCarGroupSize;
    }

    /**
     * Gets the car group size normal distribution.
     *
     * @return The Normal distribution for car group sizes.
     */
    public static Normal getCarGroupSizeDist() {
        return carGroupSizeDist;
    }

    /**
     * Sets the car group size normal distribution based on the maximum car group size.
     *
     * @param maxCarGroupSize The maximum size of car groups.
     */
    public static void setCarGroupSizeDist(int maxCarGroupSize) {
        carGroupSizeDist = new Normal((double) maxCarGroupSize /2, (double) maxCarGroupSize /2);
    }

    /**
     * Gets the driver reaction time normal distribution.
     *
     * @return The Normal distribution for driver reaction times.
     */
    public static Normal getDriverReactionTimeDist() {
        return driverReactionTimeDist;
    }

    /**
     * Sets the driver reaction time normal distribution based on the average reaction time.
     *
     * @param avgReactionTime The average reaction time for drivers.
     */
    public static void setDriverReactionTimeDist(int avgReactionTime) {
    	driverReactionTimeDist = new Normal((double)avgReactionTime, (double)avgReactionTime);
    }

    /**
     * Gets the car arrival interval normal distribution.
     *
     * @return The Normal distribution for car arrival intervals.
     */
    public static Normal getCarArrivalIntervalDist() {
        return carArrivalIntervalDist;
    }

    /**
     * Gets the time taken for a car to cross an intersection.
     *
     * @return The time to cross an intersection in seconds.
     */
    public static int getTimeToCrossIntersection() {
        return timeToCrossIntersection;
    }

    /**
     * Sets the normal distribution for car arrival intervals based on the average arrival interval.
     * @param avgArrivalInterval The average interval between car arrivals.
     */
    public static void setCarArrivalIntervalDist(int avgArrivalInterval) {
    	carArrivalIntervalDist = new Normal((double) avgArrivalInterval, (double) avgArrivalInterval);
    }

    /**
     * Gets the time taken for a car to travel between intersections.
     *
     * @return The time between intersections in seconds.
     */
    public static int getTimeBetweenIntersections() {
        return timeBetweenIntersections;
    }

    /**
     * Sets the time taken for a car to travel between intersections.
     * @param timeBetweenIntersections The time between intersections in seconds.
     */
    public static void setTimeBetweenIntersections (int timeBetweenIntersections) {
        IntersectionEngine.timeBetweenIntersections = timeBetweenIntersections;
    }

    /**
     * Sets the simulation speed factor. The simulation sleeps for (simulationSpeed * 100) milliseconds between events.
     *
     * @param simulationSpeed The simulation speed factor (must be greater than 0).
     * @throws IllegalArgumentException if simulationSpeed is less than or equal to 0.
     */
    public static void setSimulationSpeed(double simulationSpeed) {
        if (simulationSpeed <= 0) {
            throw new IllegalArgumentException("simulationSpeed must be > 0");
        }
        IntersectionEngine.simulationSpeed = simulationSpeed;
    }

    /**
     * Sets the total duration of the simulation.
     *
     * @param duration The duration of the simulation in seconds (must be greater than 0).
     * @throws IllegalArgumentException if duration is less than or equal to 0.
     */
    public static void setSimulationDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be > 0");
        }
        System.out.println("Setting simulation duration to " + duration + " seconds.");
        addSimulationTime(duration);
    }

    /**
     * Gets the type of intersection at the specified index in the intersection list.
     *
     * @param index The index of the intersection in the list.
     * @return A string representing the type of intersection ("Traffic Light Intersection", "Bare Intersection", or "Unknown Intersection Type").
     *         Returns null if the index is out of bounds.
     */
    public String getIntersectionTypeAt(int index) {
        if (index < 0 || index >= intersectionList.size()) {
            return null; // or throw IllegalArgumentException
        }
        Intersection intersection = intersectionList.get(index);
        if (intersection instanceof TrafficLightIntersection) {
            return "Traffic Light Intersection";
        } else if (intersection instanceof BareIntersection) {
            return "Bare Intersection";
        } else {
            return "Unknown Intersection Type";
        }
    }

}