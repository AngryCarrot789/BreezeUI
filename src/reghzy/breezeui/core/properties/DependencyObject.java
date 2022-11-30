package reghzy.breezeui.core.properties;

import reghzy.breezeui.utils.ClassUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.function.Predicate;

public class DependencyObject {
    private final HashMap<DependencyProperty, Object> propertyMap;

    protected DependencyObject() {
        this.propertyMap = new HashMap<DependencyProperty, Object>();
    }

    public static DependencyProperty registerDP(String name, Class<?> type, Class<?> ownerType) {
        return DependencyProperty.register(name, type, ownerType);
    }

    public static DependencyProperty registerDP(String name, Class<?> type, Class<?> ownerType, PropertyMeta meta) {
        return DependencyProperty.register(name, type, ownerType, meta);
    }

    public static DependencyProperty registerDP(String name, Class<?> type, Class<?> ownerType, PropertyMeta meta, Predicate<Object> validateCallback) {
        return DependencyProperty.register(name, type, ownerType, meta, validateCallback);
    }

    public <V> V getValue(DependencyProperty property) {
        if (property.isOwnerAssignable(this)) {
            if (this.propertyMap.containsKey(property)) {
                return (V) this.propertyMap.get(property);
            }
            else {
                Object value = property.getMeta(this).getDefaultValue(property, this);
                if (property.isValueAssignable(value)) {
                    this.propertyMap.put(property, value);
                    return (V) value;
                }
                else {
                    throw new RuntimeException(property + "'s metadata provided an invalid default value: " + value);
                }
            }
        }
        else {
            throw new IllegalArgumentException("Current DependencyObject instance (" + this.getClass().getName() + ") is not suitable for property: " + property);
        }
    }

    public Object setValue(DependencyProperty property, Object value) {
        boolean valid = false;
        Class<?> unboxedType = ClassUtils.unbox(property.getType());
        if (unboxedType.isPrimitive()) {
            Object converted = ClassUtils.convertPrimitiveTo(value, unboxedType);
            if (property.isValueAssignable(converted)) {
                value = converted;
                valid = true;
            }
        }

        if (valid || property.isValueAssignable(value)) {
            PropertyMeta meta = property.getMeta(this);
            Object oldValue = this.propertyMap.get(property);
            Object newValue = meta.getCoerceValue(property, this, value);
            raisePropertyChanged(property, oldValue, newValue);
            this.propertyMap.put(property, newValue);
            return oldValue;
        }
        else {
            throw new RuntimeException(MessageFormat.format("{0} (of type {1}) cannot be assigned to {2}", property, property.getType().getName(), value.getClass().getName()));
        }
    }

    public <V> V clearValue(DependencyProperty property) {
        V oldValue = (V) this.propertyMap.get(property);
        this.onPropertyChanged(property, oldValue, DependencyProperty.UNSET_VALUE);
        this.propertyMap.put(property, DependencyProperty.UNSET_VALUE);
        return oldValue;
    }

    public boolean hasValue(DependencyProperty property) {
        return this.propertyMap.containsKey(property) && this.propertyMap.get(property) != DependencyProperty.UNSET_VALUE;
    }

    private void raisePropertyChanged(DependencyProperty property, Object oldValue, Object newValue) {
        if (!property.isSuspended(this)) {
            onPropertyChanged(property, oldValue, newValue);
        }
    }

    protected void onPropertyChanged(DependencyProperty property, Object oldValue, Object newValue) {
        property.getMeta(this).onPropertyChanged(property, this, oldValue, newValue);
    }
}
