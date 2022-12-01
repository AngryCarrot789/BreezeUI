package reghzy.breezeui.core;

import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.core.utils.Thickness;
import reghzy.breezeui.render.RenderContext;

public class UIElement extends Visual {
    public static final DependencyProperty IS_MOUSE_OVER = DependencyProperty.register("IsMouseOver", boolean.class, UIElement.class, new PropertyMeta(false));
    public static final DependencyProperty MARGIN = DependencyProperty.register("Margin", Thickness.class, UIElement.class, new PropertyMeta(new Thickness(0)));
    public static final DependencyProperty PARENT = DependencyProperty.register("Parent", UIElement.class, UIElement.class, new PropertyMeta(null, (p, o, ov, nv) -> ((UIElement) o).onParentChanged((UIElement) ov, (UIElement) nv)));

    public boolean isRenderDirty = false;
    public boolean isLayoutDirty = false;
    public boolean isUpdatingLayout = false;

    protected String id;

    protected boolean hasNeverUpdatedLayout;

    protected Rect lastLayoutRect;
    protected Rect layoutRect;

    protected boolean isValid;

    public UIElement() {
        this.lastLayoutRect = new Rect(0d, 0d, 0d, 0d);
        this.layoutRect = new Rect(0, 0, 0, 0);
        this.hasNeverUpdatedLayout = true;
    }

    public int getTreeIndex() {
        UIElement parent = getParent();
        if (parent == null) {
            return 0;
        }
        else {
            return parent.getTreeIndex() + 1;
        }
    }

    public final boolean isValid() {
        return this.isValid;
    }

    public final void validate(boolean isValid) {
        this.isValid = isValid;
    }

    public void render(RenderContext context, double width, double height) {

    }

    public final void invalidateLayout() {
        if (!this.isValid || this.isUpdatingLayout || this.isLayoutDirty) {
            return;
        }

        onLayoutInvalidated();
        ContextLayoutManager.of().getRearrangeQueue().add(this);
        this.isLayoutDirty = true;
    }

    public final void invalidateRender() {
        if (!this.isValid || this.isRenderDirty) {
            return;
        }

        onRenderInvalidated();
        ContextLayoutManager.of().getRenderQueue().add(this);
        this.isRenderDirty = true;
    }

    public final void invalidateVisual() {
        this.invalidateRender();
        this.invalidateLayout();
    }

    protected void onLayoutInvalidated() {

    }

    protected void onRenderInvalidated() {

    }

    public UIElement getParent() {
        return getValue(PARENT);
    }

    public void setParent(UIElement parent) {
        setValue(PARENT, parent);
    }

    private void onParentChanged(UIElement oldParent, UIElement newParent) {
        if (newParent != null) {
            newParent.invalidateLayout();
        }

        validate(newParent != null);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Thickness getMargin() {
        return getValue(MARGIN);
    }

    public void setMargin(Thickness thickness) {
        setValue(MARGIN, thickness);
    }

    /**
     * The core method for measuring the final rendering size of this UIElement
     * <p>
     *     This method may mutate this UIElement. However, if the input rect is the same as
     *     the last rect when this method was last called, a re-measurement may be skipped
     * </p>
     * @param rect The amount of available space this element has to fit in
     * @return Returns {@link UIElement#layoutRect}
     */
    public Rect measure(Rect rect) {
        // Rect rect = this.lastArrangementRect;
        // rect.x = rect.y = 0d;
        // if (Double.POSITIVE_INFINITY == element.prevAvailableSize.x)
        //     rect.w = this.desiredSize.x;
        // if (Double.POSITIVE_INFINITY == element.prevAvailableSize.y)
        //     rect.h = this.desiredSize.y;

        if (this.hasNeverUpdatedLayout || !this.isUpdatingLayout || this.isLayoutDirty) {
            this.hasNeverUpdatedLayout = false;
            this.isUpdatingLayout = true;
            try {
                this.lastLayoutRect = new Rect(this.layoutRect);
                this.layoutRect = this.measureCoreLayout(rect);
            }
            finally {
                this.isUpdatingLayout = false;
                this.isLayoutDirty = false;
            }

            ContextLayoutManager.of().getRenderQueue().add(this);
            // if (this.isRenderDirty || !this.lastLayoutRect.isCloseTo(this.layoutRect)) {
            //     ContextLayoutManager.of().getRenderList().add(this);
            // }
        }

        return this.layoutRect;
    }

    /**
     * Measures the actual layout position and size of this element, based on the available size given as a rect
     * <p>
     *     The returned value must be aware of the Rect's x and y positions being non-zero
     * </p>
     */
    public Rect measureCoreLayout(Rect rect) {
        return rect;
    }

    public RenderContext openRender() {
        return new RenderContext(this, this.layoutRect.getPosition());
    }

    public void closeRender(RenderContext context) {
        context.close();
        this.isRenderDirty = false;
    }

    public double getActualWidth() {
        return this.layoutRect.w;
    }

    public double getActualHeight() {
        return this.layoutRect.h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("[");
        if (this.id != null) {
            sb.append("id=").append(this.id).append(", ");
        }

        sb.append("render=").append(this.layoutRect).append(", ");
        sb.append("treeIndex=").append(this.getTreeIndex());
        return sb.toString();
    }
}
