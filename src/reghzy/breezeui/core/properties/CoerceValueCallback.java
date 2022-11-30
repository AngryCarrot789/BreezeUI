package reghzy.breezeui.core.properties;

public interface CoerceValueCallback {
    Object onCoerceValue(DependencyProperty property, DependencyObject owner, Object value);
}
