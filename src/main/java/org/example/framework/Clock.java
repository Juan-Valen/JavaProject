package org.example.framework;

/**
 * Clock for the simulation.
 *
 * <p>Tracks simulation time and provides methods to advance and query the current time.
 */
public class Clock {

    /**
     * The singleton instance of the Clock.
     */
    private static Clock instance;

    /**
     * The current time of the simulation.
     */
    private long clock;

    /**
     * Private constructor to prevent instantiation.
     */
    private Clock() { }

    /**
     * Returns the singleton instance of the Clock.
     * If the instance does not exist, it creates one.
     * @return The Clock instance.
     */
    public static Clock getInstance() {
        if (instance == null)
            instance = new Clock();
        return instance;
    }

    /**
     * Sets the current time of the simulation.
     * @param clock The new time to set.
     */
    public void setClock(long clock) {
        this.clock = clock;
    }

    /**
     * Gets the current time of the simulation.
     * @return The current time.
     */
    public long getClock() {
        return clock;
    }
}
