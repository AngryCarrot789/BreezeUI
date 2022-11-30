package reghzy.breezeui.core.utils;

import org.joml.Vector2d;

public class Maths {
    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static boolean areClose(Vector2d a, Vector2d b) {
        return areClose(a.x, b.x) && areClose(a.y, b.y);
    }

    public static boolean areClose(double a, double b) {
        return Math.abs(a - b) < 0.00001d;
    }

    public static boolean isInfOrNaN(double value) {
        return Double.isInfinite(value) || Double.isNaN(value);
    }
}
