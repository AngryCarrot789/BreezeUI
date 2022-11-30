package reghzy.breezeui.core.properties.framework;

public final class FrameworkPropertyMetaFlags {
    public static final int NONE = 0;
    public static final int AFFECTS_LAYOUT = 1;
    public static final int AFFECTS_PARENT_LAYOUT = 2;
    public static final int AFFECTS_RENDER = 4;
    public static final int INHERITS = 8;

    private FrameworkPropertyMetaFlags() {
        throw new UnsupportedOperationException();
    }
}
