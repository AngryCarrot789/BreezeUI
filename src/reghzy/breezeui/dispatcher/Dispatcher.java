package reghzy.breezeui.dispatcher;

import reghzy.breezeui.Application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dispatcher {
    private final Thread thread;
    private final DispatchQueue queue;

    public Object temp1;
    public Object temp2;
    public Object temp3;
    public Object temp4;
    public Object temp5;
    public Object temp6;
    public Object temp7;

    public Dispatcher() {
        this.queue = new DispatchQueue(this);
        this.thread = Thread.currentThread();
    }

    public Thread getThread() {
        return this.thread;
    }

    public DispatchQueue getQueue() {
        return this.queue;
    }

    public DispatcherOperation invoke(Runnable runnable) {
        return this.invoke(runnable, DispatcherPriority.APP_PRE_TICK);
    }

    public DispatcherOperation invoke(Runnable runnable, DispatcherPriority priority) {
        DispatcherOperation operation = new DispatcherOperation(this, priority, runnable);
        if (priority == DispatcherPriority.APP_PRE_TICK && this.isOnOwningThread()) {
            operation.invoke();
        }
        else {
            this.queue.enqueue(operation);
        }

        return operation;
    }

    public DispatcherOperation invoke(DispatcherOperation operation) {
        if (operation != null) {
            if (operation.getStatus() != DispatcherOperationStatus.PENDING) {
                operation.reset(false);
            }

            this.queue.enqueue(operation);
            return operation;
        }
        else {
            throw new IllegalArgumentException("Operation cannot be null");
        }
    }

    /**
     * Returns whether the thread that called this code is the same as the thread that owns this dispatcher
     */
    public boolean isOnOwningThread() {
        return Thread.currentThread() == this.thread;
    }

    public static void requestProcessing() {
        Application.pushMessage(null);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    void processOperationList(ArrayList<DispatcherOperation> list) {
        LinkedHashMap<DispatcherOperation, Throwable> errors = null;
        for (int i = 0, size = list.size(); i < size; i++) {
            DispatcherOperation operation = list.get(i);
            try {
                operation.invoke();
            }
            catch (Throwable e) {
                if (errors == null) {
                    errors = new LinkedHashMap<DispatcherOperation, Throwable>();
                }

                errors.put(operation, e);
            }
        }

        if (errors != null) {
            RuntimeException error = new RuntimeException("Failed to invoke 1 or more operation");
            for (Map.Entry<DispatcherOperation, Throwable> entry : errors.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to invoke operation ").append(entry.getKey()).append("\n");
                if (entry.getKey().creationTrace != null) {
                    sb.append("Creation stack trace: \n");
                    for (StackTraceElement element : entry.getKey().creationTrace) {
                        sb.append("    ").append(element.toString()).append('\n');
                    }
                }

                error.addSuppressed(new RuntimeException(sb.toString()));
            }

            throw error;
        }
    }
}
