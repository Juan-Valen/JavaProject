package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.IntersectionEngine;
import org.example.view.HomeView;

import java.util.LinkedList;

public class TrafficLightIntersection extends  Intersection {
    private TrafficLight north;
    private TrafficLight south;
    private TrafficLight east;
    private TrafficLight west;
    private TrafficLightController controller;


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

        // Update light states
        int timeToNext =this.trafficLightController.changeLights();

        // Schedule next light change
        if (timeToNext > 0) {
            eventList.add(new Event(
                    now + timeToNext,
                    Event.EventType.LIGHT_CHANGE,
                    new TrafficLightChange(this),
                    "Traffic Light Change Event for: " + this.name
            ));
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
        EventList eventList = a.eventList;
        eventList.add(new Event(
                ClockTime(),
                Event.EventType.CHECK_LIGHT,
                this,
                "Car arrived, check lights immediately"
        ));

        if (a.fromA) {
            if (!queueA.isEmpty() || trafficLightController.getWestState() == TrafficLight.State.RED) {
                a.car.setWaitingAtLight(true);
            }
            queueA.addLast(a.car);
        } else {
            if (!queueB.isEmpty() || trafficLightController.getEastState() == TrafficLight.State.RED) {
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

        boolean moved = false;

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

        if (car.isWaitingAtLight()) {
            car.setWaitingAtLight(false);
            extra = (long) IntersectionEngine.getDriverReactionTimeDist().sample();
        }

        long crossingTime = timeToPass + extra;

        eventList.add(new Event(crossingTime, Event.EventType.DEPARTURE, new Departure(car, fromA, this),
                "Departure after service"));

        // Update UI immediately or via Platform.runLater() in departure handler
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

    public void setLightSet(HomeView.LightSet lights) {
    }
}
