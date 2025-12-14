package org.example.model;

/**
 * Represents a car in the traffic simulation.
 */
public class Car {

    /** Unique identifier for the car. */
    private int id;

    /** The time when the car started its journey. */
    private long startTime;

    /** The time when the car ended its journey. Only applicable to direction A cars.
     */
    private long endTime;

    /** Indicates if the car is currently waiting at a traffic light. */
    private boolean waitingAtLight = false;

    /** Indicates if the car is waiting in a bare intersection queue. */
    private boolean waitingInBareIntersectionQueue = false;

    /** Constructs a Car with the specified start time.
     * @param startTime The time when the car started its journey.
     */
    public Car(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the unique identifier of the car.
     * @param endTime The time when the car ended its journey.
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the unique identifier of the car.
     * @return The unique identifier of the car.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the car.
     * @param startTime The time when the car started its journey.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Calculates the total time spent by the car from start to end.
     * @return The total time spent by the car.
     */
    public long timeSpent() {
        return endTime - startTime;
    }

    /**
     * Checks if the car is waiting at a traffic light.
     * @return True if the car is waiting at a traffic light, false otherwise.
     */
    public boolean isWaitingAtLight() {
        return waitingAtLight;
    }

    /**
     * Sets the waiting status of the car at a traffic light.
     * @param waitingAtLight True if the car is waiting at a traffic light, false otherwise.
     */
    public void setWaitingAtLight(boolean waitingAtLight) {
        this.waitingAtLight = waitingAtLight;
    }

    /**
     * Checks if the car is waiting in a bare intersection queue.
     * @return True if the car is waiting in a bare intersection queue, false otherwise.
     */
    public boolean isWaitingInBareIntersectionQueue() {
        return waitingInBareIntersectionQueue;
    }

    /**
     * Sets the waiting status of the car in a bare intersection queue.
     * @param waitingInBareIntersectionQueue True if the car is waiting in a bare intersection queue, false otherwise.
     */
    public void setWaitingInBareIntersectionQueue(boolean waitingInBareIntersectionQueue) {
        this.waitingInBareIntersectionQueue = waitingInBareIntersectionQueue;
    }


    /**
     * Generates a string representation of the car.
     * @return A string representing the car.
     */
    @Override
    public String toString() {
        return "Car{id=" + id + "}"; // or include arrival time, etc.
    }
}
