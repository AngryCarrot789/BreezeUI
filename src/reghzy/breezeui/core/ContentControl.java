package reghzy.breezeui.core;

import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMeta;
import reghzy.breezeui.core.utils.Rect;

public class ContentControl extends Control {
    public static final DependencyProperty CONTENT = DependencyProperty.register("Content", UIElement.class, ContentControl.class, new FrameworkPropertyMeta(null, (p, o, ov, nv) -> ((ContentControl) o).onContentChanged((UIElement)ov, (UIElement)nv)));

    public ContentControl() {

    }

    public UIElement getContent() {
        return getValue(CONTENT);
    }

    public void setContent(UIElement element) {
        setValue(CONTENT, element);
    }

    private void onContentChanged(UIElement oldValue, UIElement newValue) {
        invalidateVisual();
        if (oldValue != null) {
            oldValue.validate(false);
        }

        if (newValue != null) {
            newValue.validate(true);
            newValue.setParent(this);
            newValue.invalidateVisual();
        }
    }

    @Override
    public Rect measureCoreLayout(Rect rect) {
        Rect layout = super.measureCoreLayout(rect);
        UIElement child = this.getContent();
        if (child != null) {
            layout = Rect.max(layout, child.measure(layout));
        }

        return layout;
    }

    @Override
    protected void onLayoutInvalidated() {
        super.onLayoutInvalidated();
        UIElement child = getContent();
        if (child != null) {
            child.invalidateLayout();
        }
    }

    @Override
    protected void onRenderInvalidated() {
        super.onRenderInvalidated();
        UIElement child = getContent();
        if (child != null) {
            child.invalidateRender();
        }
    }
}
