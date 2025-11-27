
package org.example.framework;

import org.example.model.ServicePoint;

public class TrafficLightEvent implements EventType {
    private double timestamp;
    private long duration;
    private String state; // RED / GREEN
    private ServicePoint servicePoint;

    public TrafficLightEvent(double timestamp, long duration, String state, ServicePoint servicePoint) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.state = state;
        this.servicePoint = servicePoint;
    }

    public String getName() {
        return "TrafficLightEvent";
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void execute() {
        if ("RED".equals(state)) {
            System.out.println("Traffic light RED: Pausing cars for " + duration + " ms");
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Traffic light GREEN: Cars can move");
            servicePoint.serve();
        }
    }

}
