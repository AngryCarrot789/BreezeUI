package reghzy.breezeui.core;

import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.render.RenderContext;
import reghzy.breezeui.utils.Colour;

public class Control extends FrameworkElement {
    public static final DependencyProperty BACKGROUND = DependencyProperty.register("BackgroundColour", Colour.class, Control.class, new PropertyMeta((p, control, o, n) -> ((Control) control).onBackgroundChanged((Colour) o, (Colour) n)));

    private void onBackgroundChanged(Colour oldValue, Colour newValue) {
        invalidateRender();
    }

    @Override
    public void render(RenderContext context, double width, double height) {
        super.render(context, width, height);
        Colour bg = getBackgroundColour();
        if (bg != null) {
            context.drawRect(new Rect(0, 0, width, height), bg);
        }
    }

    public void setBackgroundColour(Colour colour) {
        setValue(BACKGROUND, colour);
    }

    public Colour getBackgroundColour() {
        return getValue(BACKGROUND);
    }
}
