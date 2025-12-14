package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.LinkedList;

/**
 * A traffic light controlled intersection.
 */
public class TrafficLightIntersection extends  Intersection {

    /** Traffic lights for each direction */
    public final TrafficLight south = new TrafficLight("SOUTH"),
            north = new TrafficLight("NORTH"),
            east = new TrafficLight("EAST"),
            west = new TrafficLight("WEST");

    /**
     * Constructs a TrafficLightIntersection.
     * @param name The name of the intersection.
     * @param next The next intersection in the chain.
     * @param controller The traffic light controller for this intersection.
     */
    public TrafficLightIntersection(String name, Intersection next, TrafficLightController controller) {
        super(name, next, controller);
    }

    /**
     * Changes the traffic lights at the intersection and schedules the next light change event.
     * @param now The current time.
     * @param eventList The event list to schedule the next light change event.
     */
    @Override
    public void ChangeTrafficLights(long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- ChangeTrafficLights: ------");
        System.out.printf("%s CHANGING TRAFFIC LIGHTS at %.0f \n", name, (double) ClockTime());
        int timeToNext = trafficLightController.changeLights(this);
        // schedule next light change
        //changeLights returns 0 if light is somehow not red, green, or yellow
        if (timeToNext != 0) {
            eventList.add(new Event(now + timeToNext, Event.EventType.LIGHT_CHANGE, new TrafficLightChange(this), "Traffic Light Change Event for: " + this.name) );
        }
    }

    /**
     * Handles the arrival of a car at the intersection.
     * Places the car in the appropriate queue based on its direction.
     * Sets the car's waiting status if necessary.
     * @param a The arrival event.
     */
    @Override
    public void handleArrival(Arrival a) {

        if (a.fromA) {
            if (!queueA.isEmpty() || north.getState() == TrafficLight.State.RED) {
                a.car.setWaitingAtLight(true);
            }
            queueA.addLast(a.car);
        } else {
            if (!queueB.isEmpty() || east.getState() == TrafficLight.State.RED) {
                a.car.setWaitingAtLight(true);
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
     * Starts the process of passing cars through the intersection based on the traffic light states.
     * Schedules departure events for cars passing through the intersection.
     * Sets the intersection as busy while a car is passing through.
     * @param now The current time.
     * @param eventList The event list to schedule departure events.
     */
    @Override
    public void startPassingIntersection(long now, EventList eventList) {
        if ((queueA.isEmpty() && queueB.isEmpty()) || busy) return;
        System.out.println(" ");
        System.out.println("---------- startPassingIntersection: ------");
        System.out.printf("%s STARTING TO PASS INTERSECTION at %.0f \n", name, (double) ClockTime());

        // Check which directions are green

        boolean nsGreen = north.getState() == TrafficLight.State.GREEN
                || south.getState() == TrafficLight.State.GREEN;
        boolean ewGreen = east.getState() == TrafficLight.State.GREEN
                || west.getState() == TrafficLight.State.GREEN;

        LinkedList<Car> activeQueue = null;

        if (nsGreen && !queueA.isEmpty()) {
            activeQueue = queueA;
        } else if (ewGreen && !queueB.isEmpty()) {
            activeQueue = queueB;
        }

        if (activeQueue == null) return;

        Car car = activeQueue.removeFirst();
        busy = true;

        long timeToPass = now + IntersectionEngine.getTimeToCrossIntersection();
        long extra = 0;
        long crossingTime;

        if (car.isWaitingAtLight()) {
            car.setWaitingAtLight(false);
            extra = (long) IntersectionEngine.getDriverReactionTimeDist().sample();
        }

        if (extra > 0) {
            crossingTime = timeToPass + extra;
        }
        else {
            crossingTime = timeToPass;
        }

        eventList.add(new Event(crossingTime, Event.EventType.DEPARTURE, new Departure(car, nsGreen, this), "Departure after service"));
    }

    /**
     * Gets the traffic light for the north direction.
     * @return The traffic light for the north direction.
     */
    public TrafficLight getNorthLight() { return north; }

    /**
     * Gets the traffic light for the south direction.
     * @return The traffic light for the south direction.
     */
    public TrafficLight getSouthLight() { return south; }

    /**
     * Gets the traffic light for the east direction.
     * @return The traffic light for the east direction.
     */
    public TrafficLight getEastLight()  { return east;  }

    /**
     * Gets the traffic light for the west direction.
     * @return The traffic light for the west direction.
     */
    public TrafficLight getWestLight()  { return west;  }
}
