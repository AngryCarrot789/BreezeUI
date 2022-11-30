package reghzy.breezeui.core.utils;

import reghzy.breezeui.core.FrameworkElement;

import java.text.MessageFormat;

public class MinMax {
    public double minW;
    public double minH;
    public double maxW;
    public double maxH;

    public MinMax(FrameworkElement e, Rect rect) {
        this.maxH = e.getValue(FrameworkElement.MAX_HEIGHT);
        this.minH = e.getValue(FrameworkElement.MIN_HEIGHT);
        this.maxW = e.getValue(FrameworkElement.MAX_WIDTH);
        this.minW = e.getValue(FrameworkElement.MIN_WIDTH);

        this.maxW = Maths.clamp(this.maxW, this.minW, rect.w);
        this.maxH = Maths.clamp(this.maxH, this.minH, rect.h);
    }

    @Override
    public String toString() {
        return MessageFormat.format("MinW({0}) MinH({1}) MaxW({2}) MaxH({3}) ", this.minW, this.minH, this.maxW, this.maxH);
    }
}
