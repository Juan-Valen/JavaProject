package org.example.framework;

public class Clock {
    private static Clock instance;
    private long clock;

    private Clock() { }

    public static Clock getInstance() {
        if (instance == null)
            instance = new Clock();
        return instance;
    }

    public void setClock(long clock) {
        this.clock = clock;
    }

    public long getClock() {
        return clock;
    }
}
