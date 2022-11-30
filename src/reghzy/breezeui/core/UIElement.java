package reghzy.breezeui.core;

import reghzy.breezeui.Application;
import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.core.utils.Thickness;
import reghzy.breezeui.render.RenderContext;

public class UIElement extends Visual {
    public static final DependencyProperty IS_MOUSE_OVER = DependencyProperty.register("IsMouseOver", boolean.class, UIElement.class, new PropertyMeta(false));
    public static final DependencyProperty MARGIN = DependencyProperty.register("Margin", Thickness.class, UIElement.class, new PropertyMeta(new Thickness(0)));

    public boolean isRenderDirty = false;
    public boolean isLayoutDirty = false;
    public boolean isUpdatingLayout = false;

    protected UIElement parent;

    protected String id;

    protected boolean hasNeverUpdatedLayout;

    protected Rect lastLayoutRect;
    public Rect layoutRect;

    public UIElement() {
        this.lastLayoutRect = new Rect(0d, 0d, 0d, 0d);
        this.layoutRect = new Rect(0, 0, 0, 0);
        this.hasNeverUpdatedLayout = true;
    }

    public void render(RenderContext context) {

    }

    public final void invalidateLayout() {
        if (this.isUpdatingLayout || this.isLayoutDirty) {
            return;
        }

        ContextLayoutManager.of().getRearrangeQueue().add(this);
        this.isLayoutDirty = true;
    }

    public final void invalidateRender() {
        if (this.isRenderDirty) {
            return;
        }

        ContextLayoutManager.of().getRenderList().add(this);
        this.isRenderDirty = true;
    }

    public final void invalidateVisual() {
        this.invalidateRender();
        this.invalidateLayout();
    }

    public UIElement getParent() {
        return this.parent;
    }

    public void setParent(UIElement parent) {
        this.parent = parent;
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

        boolean isSizeSimilar = this.layoutRect.isCloseTo(rect);
        if (this.hasNeverUpdatedLayout || (!this.isUpdatingLayout && !isSizeSimilar) || this.isLayoutDirty) {
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

            if (this.isRenderDirty || !this.lastLayoutRect.isCloseTo(this.layoutRect)) {
                ContextLayoutManager.of().getRenderList().add(this);
            }
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
}
