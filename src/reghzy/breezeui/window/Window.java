package reghzy.breezeui.window;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import reghzy.breezeui.core.ContentControl;
import reghzy.breezeui.core.properties.DependencyProperty;
import reghzy.breezeui.core.properties.PropertyMeta;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMeta;
import reghzy.breezeui.core.properties.framework.FrameworkPropertyMetaFlags;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.utils.Disposable;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window extends ContentControl implements Disposable {
    public static final DependencyProperty TITLE = DependencyProperty.register("Title", String.class, Window.class, new PropertyMeta((p, o, ov, nv) -> ((Window) o).onTitleChanged((String) nv)));

    private final long hWnd;
    private boolean isDisposed;

    static {
        WIDTH.overrideMetadata(Window.class, new FrameworkPropertyMeta(Double.NaN, (property, owner, oldValue, newValue) -> ((Window) owner).glfwSetSize((Double) newValue, owner.getValue(HEIGHT)), FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
        HEIGHT.overrideMetadata(Window.class, new FrameworkPropertyMeta(Double.NaN, (property, owner, oldValue, newValue) -> ((Window) owner).glfwSetSize(owner.getValue(WIDTH), (Double) newValue), FrameworkPropertyMetaFlags.AFFECTS_LAYOUT));
    }

    private int frameBufferX;
    private int frameBufferY;

    private Window(long id) {
        this.hWnd = id;
        this.bypassMeasurementPolicies = true;
        glfwSetWindowSizeCallback(id, (window, width, height) -> processSizeChanged(width, height));
    }

    public void processSizeChanged(int width, int height) {
        try {
            WIDTH.suspend(this);
            HEIGHT.suspend(this);
            WIDTH.setValue(this, width);
            HEIGHT.setValue(this, height);
        }
        finally {
            WIDTH.unsuspend(this);
            HEIGHT.unsuspend(this);
        }

        updateLayout();
    }

    private void onTitleChanged(String title) {
        glfwSetWindowTitle(this.hWnd, title != null ? title : "");
    }

    public void updateLayout() {
        Vector2d size = new Vector2d(getWidth(), getHeight());
        this.isLayoutDirty = true;
        this.measure(new Rect(0, 0, size));
    }

    public static void app_init() {

    }

    public static Window create(String title, int width, int height) {
        return create(title, width, height, NULL, NULL);
    }

    public static Window create(String title, int width, int height, long monitor, long share) {
        long id = GLFW.glfwCreateWindow(width, height, title, monitor, share);
        if (id == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        Window window = new Window(id);
        window.setHeight(height);
        window.setWidth(width);
        window.setTitle(title);
        return window;
    }

    public void setTitle(String title) {

    }

    public void onAppTick() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetFramebufferSize(this.hWnd, x, y);
            this.frameBufferX = x.get(0);
            this.frameBufferY = y.get(0);
        }
    }

    public void glfwSetPos(int x, int y) {
        glfwSetWindowPos(this.hWnd, x, y);
    }

    public void glfwGetPos(IntBuffer width, IntBuffer height) {
        glfwGetWindowPos(this.hWnd, width, height);
    }

    public Vector2i glfwGetPos() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetPos(x, y);
            return new Vector2i(x.get(0), y.get(0));
        }
    }

    public void glfwGetSize(IntBuffer width, IntBuffer height) {
        glfwGetWindowSize(this.hWnd, width, height);
    }

    public Vector2i glfwGetSize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetSize(x, y);
            return new Vector2i(x.get(0), y.get(0));
        }
    }

    public void glfwSetSize(double x, double y) {
        glfwSetWindowSize(this.hWnd, (int) x, (int) y);
    }

    public void show() {
        GLFW.glfwShowWindow(this.hWnd);
        this.hasNeverUpdatedLayout = true;
        this.validate(true);
        updateLayout();
    }

    /**
     * Makes this window glfw's main window for rendering and other things
     */
    public void glfwMakeContextCurrent() {
        GLFW.glfwMakeContextCurrent(this.hWnd);
    }

    public long getWindowId() {
        return this.hWnd;
    }

    public void glfwFreeCallbacks() {
        Callbacks.glfwFreeCallbacks(this.hWnd);
    }

    public void glfwDestroyWindow() {
        GLFW.glfwDestroyWindow(this.hWnd);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.hWnd);
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.hWnd);
    }

    @Override
    public void dispose() {
        glfwFreeCallbacks();
        glfwDestroyWindow();
        this.isDisposed = true;
    }

    public void beginRenderFrame(long nvg) {
        double w = getWidth(), h = getHeight();
        GL11.glViewport(0, 0, (int) w, (int) h);
        nvgBeginFrame(nvg, (float) w, (float) h, (float) ((double) this.frameBufferX / w));
    }
}
