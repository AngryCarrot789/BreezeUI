package reghzy.breezeui.core.properties;

import reghzy.breezeui.utils.Validate;

import java.util.function.Supplier;

public class PropertyMeta {
    private final Object defaultValue;
    private final PropertyChangedCallback propChangedCallback;
    private final CoerceValueCallback coerceValueCallback;

    public PropertyMeta() {
        this((Object) null);
    }

    public PropertyMeta(Object defaultValue) {
        this(defaultValue, null, null);
    }

    public PropertyMeta(PropertyChangedCallback onPropertyChanged) {
        this(null, onPropertyChanged, null);
    }

    public PropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged) {
        this(defaultValue, onPropertyChanged, null);
    }

    public PropertyMeta(PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue) {
        this(null, onPropertyChanged, onCoerceValue);
    }

    public PropertyMeta(Object defaultValue, PropertyChangedCallback onPropertyChanged, CoerceValueCallback onCoerceValue) {
        this.defaultValue = defaultValue;
        this.propChangedCallback = onPropertyChanged;
        this.coerceValueCallback = onCoerceValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PropertyChangedCallback getPropChangedCallback() {
        return this.propChangedCallback;
    }

    public CoerceValueCallback getCoerceValueCallback() {
        return this.coerceValueCallback;
    }

    public Object getDefaultValue() {
        Object value = this.defaultValue;
        if (value == null) {
            return null;
        }
        else if (value instanceof Supplier) {
            return ((Supplier<?>) value).get();
        }
        else {
            return value;
        }
    }

    public Object getDefaultValue(DependencyProperty property, DependencyObject object) {
        if (this.defaultValue instanceof DefaultValueFactory) {
            return ((DefaultValueFactory) this.defaultValue).provide(property, object);
        }
        else {
            return this.getDefaultValue();
        }
    }

    public Object getCoerceValue(DependencyProperty property, DependencyObject owner, Object value) {
        Validate.notNull(property, "Property cannot be null");
        Validate.notNull(owner, "owner cannot be null");
        if (this.coerceValueCallback == null) {
            if (property.isValueAssignable(value)) {
                return value;
            }
            else {
                throw new IllegalArgumentException("Value (" + value + ") is not valid for property: " + property);
            }
        }
        else {
            value = this.coerceValueCallback.onCoerceValue(property, owner, value);
            if (property.isValueAssignable(value)) {
                return value;
            }
            else {
                throw new RuntimeException("Coerced value (" + value + ") is not valid for property: " + property);
            }
        }
    }

    public void onPropertyChanged(DependencyProperty property, DependencyObject owner, Object oldValue, Object newValue) {
        if (this.propChangedCallback != null) {
            this.propChangedCallback.onPropertyChanged(property, owner, oldValue, newValue);
        }
    }

    public static class Builder {
        private Object defaultValue;
        private PropertyChangedCallback propChangedCallback;
        private CoerceValueCallback coerceValueCallback;

        private Builder() {

        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setPropChangedCallback(PropertyChangedCallback propChangedCallback) {
            this.propChangedCallback = propChangedCallback;
            return this;
        }

        public Builder setCoerceValueCallback(CoerceValueCallback coerceValueCallback) {
            this.coerceValueCallback = coerceValueCallback;
            return this;
        }

        public PropertyMeta build() {
            return new PropertyMeta(this.defaultValue, this.propChangedCallback, this.coerceValueCallback);
        }
    }
}
