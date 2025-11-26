package org.example.model;

import java.util.LinkedList;

public class ServicePoint {
    private LinkedList<Car> cars = new LinkedList<>();

    public static void main(String[] args) {
        ServicePoint servicePoint = new ServicePoint();
        System.out.println("Boot");
        CarGenerator.generate(9, servicePoint);
        servicePoint.serve();
        CarGenerator.generate(80, servicePoint);
        servicePoint.serve();
        CarGenerator.generate(20, servicePoint);
        servicePoint.serve();
    }

    public void addToQueue(Car car) {
        cars.addFirst(car);
    }

    public Car removeFromQueue() {
        Car car = cars.removeLast();
        car.setEndTime(System.currentTimeMillis());
        return car;
    }

    public void serve() {
        long sumWait = 0;
        long sum = 0;
        int length = cars.size();
        for (int i = 0; i < length; i++) {
            Car car = removeFromQueue();
            long wait = car.timeSpent();
            car.setStartTime(System.currentTimeMillis());
            try {
                Thread.sleep((int) (Math.random() * 100 + 1));
            } catch (Exception e) {
                System.out.println(e);
            }
            car.setEndTime(System.currentTimeMillis());
            sumWait += wait;
            sum += wait + car.timeSpent();
        }
        System.out.printf(
                "Jobs analitics:\nTotal wait time: %d\nAverage wait time: %d\nTotal response time: %d milliseconds\nAverage resopnse time: %d milliseconds\nTotal service time: %d milliseconds\nAverage service time: %d milliseconds\n",
                sumWait, sumWait / length, sum, sum / length, (sum - sumWait), (sum - sumWait) / length);
    }
}
