package reghzy.breezeui.core.properties;

public interface DefaultValueFactory {
    Object provide(DependencyProperty property, DependencyObject object);
}
