package reghzy.breezeui;

import org.lwjgl.Version;
import reghzy.breezeui.core.FrameworkElement;
import reghzy.breezeui.core.ItemsControl;
import reghzy.breezeui.core.Rectangle;
import reghzy.breezeui.core.utils.HorizontalAlignment;
import reghzy.breezeui.core.utils.Thickness;
import reghzy.breezeui.core.utils.VerticalAlignment;
import reghzy.breezeui.utils.Colour;
import reghzy.breezeui.window.Window;

public class Main {
    public static void main(String[] args) {
        // new Main().run();

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        Application application = new Application();
        application.setup();

        ItemsControl center = new ItemsControl();
        center.setValue(FrameworkElement.HORIZONTAL_ALIGNMENT, HorizontalAlignment.Stretch);
        center.setValue(FrameworkElement.VERTICAL_ALIGNMENT, VerticalAlignment.Center);
        center.setWidth(200d);
        center.setHeight(200d);
        center.setBackgroundColour(Colour.ofInts(75, 75, 75));
        // center.setMargin(new Thickness(25, 0, 25, 0));

        Rectangle a = createRect(50d, 50d, HorizontalAlignment.Left, VerticalAlignment.Top, 125);
        Rectangle b = createRect(50d, 50d, HorizontalAlignment.Right, VerticalAlignment.Top, 125);
        Rectangle c = createRect(50d, 50d, HorizontalAlignment.Stretch, VerticalAlignment.Bottom, 125);
        Rectangle d = createRect(50d, 25d, HorizontalAlignment.Center, VerticalAlignment.Center, 100);

        center.addChild(a);
        center.addChild(b);
        center.addChild(c);
        center.addChild(d);

        Window window = Window.create("Hello!!!", 500, 500);
        window.setContent(center);
        application.run(window);
    }

    private static Rectangle createRect(double width, double height, HorizontalAlignment left, VerticalAlignment top, int rgb) {
        Rectangle r1 = new Rectangle();
        // Margin does not yet work properly, when alignment is in the center
        // element.setMargin(new Thickness(50, 50, 50, 50));
        r1.setWidth(width);
        r1.setHeight(height);
        r1.setValue(FrameworkElement.HORIZONTAL_ALIGNMENT, left);
        r1.setValue(FrameworkElement.VERTICAL_ALIGNMENT, top);
        r1.setBackgroundColour(Colour.ofInts(rgb, rgb, rgb));
        return r1;
    }

}