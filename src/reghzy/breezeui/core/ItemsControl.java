package reghzy.breezeui.core;

import reghzy.breezeui.render.RenderContext;

import java.util.ArrayList;

public class ItemsControl extends Control implements ISubContents {
    private final ArrayList<UIElement> children;

    public ItemsControl() {
        this.children = new ArrayList<UIElement>();
    }

    public void addChild(UIElement element) {
        element.invalidateLayout();
        this.children.add(element);
    }

    public boolean removeChild(UIElement element) {
        return this.children.remove(element);
    }

    @Override
    public void propagateLayoutInvalidation() {
        this.children.forEach(UIElement::invalidateLayout);
    }

    @Override
    public void render(RenderContext context) {

    }
}
