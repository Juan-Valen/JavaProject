package org.example.framework;


public abstract class Engine {
    private static final String RED = "\033[0;31m"; // https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    private static final String WHITE = "\033[0;37m"; // ANSI escape code for white color
    private static double simulationTime = 0;
    protected EventList eventList;

    public Engine() {
        eventList = new EventList();
        // Service Points are created in the subclass
    }

    public static void setSimulationTime(double simulationTime) {
        Engine.simulationTime = simulationTime;
    }

    public static double getSimulationTime() {
        return simulationTime;
    }

    public static long getRemainingSimulationTime() {
        return (long)Math.max(0, getSimulationTime() - Clock.getInstance().getClock());
    }


    public void run() {
        initialize();

        while (simulate()) {
            System.out.printf("\n%sA-phase:%s time is %.2f\n", RED, WHITE, currentTime());
            Clock.getInstance().setClock((long)currentTime());

            System.out.printf("%sB-phase:%s ", RED, WHITE);
            runBEvents();

            System.out.printf("%sC-phase:%s ", RED, WHITE);
            tryCEvents();
        }

        results();
    }

    private boolean simulate() {
        return Clock.getInstance().getClock() < simulationTime;
    }

    private double currentTime() {
        return eventList.getNextEventTime();
    }

    private void runBEvents() {
        while (eventList.getNextEventTime() == Clock.getInstance().getClock()) {
            runEvent(eventList.remove());
        }
    }

    protected abstract void initialize();
    protected abstract void runEvent(Event e);
    protected abstract void tryCEvents();
    protected abstract void results();
}
