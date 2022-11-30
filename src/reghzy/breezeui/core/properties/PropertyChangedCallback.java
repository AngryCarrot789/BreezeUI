package reghzy.breezeui.core.properties;

public interface PropertyChangedCallback {
    void onPropertyChanged(DependencyProperty property, DependencyObject owner, Object oldValue, Object newValue);
}
