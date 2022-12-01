package reghzy.breezeui.core;

import org.joml.Vector2d;
import reghzy.breezeui.core.properties.DependencyObject;
import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.PropertyChangedCallback;
import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMeta;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMetaFlags;
import reghzy.breezeui.core.utils.HorizontalAlignment;
import reghzy.breezeui.core.utils.Maths;
import reghzy.breezeui.core.utils.MinMax;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.core.utils.Thickness;
import reghzy.breezeui.core.utils.VerticalAlignment;

public class FrameworkElement extends UIElement {
    public static final DependencyProperty HORIZONTAL_ALIGNMENT = DependencyProperty.register("HorizontalAlignment", HorizontalAlignment.class, FrameworkElement.class, new FrameworkPropertyMeta(HorizontalAlignment.Left, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty VERTICAL_ALIGNMENT = DependencyProperty.register("VerticalAlignment", VerticalAlignment.class, FrameworkElement.class, new FrameworkPropertyMeta(VerticalAlignment.Top, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));

    public static final DependencyProperty WIDTH =      DependencyProperty.register("Width", double.class, FrameworkElement.class,     new FrameworkPropertyMeta(Double.NaN, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty MIN_WIDTH =  DependencyProperty.register("MinWidth", double.class, FrameworkElement.class,  new FrameworkPropertyMeta(0d, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty MAX_WIDTH =  DependencyProperty.register("MaxWidth", double.class, FrameworkElement.class,  new FrameworkPropertyMeta(Double.POSITIVE_INFINITY, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty HEIGHT =     DependencyProperty.register("Height", double.class, FrameworkElement.class,    new FrameworkPropertyMeta(Double.NaN, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty MIN_HEIGHT = DependencyProperty.register("MinHeight", double.class, FrameworkElement.class, new FrameworkPropertyMeta(0d, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    public static final DependencyProperty MAX_HEIGHT = DependencyProperty.register("MaxHeight", double.class, FrameworkElement.class, new FrameworkPropertyMeta(Double.POSITIVE_INFINITY, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));

    static {
        IS_MOUSE_OVER.overrideMetadata(FrameworkElement.class, new FrameworkPropertyMeta(false, FrameworkPropertyMetaFlags.AFFECTS_RENDER));
        MARGIN.overrideMetadata(FrameworkElement.class, new FrameworkPropertyMeta(new Thickness(0), FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
        PARENT.overrideMetadata(FrameworkElement.class, new FrameworkPropertyMeta(null, FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    }

    protected boolean bypassMeasurementPolicies;

    public FrameworkElement() {

    }

    @Override
    public Rect measureCoreLayout(Rect rect) {
        if (this.bypassMeasurementPolicies) {
            return rect;
        }

        Vector2d size = rect.getSize();
        Thickness margin = getValue(MARGIN);

        // we are bound to the center
        // rect = 500x500 square

        // our margin = 5,5,5,5

        double marginSubW = margin.getLeft() + margin.getRight();
        double marginSubH = margin.getTop() + margin.getBottom();

        double targetWidth = getWidth();
        double targetHeight = getHeight();

        Rect layout = getAlignmentLayout(rect, targetWidth, targetHeight);

        // rect = 0,0,800,800
        // margin = 50,50,50,50
        // marginSubH = 10
        // space available = 0,0,700,700
        double realSpaceW = rect.w - marginSubW;
        double realSpaceH = rect.h - marginSubH;

        double spaceRemainingW = rect.w - layout.w - marginSubW;
        double spaceRemainingH = rect.h - layout.h - marginSubH;

        // if ((rect.h - layout.h - margin.getTop()) < 0 && getValue(VERTICAL_ALIGNMENT) == VerticalAlignment.Center) {
        //     layout = layout.contract(0d, margin.getTop(), 0d, margin.getBottom());
        // }
        // if (spaceRemainingW < 0 && getValue(HORIZONTAL_ALIGNMENT) != HorizontalAlignment.Center) {
        //     layout = layout.contract(margin.getLeft(), 0d, margin.getRight(), 0d);
        // }

        // if (layout.w < realSpaceW) layout = new Rect(layout.x, layout.y, realSpaceW, layout.h);
        // if (layout.h < realSpaceH) layout = new Rect(layout.x, layout.y, layout.w, realSpaceH);

        // layout = layout.contract(margin.getLeft(), margin.getTop(), margin.getRight(), margin.getBottom());

        MinMax bound = new MinMax(this, rect);
        if (layout.w < bound.minW) {
            layout = new Rect(layout.x, layout.y, bound.minW, layout.h);
        }
        else if (layout.w > bound.maxW) {
            layout = new Rect(layout.x, layout.y, bound.maxW, layout.h);
        }

        if (layout.h < bound.minH) {
            layout = new Rect(layout.x, layout.y, layout.w, bound.minH);
        }
        else if (layout.h > bound.maxH) {
            layout = new Rect(layout.x, layout.y, layout.w, bound.maxH);
        }

        return layout;
    }

    /**
     * The final calculation size of this element
     * @param rect
     * @return
     */
    public Rect measureFinalRect(Rect rect) {
        return rect;
    }

    public double getSuitableWidth(Rect rect) {
        double w = getWidth();
        if (Double.isNaN(w)) {
            w = getValue(MIN_WIDTH);
        }
        else if (Double.isInfinite(w)) {
            double max = getValue(MAX_WIDTH);
            w = Double.isInfinite(max) ? rect.w : max;
        }

        return Maths.clamp(w, getValue(MIN_WIDTH), getValue(MAX_WIDTH));
    }

    public double getSuitableHeight(Rect rect) {
        double h = getHeight();
        if (Double.isNaN(h)) {
            h = getValue(MIN_HEIGHT);
        }
        else if (Double.isInfinite(h)) {
            double max = getValue(MAX_HEIGHT);
            h = Double.isInfinite(max) ? rect.h : max;
        }

        return Maths.clamp(h, getValue(MIN_HEIGHT), getValue(MAX_HEIGHT));
    }

    /**
     * Calculates the location of this element relative to the given rect
     */
    public Rect getAlignmentLayout(Rect area, double width, double height) {
        double x, y, w, h;
        switch (this.<HorizontalAlignment>getValue(HORIZONTAL_ALIGNMENT)) {
            case Center: {
                double center = area.x + (area.w / 2);
                x = center - (width / 2);
                w = width;
            } break;
            case Stretch: {
                x = area.x;
                w = area.w;
            } break;
            case Right: {
                x = area.getX2() - width;
                w = width;
            } break;
            default: {
                x = area.x;
                w = width;
            } break;
        }

        switch (this.<VerticalAlignment>getValue(VERTICAL_ALIGNMENT)) {
            case Center: {
                double center = area.y + (area.h / 2);
                y = center - (height / 2);
                h = height;
            } break;
            case Stretch: {
                y = area.y;
                h = area.h;
            } break;
            case Bottom: {
                y = area.getY2() - height;
                h = height;
            } break;
            default: {
                y = area.y;
                h = height;
            } break;
        }

        return new Rect(x, y, w, h);
    }

    @Override
    protected void onPropertyChanged(DependencyProperty property, Object oldValue, Object newValue) {
        super.onPropertyChanged(property, oldValue, newValue);
        PropertyMeta propertyMeta = property.getMeta(this);
        if (propertyMeta instanceof FrameworkPropertyMeta) {
            FrameworkPropertyMeta meta = (FrameworkPropertyMeta) propertyMeta;
            UIElement parent = getParent();
            if (parent != null) {
                if (meta.canAffectParentLayout()) {
                    parent.invalidateLayout();
                }
            }

            if (meta.canAffectLayout() && (!this.bypassMeasurementPolicies || property != FrameworkElement.WIDTH && property != FrameworkElement.HEIGHT)) {
                this.invalidateLayout();
            }

            if (meta.canAffectRender()) {
                this.invalidateVisual();
            }
        }
    }

    public double getWidth() {
        return getValue(WIDTH);
    }

    public double getHeight() {
        return getValue(HEIGHT);
    }

    public void setWidth(double value) {
        setValue(WIDTH, value);
    }

    public void setHeight(double value) {
        setValue(HEIGHT, value);
    }
}
