package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.LinkedList;

public class BareIntersection extends Intersection{
    String lastPassedDirection;


    public BareIntersection(String name, Intersection next, TrafficLightController controller) {
        super(name, next, controller);
    }

    @Override
    public void startPassingIntersection(long now, EventList eventList) {
        if ((queueA.isEmpty() && queueB.isEmpty()) || busy) return;
        System.out.println(" ");
        System.out.println("---------- startPassingIntersection: ------");
        System.out.printf("%s STARTING TO PASS INTERSECTION at %.0f \n", name, (double) ClockTime());

        Car car = null;

        // determine service time
        long serviceTime = IntersectionEngine.getTimeToCrossIntersection() + (long) IntersectionEngine.getDriverReactionTimeDist().sample();

        // if one of the queues is empty, serve from the other
        if (queueA.isEmpty() || queueB.isEmpty()) {
            // time it takes to pass the intersection
            // serve from non-empty queue
            if (!queueA.isEmpty()) {
                car = queueA.removeFirst();
                lastPassedDirection = "A";
                eventList.add(new Event(now + serviceTime, Event.EventType.DEPARTURE, new Departure(car, true, this), "Departure of Car from direction A"));
            } else if (!queueB.isEmpty()) {
                lastPassedDirection = "B";
                car = queueB.removeFirst();
                eventList.add(new Event(now + serviceTime, Event.EventType.DEPARTURE, new Departure(car, false, this), "Departure of Car from direction B"));
            }
        } else {
            // both queues have cars, alternate based on last passed direction
            if ("B".equals(lastPassedDirection)) {
                car = queueA.removeFirst();
                lastPassedDirection = "A";
                eventList.add(new Event(now + serviceTime, Event.EventType.DEPARTURE, new Departure(car, true, this), "Departure of Car from direction A"));
            } else {
                car = queueB.removeFirst();
                lastPassedDirection = "B";
                eventList.add(new Event(now + serviceTime, Event.EventType.DEPARTURE, new Departure(car, false, this), "Departure of Car from direction B"));
            }
        }
                busy = true;
                System.out.printf("%s START SERVICE %s from " + lastPassedDirection + " at %.0f for %d time units\n", name, car, (double) now, serviceTime);
    }
}
