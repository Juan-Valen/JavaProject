package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.LinkedList;
import java.util.Objects;

/**
 * A bare intersection without traffic lights.
 */
public class BareIntersection extends Intersection{
    String lastPassedDirection;

    /**
     * Constructs a BareIntersection.
     * @param name The name of the intersection.
     * @param next The next intersection in the chain.
     * @param controller The traffic light controller for this intersection.
     */
    public BareIntersection(String name, Intersection next, TrafficLightController controller) {
        super(name, next, controller);
    }

    /**
     * Handles the arrival of a car at the intersection.
     * Places the car in the appropriate queue based on its direction.
     * Sets the car's waiting status if necessary.
     * @param a The arrival event.
     */
    public void handleArrival(Arrival a) {

        if (a.fromA) {
            if (!queueA.isEmpty() || (Objects.equals(lastPassedDirection, "B") && busy)) {
                a.car.setWaitingInBareIntersectionQueue(true);
            }
            queueA.addLast(a.car);
        } else {
            if (!queueB.isEmpty() || (Objects.equals(lastPassedDirection, "A") && busy)) {
                a.car.setWaitingInBareIntersectionQueue(true);
            }
            queueB.addLast(a.car);
        }
        System.out.println(" ");
        System.out.println("---------- HandleArrival: ------");
        System.out.printf("%s ARRIVE %s from %s at %.0f \n",
                name, a.car, a.fromA ? "A" : "B", (double) ClockTime());
        System.out.println("QueueA size: " + queueA.size() + ", QueueB size: " + queueB.size());
    }

    /**
     * Starts the process of passing cars through the intersection.
     * Alternates between queues A and B based on the last passed direction.
     * Schedules departure events for cars passing through the intersection.
     * @param now The current time.
     * @param eventList The event list to schedule departure events.
     */
    @Override
    public void startPassingIntersection(long now, EventList eventList) {
        if ((queueA.isEmpty() && queueB.isEmpty()) || busy) return;
        System.out.println(" ");
        System.out.println("---------- startPassingIntersection: ------");
        System.out.printf("%s STARTING TO PASS INTERSECTION at %.0f \n", name, (double) ClockTime());

        Car car = null;

        // determine service time
        long reactionServiceTime = IntersectionEngine.getTimeToCrossIntersection() + (long) IntersectionEngine.getDriverReactionTimeDist().sample();
        long serviceTime = IntersectionEngine.getTimeToCrossIntersection();

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
                System.out.printf("%s START SERVICE %s from " + lastPassedDirection + " at %.0f for %d time units\n", name, car, (double) now, serviceTime);
        } else {
            // both queues have cars, alternate based on last passed direction
            if ("B".equals(lastPassedDirection)) {
                car = queueA.removeFirst();
                lastPassedDirection = "A";
                eventList.add(new Event(now + (car.isWaitingInBareIntersectionQueue() ? reactionServiceTime : serviceTime), Event.EventType.DEPARTURE, new Departure(car, true, this), "Departure of Car from direction A"));

                System.out.printf("%s START SERVICE %s from " + lastPassedDirection + " at %.0f for %d time units\n", name, car, (double) now, (car.isWaitingInBareIntersectionQueue() ? reactionServiceTime : serviceTime));
                car.setWaitingInBareIntersectionQueue(false);

            } else {

                car = queueB.removeFirst();
                lastPassedDirection = "B";
                eventList.add(new Event(now + (car.isWaitingInBareIntersectionQueue() ? reactionServiceTime : serviceTime), Event.EventType.DEPARTURE, new Departure(car, false, this), "Departure of Car from direction B"));

                System.out.printf("%s START SERVICE %s from " + lastPassedDirection + " at %.0f for %d time units\n", name, car, (double) now, (car.isWaitingInBareIntersectionQueue() ? reactionServiceTime : serviceTime));
                car.setWaitingInBareIntersectionQueue(false);
            }
        }
                busy = true;
    }
}
