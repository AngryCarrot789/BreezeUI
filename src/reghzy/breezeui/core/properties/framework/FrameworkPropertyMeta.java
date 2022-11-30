package reghzy.breezeui.core.properties.framework;

import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.properties.CoerceValueCallback;
import reghzy.breezeui.core.properties.PropertyChangedCallback;

public class FrameworkPropertyMeta extends PropertyMeta {
    private static final int DEFAULT_FLAGS = FrameworkPropertyMetaFlags.NONE;

    private final int flags;

    public FrameworkPropertyMeta() {
        this(DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(Object defaultValue) {
        this(defaultValue, DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(PropertyChangedCallback onPropertyChanged) {
        this(onPropertyChanged, DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged) {
        this(defaultValue, onPropertyChanged, DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue) {
        this(onPropertyChanged, onCoerceValue, DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue) {
        this(defaultValue, onPropertyChanged, onCoerceValue, DEFAULT_FLAGS);
    }

    public FrameworkPropertyMeta(int flags) {
        this.flags = flags;
        init();
    }

    public FrameworkPropertyMeta(Object defaultValue, int flags) {
        super(defaultValue);
        this.flags = flags;
        init();
    }

    public FrameworkPropertyMeta(PropertyChangedCallback onPropertyChanged, int flags) {
        super(onPropertyChanged);
        this.flags = flags;
        init();
    }

    public FrameworkPropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged, int flags) {
        super(defaultValue, onPropertyChanged);
        this.flags = flags;
        init();
    }

    public FrameworkPropertyMeta(PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue, int flags) {
        super(onPropertyChanged, onCoerceValue);
        this.flags = flags;
        init();
    }

    public FrameworkPropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue, int flags) {
        super(defaultValue, onPropertyChanged, onCoerceValue);
        this.flags = flags;
        init();
    }

    public static Object of(Object defaultValue) {
        return null;
    }

    protected void init() {

    }

    public boolean canAffectRender() {
        return (this.flags & FrameworkPropertyMetaFlags.AFFECTS_RENDER) != 0;
    }

    public boolean canAffectLayout() {
        return (this.flags & FrameworkPropertyMetaFlags.AFFECTS_LAYOUT) != 0;
    }

    public boolean canAffectParentLayout() {
        return (this.flags & FrameworkPropertyMetaFlags.AFFECTS_PARENT_LAYOUT) != 0;
    }
}
