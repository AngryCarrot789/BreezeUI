package reghzy.breezeui.render;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import reghzy.breezeui.core.UIElement;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.utils.Colour;
import reghzy.breezeui.window.Window;

import java.util.Stack;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTransformPoint;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderContext {
    private final UIElement element;
    private final Vector2d offset;

    private static final long VG;

    private static Stack<Window> WINDOW_FRAMES = new Stack<Window>();

    private static Window ACTIVE_WINDOW;

    public RenderContext(UIElement element, Vector2d layoutOffset) {
        this.element = element;
        this.offset = layoutOffset;
    }

    public static NVGColor createNVGColour(Colour colour) {
        NVGColor color = NVGColor.calloc();
        color.r(colour.r);
        color.g(colour.g);
        color.b(colour.b);
        color.a(colour.a);
        return color;
    }

    // ------------------------------ Rendering functions ------------------------------

    public void drawRect(Rect rect, Colour colour) {
        // GL11.glPushMatrix();
        // GL11.glTranslatef(25f, 0.5f, 0f);
        try (NVGColor fillColor = createNVGColour(colour)) {
            nvgBeginPath(VG);
            nvgFillColor(VG, fillColor);
            nvgRect(VG, (float) (rect.x + this.offset.x), (float) (rect.y + this.offset.y), (float) rect.w, (float) rect.h);
            nvgFill(VG);
        }
        // GL11.glPopMatrix();
    }

    // ---------------------------------------------------------------------------------

    public static void setActiveWindow(Window window) {
        ACTIVE_WINDOW = window;
    }

    public static Window getActiveWindow() {
        if (ACTIVE_WINDOW == null) {
            throw new IllegalStateException("No active window available");
        }

        return ACTIVE_WINDOW;
    }

    public static void beginRender() {
        Window window = getActiveWindow();

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        window.beginRenderFrame(VG);
    }

    public static void endRender() {
        nvgEndFrame(VG);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public static void disposeNVG() {
        nvgDelete(VG);
    }

    static {
        VG = nvgCreate(NVG_ANTIALIAS);
        if (VG == NULL) {
            throw new RuntimeException("Failed to create NVG");
        }
    }

    public void close() {

    }
}
