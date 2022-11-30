package reghzy.breezeui.core.properties;

import reghzy.breezeui.utils.ClassInheritanceMap;
import reghzy.breezeui.utils.ClassUtils;
import reghzy.breezeui.utils.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

public class DependencyProperty {
    private static final HashMap<Class<?>, HashMap<String, DependencyProperty>> PROPERTY_MAP = new HashMap<Class<?>, HashMap<String, DependencyProperty>>();

    public static final Object UNSET_VALUE = new Object();

    private final String name;
    private final Class<?> type;
    private final Class<?> ownerType;
    private final PropertyMeta defaultMeta;
    private final Predicate<Object> validateValue;

    private final ClassInheritanceMap<PropertyMeta> metaMap;

    private final HashSet<DependencyObject> suspended;

    private DependencyProperty(String name, Class<?> type, Class<?> ownerType, PropertyMeta meta, Predicate<Object> validateValue) {
        this.name = name;
        this.type = type;
        this.ownerType = ownerType;
        this.defaultMeta = meta;
        this.validateValue = validateValue;
        this.metaMap = new ClassInheritanceMap<PropertyMeta>();
        this.metaMap.put(ownerType, meta);

        this.suspended = new HashSet<DependencyObject>();
    }

    public static DependencyProperty getProperty(Class<?> ownerType, String propertyName) {
        HashMap<String, DependencyProperty> map = PROPERTY_MAP.get(ownerType);
        return map != null ? map.get(propertyName) : null;
    }

    public static DependencyProperty register(String name, Class<?> type, Class<?> ownerType) {
        return register(name, type, ownerType, null);
    }

    public static DependencyProperty register(String name, Class<?> type, Class<?> ownerType, PropertyMeta defaultMeta) {
        return register(name, type, ownerType, defaultMeta, null);
    }

    public static DependencyProperty register(String name, Class<?> type, Class<?> ownerType, PropertyMeta defaultMeta, Predicate<Object> validateCallback) {
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(type, "Property type cannot be null");
        Validate.notNull(ownerType, "Owner type cannot be null");

        DependencyProperty property;
        HashMap<String, DependencyProperty> classMap = PROPERTY_MAP.get(ownerType);
        if (classMap == null) {
            PROPERTY_MAP.put(ownerType, classMap = new HashMap<String, DependencyProperty>());
        }
        else if ((property = classMap.get(name)) != null) {
            throw new RuntimeException("Property already registered: " + property);
        }

        type = ClassUtils.box(type);
        if (defaultMeta == null) {
            Object def = ClassUtils.getDefaultValue(type);
            if (validateCallback == null || validateCallback.test(def)) {
                defaultMeta = new PropertyMeta(def);
            }
            else {
                throw new RuntimeException("PropertyMeta was null, but validation callback was non-null and does not accept the default value of " + type.getName() + ". PropertyMeta could be auto-generated");
            }
        }

        property = new DependencyProperty(name, type, ownerType, defaultMeta, validateCallback);
        classMap.put(name, property);
        return property;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Class<?> getOwnerType() {
        return this.ownerType;
    }

    public PropertyMeta getMeta(DependencyObject owner) {
        if (owner == null) {
            return this.defaultMeta;
        }

        PropertyMeta meta = this.metaMap.get(owner);
        return meta == null ? this.defaultMeta : meta;
    }

    public void overrideMetadata(Class<?> newOwnerType, PropertyMeta meta) {
        this.metaMap.put(newOwnerType, meta);
    }

    public Predicate<Object> getValidateValueCallback() {
        return this.validateValue;
    }

    public boolean isValueAssignable(Object value) {
        return this.isValueAssignable(value, true);
    }

    public boolean isValueAssignable(Object value, boolean runCustomValidation) {
        if (value == null || value == DependencyProperty.UNSET_VALUE) {
            return true;
        }

        return this.type.isInstance(value) && (!runCustomValidation || this.validateValue == null || this.validateValue.test(value));
    }

    public boolean isOwnerAssignable(DependencyObject value) {
        return this.ownerType.isInstance(value);
    }

    @Override
    public String toString() {
        return this.ownerType.getName() + "::" + this.name;
    }

    /**
     * Suspends property changed notifications being sent to the given object instance
     * @param object The object to suspend
     */
    public void suspend(DependencyObject object) {
        this.suspended.add(object);
    }

    /**
     * Allows property changed notifications to be sent to the given object instance again
     * @param object The object to resume events for
     */
    public void unsuspend(DependencyObject object) {
        this.suspended.remove(object);
    }

    public boolean isSuspended(DependencyObject object) {
        return this.suspended.contains(object);
    }

    public <T> T getValue(DependencyObject object) {
        return object.getValue(this);
    }

    public Object setValue(DependencyObject object, Object value) {
        return object.setValue(this, value);
    }

    public <V> V clearValue(DependencyObject object) {
        return object.clearValue(this);
    }

    public boolean hasValue(DependencyObject object) {
        return object.hasValue(this);
    }
}
