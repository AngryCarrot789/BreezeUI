package reghzy.breezeui.core;

import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMeta;
import reghzy.breezeui.core.utils.Rect;

public class ContentControl extends Control {
    public static final DependencyProperty CONTENT = DependencyProperty.register("Content", Object.class, ContentControl.class, new FrameworkPropertyMeta(null, (property, owner, oldValue, newValue) -> ((ContentControl) owner).onContentChanged(oldValue, newValue)));

    public ContentControl() {

    }



    public UIElement getContent() {
        return getValue(CONTENT);
    }

    public void setContent(UIElement element) {
        setValue(CONTENT, element);
    }

    private void onContentChanged(Object oldValue, Object newValue) {
        invalidateVisual();
        if (newValue instanceof UIElement) {
            ((UIElement) newValue).invalidateLayout();
        }
    }

    @Override
    public Rect measureCoreLayout(Rect rect) {
        UIElement child =this.getContent();
        if (child != null) {
            rect = Rect.max(rect, child.measure(rect));
        }

        return super.measureCoreLayout(rect);
    }
}
