package reghzy.breezeui.core.utils;

import org.joml.Vector2d;

import java.text.MessageFormat;

public class Rect {
    public double x;
    public double y;
    public double w;
    public double h;

    public Rect(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Rect(Rect rect) {
        this(rect.x, rect.y, rect.w, rect.h);
    }

    public Rect(double x, double y, Vector2d size) {
        this(x, y, size.x, size.y);
    }

    public double getX2() {
        return this.w + this.x;
    }

    public double getY2() {
        return this.h + this.y;
    }

    public boolean isCloseTo(Rect rect) {
        return Maths.areClose(this.x, rect.x) && Maths.areClose(this.y, rect.y) &&
               Maths.areClose(this.w, rect.w) && Maths.areClose(this.h, rect.h);
    }

    public Vector2d getPosition() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d getSize() {
        return new Vector2d(this.w, this.h);
    }

    public Rect add(double x, double y, double w, double h) {
        return new Rect(this.x + x, this.y + y, this.w + w, this.h + h);
    }

    public Rect addXY(double x, double y) {
        return new Rect(this.x + x, this.y + y, this.w, this.h);
    }

    public Rect addSize(double w, double h) {
        return new Rect(this.x, this.y, this.w + w, this.h + h);
    }

    public Rect contract(double x, double y, double w, double h) {
        return new Rect(this.x + x, this.y + y, this.w - x - w, this.h - y - h);
    }

    public Rect expand(double x, double y, double w, double h) {
        return new Rect(this.x - x, this.y - y, this.w + x + w, this.h + y + h);
    }

    public static Rect min(Rect a, Rect b) {
        return new Rect(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.min(a.w, b.w), Math.min(a.h, b.h));
    }

    public static Rect max(Rect a, Rect b) {
        return new Rect(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.max(a.w, b.w), Math.max(a.h, b.h));
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0},{1} -> {2},{3} [W={4} H={5}]", this.x, this.y, getX2(), getY2(), this.w, this.h);
    }
}
