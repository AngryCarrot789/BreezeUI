package reghzy.breezeui.dispatcher;

import java.util.ArrayList;

public class DispatchQueue {
    private final Dispatcher dispatcher;
    private final ArrayList<DispatcherOperation> APP_PRE_TICK;
    private final ArrayList<DispatcherOperation> INPUT_PRE;
    private final ArrayList<DispatcherOperation> INPUT_POST;
    private final ArrayList<DispatcherOperation> RENDER_PRE;
    private final ArrayList<DispatcherOperation> RENDER_POST;
    private final ArrayList<DispatcherOperation> APPLICATION_IDLE;
    private final ArrayList<DispatcherOperation> CONTEXT_IDLE;
    private final ArrayList<DispatcherOperation> APP_POST_TICK;

    public DispatchQueue(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.APP_PRE_TICK = new ArrayList<DispatcherOperation>(32);
        this.INPUT_PRE = new ArrayList<DispatcherOperation>(32);
        this.INPUT_POST = new ArrayList<DispatcherOperation>(32);
        this.RENDER_PRE = new ArrayList<DispatcherOperation>(32);
        this.RENDER_POST = new ArrayList<DispatcherOperation>(32);
        this.APPLICATION_IDLE = new ArrayList<DispatcherOperation>(32);
        this.CONTEXT_IDLE = new ArrayList<DispatcherOperation>(32);
        this.APP_POST_TICK = new ArrayList<DispatcherOperation>(32);
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    private ArrayList<DispatcherOperation> getList(DispatcherPriority priority) {
        switch (priority) {
            case APP_PRE_TICK:     return this.APP_PRE_TICK;
            case INPUT_PRE:        return this.INPUT_PRE;
            case INPUT_POST:       return this.INPUT_POST;
            case RENDER_PRE:       return this.RENDER_PRE;
            case RENDER_POST:      return this.RENDER_POST;
            case APPLICATION_IDLE: return this.APPLICATION_IDLE;
            case CONTEXT_IDLE:     return this.CONTEXT_IDLE;
            case APP_POST_TICK:    return this.APP_POST_TICK;
        }

        throw new IllegalArgumentException("Unknown priority: " + priority);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void enqueue(DispatcherOperation operation) {
        ArrayList<DispatcherOperation> list = getList(operation.getPriority());
        synchronized (list) {
            list.add(operation);
        }
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void process(DispatcherPriority priority) {
        ArrayList<DispatcherOperation> list = getList(priority);
        if (!list.isEmpty()) { // slight performance help :) might break concurrency...
            synchronized (list) {
                this.dispatcher.processOperationList(list);
            }
        }
    }
}
