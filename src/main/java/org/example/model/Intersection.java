package org.example.model;

import org.example.controller.TrafficLightController;
import org.example.framework.Event;
import org.example.framework.EventList;

import java.util.*;

public class Intersection {
    private final String name;
    private final LinkedList<Car> queueA = new LinkedList<>(); // queue for direction A, the main direction
    private final LinkedList<Car> queueB = new LinkedList<>(); // queue for direction B, the secondary blocking direction
    private boolean greenA = true; // which direction is allowed to start service, ie which queue has the green light
    private boolean busy = false; // service in progress
    private final Intersection next; // next intersection in chain, nullable
    private final Random rnd = new Random();
    private final long minService;
    private final long maxService;

    private TrafficLightController trafficLightController;


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
        System.out.printf("%s ARRIVE %s from %s at %.0f ms\n",
                 name, a.car, a.fromA ? "A" : "B", (double) ClockTime());
    }

    public void completeService(Departure d, long completionTime, EventList eventList) {
        System.out.println(" ");
        System.out.println("---------- CompleteService: ------");
        System.out.printf("%s COMPLETE %s from %s at %.0f ms\n",  name, d.car, d.fromA ? "A" : "B",(double) completionTime);
        // route to next intersection (instant arrival at same simulated time)
        if (next != null) {
            eventList.add(new Event(completionTime, Event.EventType.ARRIVAL, new Arrival(d.car, true), "Arrival from previous intersection into next intersection")); // assume next intersection treats all as direction A
        }
        // mark service done and flip green (alternate queues)
        busy = false;
        greenA = !greenA;
    }

    // Called in C-phase for the current simulation time; if possible start one service and schedule DEPARTURE



    public void tryStartService(long now, EventList eventList) {
        if (busy) return;

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
        eventList.add(new Event(completion, Event.EventType.DEPARTURE, new Departure(car, nsGreen), "Departure after service"));
    }



    private long ClockTime() {
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




}