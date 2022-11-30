package reghzy.breezeui;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import reghzy.breezeui.core.ContextLayoutManager;
import reghzy.breezeui.core.UIElement;
import reghzy.breezeui.dispatcher.Dispatcher;
import reghzy.breezeui.dispatcher.DispatcherPriority;
import reghzy.breezeui.dispatcher.Messages;
import reghzy.breezeui.render.RenderContext;
import reghzy.breezeui.window.Window;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Application {
    private static Application CURRENT;

    private Dispatcher dispatcher;
    private Window mainWindow;

    private boolean isRunning;
    private volatile boolean isMarkedForShutdown;

    private boolean hasInitialisedCurrentContext;
    private volatile ContextLayoutManager layoutManager;
    private final Object layoutLock = new Object();

    private final ArrayList<Object> messageQueue;

    public Application() {
        if (CURRENT != null) {
            throw new UnsupportedOperationException("Cannot have multiple application instances");
        }

        CURRENT = this;

        this.messageQueue = new ArrayList<Object>();

        Window.app_init();
    }

    public static void pushMessage(Object message) {
        current().pushMessageInternal(message);
    }

    private void pushMessageInternal(Object message) {
        synchronized (this.messageQueue) {
            this.messageQueue.add(message);
            glfwPostEmptyEvent();
        }
    }

    public void run(Window mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("non-null window required");
        }

        this.mainWindow = mainWindow;
        this.mainWindow.glfwMakeContextCurrent();
        glfwSetWindowRefreshCallback(this.mainWindow.getWindowId(), w -> {
            pushMessage(null);
        });

        // The thread that this app is running on - the app's main thread,
        // used for updating and rendering when necessary (via callbacks)
        this.dispatcher = new Dispatcher();

        onStartupStage1(); // setup OpenGL stuff
        onStartupStage2(); // setup other application stuff
        onStartupStage3(); // create OpenGL stuff

        this.isRunning = true;
        this.mainWindow.show();

        // this.mainWindow.setValue(Window.WIDTH, 1280);
        // this.mainWindow.setValue(Window.HEIGHT, 720);

        try {
            do {
                tick();
                if (this.isMarkedForShutdown) {
                    break;
                }

                glfwWaitEvents();
            } while (true);
        }
        finally {
            this.isRunning = false;
            if (this.mainWindow != null) {
                this.mainWindow.dispose();
            }

            this.onShuttingDown();
            this.doShutdownGLFW();
            this.onShutdown();
        }
    }

    protected void tick() {
        // in WPF,
        // when you change a property, if that property is set to affect the rendering,
        // it will "invalidate the visual", scheduling a re-render, setting the RenderingInvalidated flag,
        // and also invalidating the arrangement, causing a full update

        // stack trace:
        //   move mouse over control
        //   is mouse over property changed (which affects render)
        //   invalidate render and therefore arrange
        //       invalidating render schedules a new dispatcher operation (on render priority)
        //   wait...
        //   ...
        //   eventually glfwWaitEvents() returns
        //   process any externally sent messages

        // wait for incoming events (real or empty)

        this.dispatcher.getQueue().process(DispatcherPriority.APP_PRE_TICK);

        processMessages();

        if (this.mainWindow != null) {
            this.mainWindow.onAppTick();

            if (this.mainWindow.shouldClose()) {
                this.shutdown();
                return;
            }
        }

        // -------------------- Invoke input callbacks ---------------------
        this.dispatcher.getQueue().process(DispatcherPriority.INPUT_PRE);
        processInputsPre();
        this.dispatcher.getQueue().process(DispatcherPriority.INPUT_POST);
        processInputsPost();
        // -----------------------------------------------------------------

        // Application arrangement
        synchronized (this.layoutManager != null ? this.layoutManager : this.layoutLock) {
            if (this.layoutManager != null) {
                this.layoutManager.setActive();

                this.layoutManager.updateLayout();

                this.layoutManager.setInactive();
            }

            // -------------------- Invoke render callbacks --------------------
            this.dispatcher.getQueue().process(DispatcherPriority.RENDER_PRE);
            processRenderPre();

            if (this.layoutManager != null) {
                RenderContext.setActiveWindow(this.mainWindow);
                for (UIElement element : this.layoutManager.getRenderList()) {
                    RenderContext context = element.openRender();
                    RenderContext.beginRender();
                    element.render(context);
                    element.closeRender(context);
                    RenderContext.endRender();
                }

                RenderContext.setActiveWindow(null);
                this.mainWindow.swapBuffers();
            }

            this.dispatcher.getQueue().process(DispatcherPriority.RENDER_POST);
            processRenderPost();

            // -----------------------------------------------------------------

            this.layoutManager = null;
        }

        this.dispatcher.getQueue().process(DispatcherPriority.APPLICATION_IDLE);
        this.dispatcher.getQueue().process(DispatcherPriority.CONTEXT_IDLE);
        this.dispatcher.getQueue().process(DispatcherPriority.APP_POST_TICK);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    protected void processMessages() {
        synchronized (this.messageQueue) {
            for (int i = this.messageQueue.size() - 1; i >= 0; i--) {
                processMessage(this.messageQueue.get(i));
            }
        }
    }

    protected void processInputsPre() {

    }

    protected void processInputsPost() {

    }

    protected void processRenderPre() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    protected void processRenderPost() {

    }

    protected void fullDraw(Window window) {

    }

    protected void processMessage(Object message) {
        if (message == null || message == Messages.WAKE_APPLICATION) {
            return;
        }
    }

    protected void onShuttingDown() {

    }

    protected void doShutdownGLFW() {
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    protected void onShutdown() {

    }

    /**
     * Setup GLFW and OpenGL ready for rendering
     */
    public void setup() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
    }

    protected void onStartupStage1() {

    }

    protected void onStartupStage2() {

    }

    protected void onStartupStage3() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.2f, 0.2f, 0.2f, 0.0f);
    }

    public void shutdown() {
        this.isMarkedForShutdown = true;
        pushMessageInternal(null);
    }

    public Thread getMainThread() {
        if (this.dispatcher != null) {
            return this.dispatcher.getThread();
        }

        throw new IllegalStateException("Application has not started");
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    public Window getMainWindow() {
        return this.mainWindow;
    }

    public ContextLayoutManager getCurrentLayoutManager() {
        if (this.layoutManager == null) {
            this.layoutManager = new ContextLayoutManager();
            pushMessageInternal(null);
        }

        return this.layoutManager;
    }

    public static Application current() {
        return CURRENT;
    }

    public boolean isReady() {
        return this.isRunning;
    }
}
