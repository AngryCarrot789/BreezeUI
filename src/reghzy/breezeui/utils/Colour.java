package reghzy.breezeui.utils;

import java.awt.*;

public class Colour {
    public final float r;
    public final float g;
    public final float b;
    public final float a;

    public static final Colour GREY = ofInts(32, 32, 32);
    public static final Colour RED = ofInts(255, 32, 20);

    public Colour(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Colour(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Colour(int r, int g, int b, int a) {
        this.r = (float) r / 255.0f;
        this.g = (float) g / 255.0f;
        this.b = (float) b / 255.0f;
        this.a = (float) a / 255.0f;
    }

    public Colour(Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Colour ofInts(int r, int g, int b, int a) {
        return new Colour(r, g, b, a);
    }

    public static Colour ofFloats(float r, float g, float b, float a) {
        return new Colour(r, g, b, a);
    }

    public static Colour ofInts(int r, int g, int b) {
        return new Colour(r, g, b, 255);
    }

    public static Colour ofFloats(float r, float g, float b) {
        return new Colour(r, g, b);
    }
}
