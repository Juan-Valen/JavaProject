package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.distributions.Normal;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;

/**
 * Superclass for intersections
 */
public class Intersection {

    /**
     *
     * The name of the intersection.
     */
    protected final String name;

    /**
     *
     * Queue for direction A, the main direction.
     */
    protected final LinkedList<Car> queueA = new LinkedList<>(); // queue for direction A, the main direction

    /**
     *
     * Queue for direction B, the secondary blocking direction.
     */
    protected final LinkedList<Car> queueB = new LinkedList<>(); // queue for direction B, the secondary blocking direction

    /**
     *
     * Indicates if the intersection is currently busy, meaning a car is passing through.
     */
    protected boolean busy = false; // service in progress

    /**
     *
     * The next intersection in the chain, can be null if this is the last intersection.
     */
    protected final Intersection next; // next intersection in chain, nullable

    /***
     * The traffic light controller for this intersection.
     */
    protected TrafficLightController trafficLightController;

    /**
     * Constructs an Intersection.
     * @param name The name of the intersection.
     * @param next The next intersection in the chain.
     * @param controller The traffic light controller for this intersection.
     */
    public Intersection(String name, Intersection next, TrafficLightController controller) {
        this.name = name;
        this.next = next;
        this.trafficLightController = controller;
    }

    /**
     * Handles the arrival of a car at the intersection. Adds the car to the appropriate queue based on its direction.
     * @param a The arrival event containing the car and its direction.
     */
    public void handleArrival(Arrival a) {
        if (a.fromA) {
            queueA.addLast(a.car);
        } else {
            queueB.addLast(a.car);
        }
        System.out.println(" ");
        System.out.println("---------- HandleArrival: ------");
        System.out.printf("%s ARRIVE %s from %s at %.0f \n",
                 name, a.car, a.fromA ? "A" : "B", (double) ClockTime());
        System.out.println("QueueA size: " + queueA.size() + ", QueueB size: " + queueB.size());
    }

    /**
     * Completes the service of a car at the intersection. Routes the car to the next intersection if applicable and marks the intersection as not busy.
     * @param d The departure event containing the car and its direction.
     * @param now The current simulation time.
     * @param eventList The event list to schedule future events.
     */
    public void completeService(Departure d, long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- CompleteService: ------");
        System.out.printf("%s COMPLETE %s from %s to %s at %.0f \n", name, d.car, d.fromA ? "A" : "B",next != null ? next.getName() : "", (double) now);
        // route to next intersection (instant arrival at same simulated time)
        // B direction cars are absorbed and do not continue to next intersection
        if (next != null && d.fromA) {
            eventList.add(new Event(now + IntersectionEngine.getTimeBetweenIntersections(), Event.EventType.ARRIVAL, new Arrival(d.car, d.fromA, next), "Arrival from previous intersection into next intersection")); // assume next intersection treats all as direction A
        } else if (next == null && d.fromA) {
            IntersectionEngine.getCarsArrived().incrementAndGet();
            IntersectionEngine.addPassThroughTime(d.car.getStartTime());
        }
        // mark service done and flip green (alternate queues)
        busy = false;
        System.out.println("marking " + name + " not busy");
    }

    /**
     * Changes the traffic lights at the intersection. This method is intended to be overridden in subclasses.
     * @param now The current simulation time.
     * @param eventList The event list to schedule future events.
     */
    public void ChangeTrafficLights(long now, EventList eventList) {
        // this does nothing in the superclass and is overridden in TrafficLightIntersection
        // I don't know
    }

    /**
     * Queues car arrivals at the intersection based on a normal distribution for car group size.
     * Schedules the next QUEUE_ARRIVALS event.
     * @param qa The QueueArrivals event containing the direction of arrivals.
     * @param now The current simulation time.
     * @param eventList The event list to schedule future events.
     */
    public void queueArrivals(QueueArrivals qa,long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- QueueArrivals: ------");
        System.out.printf("%s QUEUE ARRIVALS at %.0f \n", name, (double) ClockTime());
        // add arrivals
        // use normal distribution to determine number of arrivals, get from IntersectionEngine max car group size
        Normal dist = IntersectionEngine.getCarGroupSizeDist();
        for (int i = 0; i < round(dist.sample()); i++) {
            eventList.add(new Event(now+i, Event.EventType.ARRIVAL, new Arrival(new Car(now+i), qa.queueA, this), "Arrival of Car at time: " + (now+i) + " to intersection " + name + " from direction B"));
            if(this.getName().equals("Intersection-4")){

            }
            if (qa.queueA) {
                IntersectionEngine.getCarsSent().incrementAndGet();
            }
        }

        // schedule next QUEUE_ARRIVALS event
        eventList.add(new Event(now + ((long)IntersectionEngine.getCarArrivalIntervalDist().sample()), Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(this, qa.queueA), "Next Queue Arrivals Event for: " + name));
    }

    // Called in C-phase for the current simulation time; if possible start one service and schedule DEPARTURE
    /**
     * Starts the process of a car passing through the intersection if there are cars waiting and the intersection is not busy.
     * This method is intended to be overridden in subclasses.
     * @param now The current simulation time.
     * @param eventList The event list to schedule future events.
     */
    public void startPassingIntersection(long now, EventList eventList) {
        // this does nothing in the superclass and is overridden in TrafficLightIntersection
    }

    /**
     * Gets the current simulation clock time.
     * @return The current clock time.
     */
    protected long ClockTime() {
        return org.example.framework.Clock.getInstance().getClock();
    }

    /**
     * Gets the queue for direction A.
     * @return The list of cars in queue A.
     */
    public List<Car> getQueueA(){
        return queueA;
    }

    /**
     * Gets the queue for direction B.
     * @return The list of cars in queue B.
     */
    public List<Car> getQueueB(){
        return queueB;
    }

    /**
     * Gets the next intersection in the chain.
     * @return The next intersection, or null if this is the last intersection.
     */
    public Intersection getNext() {
        return next;
    }

    /**
     * Gets the name of the intersection.
     * @return The name of the intersection.
     */
    public String getName() {
        return name;
    }

}