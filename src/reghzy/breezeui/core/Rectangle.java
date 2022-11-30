package reghzy.breezeui.core;

import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.render.RenderContext;
import reghzy.breezeui.utils.Colour;

public class Rectangle extends Control {
    public Rectangle() {

    }

    @Override
    public void render(RenderContext context) {
        context.drawRect(new Rect(0, 0, this.getActualWidth(), this.getActualHeight()), Colour.RED);
    }
}
