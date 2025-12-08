package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.distributions.Normal;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.*;

import static java.lang.Math.round;

public class Intersection {
    protected final String name;
    protected final LinkedList<Car> queueA = new LinkedList<>(); // queue for direction A, the main direction
    protected final LinkedList<Car> queueB = new LinkedList<>(); // queue for direction B, the secondary blocking direction
    protected boolean greenA = true; // which direction is allowed to start service, ie which queue has the green light
    protected boolean busy = false; // service in progress
    protected final Intersection next; // next intersection in chain, nullable
    protected final Random rnd = new Random();
    protected final Normal normalDist = null;
    protected final long minService;
    protected final long maxService;

    protected TrafficLightController trafficLightController;


    public Intersection(String name, Intersection next, long minService, long maxService, TrafficLightController controller) {
        this.name = name;
        this.next = next;
        this.minService = minService;
        this.maxService = maxService;
        this.trafficLightController = controller;
    }

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

    public void completeService(Departure d, long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- CompleteService: ------");
        System.out.printf("%s COMPLETE %s from %s to %s at %.0f \n", name, d.car, d.fromA ? "A" : "B",next != null ? next.getName() : "", (double) now);
        // route to next intersection (instant arrival at same simulated time)
        // B direction cars are absorbed and do not continue to next intersection
        if (next != null && d.fromA) {
            eventList.add(new Event(now, Event.EventType.ARRIVAL, new Arrival(d.car, d.fromA, next), "Arrival from previous intersection into next intersection")); // assume next intersection treats all as direction A
        } else if (next == null && d.fromA) {
            IntersectionEngine.setCarsArrived(IntersectionEngine.getCarsArrived()+1);
            IntersectionEngine.addPassThroughTime(d.car.getStartTime());
        }
        // mark service done and flip green (alternate queues)
        busy = false;
        System.out.println("marking " + name + " not busy");
    }

    public void ChangeTrafficLights(long now, EventList eventList) {
        // this does nothing in the superclass and is overridden in TrafficLightIntersection
        // I don't know
    }

    public void queueArrivals(QueueArrivals qa,long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- QueueArrivals: ------");
        System.out.printf("%s QUEUE ARRIVALS at %.0f \n", name, (double) ClockTime());
        // add arrivals
        // use normal distribution to determine number of arrivals, get from IntersectionEngine max car group size
        Normal dist = IntersectionEngine.getCarGroupSizeDist();
        for (int i = 0; i < round(dist.sample()); i++) {
            eventList.add(new Event(now+i, Event.EventType.ARRIVAL, new Arrival(new Car(now+i), qa.queueA, this), "Arrival of Car at time: " + (now+i) + " to intersection " + name + " from direction B"));

            if (qa.queueA) {
                IntersectionEngine.setCarsSent(IntersectionEngine.getCarsSent()+1);
            }
        }

        // schedule next QUEUE_ARRIVALS event
        eventList.add(new Event(now + ((long)IntersectionEngine.getCarArrivalIntervalDist().sample()), Event.EventType.QUEUE_ARRIVALS, new QueueArrivals(this, qa.queueA), "Next Queue Arrivals Event for: " + name));

    }

    // Called in C-phase for the current simulation time; if possible start one service and schedule DEPARTURE
    public void startPassingIntersection(long now, EventList eventList) {
        // this does nothing in the superclass and is overridden in TrafficLightIntersection
    }



    protected long ClockTime() {
        return org.example.framework.Clock.getInstance().getClock();
    }


    public Map<String, Integer> getQueueStates() {
        Map<String, Integer> queues = new HashMap<>();
        queues.put("DirectionA", queueA.size());
        queues.put("DirectionB", queueB.size());
        return queues;
    }


    public TrafficLightController getTrafficLights() {
        return trafficLightController;
    }

    public List<Car> getQueueA(){
        return queueA;
    }
    public List<Car> getQueueB(){
        return queueB;
    }

    public Intersection getNext() {
        return next;
    }

    public String getName() {
        return name;
    }

}