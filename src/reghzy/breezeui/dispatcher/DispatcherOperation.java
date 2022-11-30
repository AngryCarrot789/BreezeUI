package reghzy.breezeui.dispatcher;

import reghzy.breezeui.Application;

public class DispatcherOperation implements Comparable<DispatcherOperation> {
    public static boolean IS_DEBUG_MODE = true;

    private final Dispatcher dispatcher;
    private final DispatcherPriority priority;
    private final Runnable runnable;

    private int status;

    private volatile boolean aborted;

    StackTraceElement[] creationTrace;

    public DispatcherOperation(Dispatcher dispatcher, DispatcherPriority priority, Runnable runnable) {
        this.dispatcher = dispatcher;
        this.priority = priority;
        this.runnable = runnable;

        if (IS_DEBUG_MODE) {
            this.creationTrace = new Throwable().getStackTrace();
        }

        reset(true);
    }

    public static DispatcherOperation of(Runnable runnable, DispatcherPriority priority) {
        return new DispatcherOperation(Application.current().getDispatcher(), priority, runnable);
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    public DispatcherPriority getPriority() {
        return this.priority;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public int getStatus() {
        return this.status;
    }

    public void abort() {
        this.aborted = true;
    }

    public boolean isAborted() {
        return this.aborted;
    }

    void invoke() {
        this.status = DispatcherOperationStatus.EXECUTING;
        try {
            this.runnable.run();
            if (this.aborted) {
                this.status = DispatcherOperationStatus.ABORTED;
            }
            else {
                this.status = DispatcherOperationStatus.COMPLETED_SUCCESS;
            }
        }
        catch (Throwable e) {
            this.status = DispatcherOperationStatus.COMPLETED_FAILED;
            throw e;
        }
    }

    @Override
    public int compareTo(DispatcherOperation operation) {
        return Integer.compare(this.priority.getPriority(), operation.priority.getPriority());
    }

    public void reset(boolean constructor) {
        this.aborted = false;
        this.status = DispatcherOperationStatus.PENDING;
    }
}
