package org.example.framework;

/**
 * Abstract Engine class for simulation framework.
 *
 * <p>Manages the simulation loop, event processing, and phases of the simulation.
 */
public abstract class Engine {

    /**
     *  ANSI escape code for red color. Used for console output formatting.
     */
    private static final String RED = "\033[0;31m"; // https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797

    /**
     * ANSI escape code for white color. Used for console output formatting.
     */
    private static final String WHITE = "\033[0;37m"; // ANSI escape code for white color

    /** Total simulation time limit. */
    private static double simulationTime = 0;

    /**
     * Event list managing scheduled events in the simulation.
     */
    protected EventList eventList;

    /**
     * Constructor initializes the event list.
     */
    public Engine() {
        eventList = new EventList();
        // Service Points are created in the subclass
    }

    /** Sets the total simulation time limit.
     * @param simulationTime The total simulation time.
     */
    public static void setSimulationTime(double simulationTime) {
        Engine.simulationTime = simulationTime;
    }

    /** Gets the total simulation time limit.
     * @return The total simulation time.
     */
    public static double getSimulationTime() {
        return simulationTime;
    }

    /** Starts the simulation engine.
     * Manages the initialization, event processing loop, and results generation.
     * Overridden in IntersectionEngine.
     */
    protected abstract void initialize();

    /**
     * Processes a single event in the simulation.
     * Overridden in IntersectionEngine.
     * @param e The event to process.
     */
    protected abstract void runEvent(Event e);

    /**
     * Attempts to process C-phase events in the simulation.
     * Overridden in IntersectionEngine.
     */
    protected abstract void tryCEvents();

    /**
     * Generates and outputs the results of the simulation.
     * Overridden in IntersectionEngine.
     */
    protected abstract void results();

}
