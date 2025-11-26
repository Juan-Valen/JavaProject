package org.example.model;

public class CarGenerator {
    public static void generate(int amount, ServicePoint servicePoint) {
        for (int i = 0; i < amount; i++) {
            servicePoint.addToQueue(new Car(System.currentTimeMillis()));
        }
    }
}
