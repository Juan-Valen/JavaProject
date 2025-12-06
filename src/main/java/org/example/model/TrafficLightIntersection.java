package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;

import java.util.LinkedList;

public class TrafficLightIntersection extends  Intersection {

    public TrafficLightIntersection(String name, Intersection next, long minService, long maxService, TrafficLightController controller) {
        super(name, next, minService, maxService, controller);
    }

    @Override
    public void ChangeTrafficLights(long now, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- ChangeTrafficLights: ------");
        System.out.printf("%s CHANGING TRAFFIC LIGHTS at %.0f \n", name, (double) ClockTime());
        int timeToNext = trafficLightController.changeLights();
        // schedule next light change
        //changeLights returns 0 if light is somehow not red, green, or yellow
        if (timeToNext != 0) {
            eventList.add(new Event(now + timeToNext, Event.EventType.LIGHT_CHANGE, new TrafficLightChange(this), "Traffic Light Change Event for: " + this.name) );
        }
    }

    @Override
    public void startPassingIntersection(long now, EventList eventList) {
        if ((queueA.isEmpty() && queueB.isEmpty()) || busy) return;
        System.out.println(" ");
        System.out.println("---------- startPassingIntersection: ------");
        System.out.printf("%s STARTING TO PASS INTERSECTION at %.0f \n", name, (double) ClockTime());

        // Check which directions are green
        boolean nsGreen = trafficLightController.getNorthState() == TrafficLight.State.GREEN
                || trafficLightController.getSouthState() == TrafficLight.State.GREEN;
        boolean ewGreen = trafficLightController.getEastState() == TrafficLight.State.GREEN
                || trafficLightController.getWestState() == TrafficLight.State.GREEN;

        LinkedList<Car> activeQueue = null;

        if (nsGreen && !queueA.isEmpty()) {
            activeQueue = queueA;
        } else if (ewGreen && !queueB.isEmpty()) {
            activeQueue = queueB;
        }

        if (activeQueue == null) return;

        Car car = activeQueue.removeFirst();
        busy = true;

        long service = minService + rnd.nextInt((int)(maxService - minService));
        long completion = now + service;
        eventList.add(new Event(completion, Event.EventType.DEPARTURE, new Departure(car, nsGreen, this), "Departure after service"));
    }
}
