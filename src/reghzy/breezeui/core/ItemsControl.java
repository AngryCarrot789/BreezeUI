package reghzy.breezeui.core;

import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMeta;
import reghzy.breezeui.core.utils.Rect;

import java.util.ArrayList;
import java.util.Collection;

public class ItemsControl extends Control {
    public static final DependencyProperty CHILDREN = DependencyProperty.register("Children", Collection.class, ItemsControl.class, new FrameworkPropertyMeta((p, o, ov, nv) -> ((ItemsControl) o).onChildrenChanged((Collection) ov, (Collection) nv)));

    public ItemsControl() {

    }

    @Override
    public Rect measureCoreLayout(Rect rect) {
        Rect layout = super.measureCoreLayout(rect);
        Collection<UIElement> children = getValue(CHILDREN);
        if (children != null) {
            for(UIElement element : children) {
                layout = Rect.max(layout, element.measure(layout));
            }
        }

        return layout;
    }

    public void addChild(UIElement element) {
        if (element == null)
            throw new IllegalArgumentException("Element is null");

        element.setParent(this);
        invalidateVisual();
        element.invalidateVisual();
        getChildren().add(element);
    }

    public boolean removeChild(UIElement element) {
        if (element == null)
            throw new IllegalArgumentException("Element is null");
        element.setParent(null);
        boolean removed = getChildren().remove(element);
        if (removed) {
            invalidateRender();
        }

        return removed;
    }

    public Collection<UIElement> getChildren() {
        Collection collection = getValue(CHILDREN);
        if (collection == null) {
            setValue(CHILDREN, collection = new ArrayList());
        }

        return collection;
    }

    private void onChildrenChanged(Collection oldList, Collection newList) {
        if (oldList != null) {
            for (Object obj : oldList) {
                ((UIElement) obj).validate(false);
            }
        }

        if (newList != null) {
            for (Object obj : newList) {
                if (!(obj instanceof UIElement)) {
                    throw new RuntimeException("Invalid child object: " + obj);
                }
            }

            for (Object obj : newList) {
                ((UIElement) obj).validate(true);
            }
        }

        this.invalidateLayout();
    }

    @Override
    protected void onLayoutInvalidated() {
        super.onLayoutInvalidated();
        Collection<UIElement> children = getValue(CHILDREN);
        if (children != null) {
            children.forEach(UIElement::invalidateLayout);
        }
    }

    @Override
    protected void onRenderInvalidated() {
        super.onRenderInvalidated();
        Collection<UIElement> children = getValue(CHILDREN);
        if (children != null) {
            children.forEach(UIElement::invalidateRender);
        }
    }
}
