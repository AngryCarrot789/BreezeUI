package reghzy.breezeui.dispatcher;

public enum DispatcherPriority {
    /**
     * Operations are processed as soon as possible, before any other priorities.
     * This is processed as soon as the application wakes back up
     */
    APP_PRE_TICK(0),

    /**
     * Called after inputs are processed
     */
    INPUT_PRE(1),

    /**
     * Called after inputs are processed
     */
    INPUT_POST(2),

    /**
     * Operations are called after rendering is finished
     */
    RENDER_PRE(3),

    /**
     * Operations are called just before any rendering is done
     */
    RENDER_POST(4),

    /**
     * The normal priority for operations, called once
     * the app tick has finished all important stuff
     */
    APPLICATION_IDLE(5),

    /**
     * A post-idle operation priority
     */
    CONTEXT_IDLE(6),

    /**
     * Operations are processed just before the application goes back into a
     * waiting state, waiting for new events to be processed
     */
    APP_POST_TICK(7),
    ;

    private final int priority;
    DispatcherPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}
