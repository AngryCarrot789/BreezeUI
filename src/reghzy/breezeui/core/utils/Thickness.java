package reghzy.breezeui.core.utils;

public class Thickness {
    private final double left;
    private final double top;
    private final double right;
    private final double bottom;

    public Thickness(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Thickness(double horizontal, double vertical) {
        this(horizontal, vertical, horizontal, vertical);
    }

    public Thickness(double all) {
        this(all, all, all, all);
    }

    public double getLeft() {
        return this.left;
    }

    public double getTop() {
        return this.top;
    }

    public double getRight() {
        return this.right;
    }

    public double getBottom() {
        return this.bottom;
    }
}
