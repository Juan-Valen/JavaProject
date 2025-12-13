package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;
import org.example.view.HomeView;

import java.util.LinkedList;

public class TrafficLightIntersection extends  Intersection {

    public final TrafficLight north = new TrafficLight("NORTH");
    public final TrafficLight south = new TrafficLight("SOUTH");
    public final TrafficLight east = new TrafficLight("EAST");
    public final TrafficLight west = new TrafficLight("WEST");

    public TrafficLightIntersection(String name, Intersection next, TrafficLightController controller) {

        super(name, next, controller);
        this.north = new TrafficLight("NORTH");
        this.south = new TrafficLight("SOUTH");
        this.east = new TrafficLight("EAST");
        this.west = new TrafficLight("WEST");
    }

    @Override
    public void ChangeTrafficLights(long now, EventList eventList) {
        System.out.println("---------- ChangeTrafficLights: ------");
        System.out.printf("%s CHANGING TRAFFIC LIGHTS at %.0f \n", name, (double) ClockTime());
        int timeToNext = trafficLightController.changeLights(this);
        // schedule next light change
        //changeLights returns 0 if light is somehow not red, green, or yellow
        if (timeToNext != 0) {
            eventList.add(new Event(now + timeToNext, Event.EventType.LIGHT_CHANGE, new TrafficLightChange(this), "Traffic Light Change Event for: " + this.name) );
        }

        // Immediately check if any cars can move after the light change
        eventList.add(new Event(
                now,
                Event.EventType.CHECK_LIGHT,
                this,
                "Check cars after light change"
        ));
    }
    public void changeLights() {

        this.north.setState(controller.getNorthState());
        this.south.setState(controller.getSouthState());
        this.east.setState(controller.getEastState());
        this.west.setState(controller.getWestState());
    }

    public void handleArrival(Arrival a) {

        if (a.fromA) {
            if (!queueA.isEmpty() || trafficLightController.getWestState() == TrafficLight.State.RED) {
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
        if (busy) return;

        // Check which directions are green
        boolean nsGreen = north.getState() == TrafficLight.State.GREEN
                || south.getState() == TrafficLight.State.GREEN;
        boolean ewGreen = east.getState() == TrafficLight.State.GREEN
                || west.getState() == TrafficLight.State.GREEN;

        // NS direction
        if (!queueA.isEmpty() && trafficLightController.getNorthState() == TrafficLight.State.GREEN) {
            processCar(queueA, true, now, eventList);
            moved = true;
        }

        // EW direction
        if (!queueB.isEmpty() && trafficLightController.getEastState() == TrafficLight.State.GREEN) {
            processCar(queueB, false, now, eventList);
            moved = true;
        }

        busy = moved;

        // Retry later if no car could move
        if (!moved) {
            eventList.add(new Event(
                    now + 50,
                    Event.EventType.CHECK_LIGHT,
                    this,
                    "Retry waiting cars"
            ));
        }
    }

    private void processCar(LinkedList<Car> queue, boolean fromA, long now, EventList eventList) {
        Car car = queue.removeFirst();
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


}
