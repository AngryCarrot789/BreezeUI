package reghzy.breezeui.dispatcher;

public class DispatcherOperationStatus {
    public static final int PENDING           = 0b0000000000000001;
    public static final int ABORTED           = 0b0000000000000010;
    public static final int COMPLETED         = 0b0000000000000100;
    public static final int COMPLETED_SUCCESS = 0b0000000000001100;
    public static final int COMPLETED_FAILED  = 0b0000000000010100;
    public static final int EXECUTING         = 0b0000000000100000;
}
