package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;

import java.util.LinkedList;

public class TrafficLightIntersection extends  Intersection {

    public final TrafficLight north = new TrafficLight("NORTH");
    public final TrafficLight south = new TrafficLight("SOUTH");
    public final TrafficLight east = new TrafficLight("EAST");
    public final TrafficLight west = new TrafficLight("WEST");

    public TrafficLightIntersection(String name, Intersection next, TrafficLightController controller) {
        super(name, next, controller);
    }

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

    public TrafficLight getNorthLight() { return north; }
    public TrafficLight getSouthLight() { return south; }
    public TrafficLight getEastLight()  { return east;  }
    public TrafficLight getWestLight()  { return west;  }


    public void setNorthLight(TrafficLight north) {
    }

    public void setSouthLight(TrafficLight south) {
    }

    public void setEastLight(TrafficLight east) {
    }

    public void setWestLight(TrafficLight west) {
    }


}
