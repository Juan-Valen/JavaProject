package org.example.model;

public class Car {
    private int id;
    private long startTime;
    private long endTime;
    static private int next = 0;
    private boolean waitingAtLight = false;
    private boolean waitingInBareIntersectionQueue = false;
    private int timesMoved = 0;

    public static void main(String[] args) {
        Car car = new Car(System.currentTimeMillis());
        try {
            Thread.sleep(80);
        } catch (Exception e) {
            System.out.println(e);
        }
        car.setEndTime(System.currentTimeMillis());
        System.out.printf("Create customer #%d\n", car.getId());
        System.out.printf("Update customer #%d end time\n", car.getId());
        System.out.printf("%d millisecond to execute task\n", car.timeSpent());

        Car car1 = new Car(System.currentTimeMillis());
        try {
            Thread.sleep(555);
        } catch (Exception e) {
            System.out.println(e);
        }
        car1.setEndTime(System.currentTimeMillis());
        System.out.printf("Create customer #%d\n", car1.getId());
        System.out.printf("Update customer #%d end time\n", car1.getId());
        System.out.printf("%d millisecond to execute task\n", car1.timeSpent());

    }

    public Car(long startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long timeSpent() {
        return endTime - startTime;
    }

    public boolean isWaitingAtLight() {
        return waitingAtLight;
    }

    public void setWaitingAtLight(boolean waitingAtLight) {
        this.waitingAtLight = waitingAtLight;
    }

    public boolean isWaitingInBareIntersectionQueue() {
        return waitingInBareIntersectionQueue;
    }

    public void setWaitingInBareIntersectionQueue(boolean waitingInBareIntersectionQueue) {
        this.waitingInBareIntersectionQueue = waitingInBareIntersectionQueue;
    }


    @Override
    public String toString() {
        return "Car{id=" + id + "}"; // or include arrival time, etc.
    }

    public int getTimesMoved() {
        return timesMoved;
    }

    public void incrementTimesMoved() {
        this.timesMoved += 1;
    }
}
