package reghzy.breezeui;

import org.joml.Vector2d;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import reghzy.breezeui.core.FrameworkElement;
import reghzy.breezeui.core.Rectangle;
import reghzy.breezeui.core.utils.HorizontalAlignment;
import reghzy.breezeui.core.utils.Thickness;
import reghzy.breezeui.core.utils.VerticalAlignment;
import reghzy.breezeui.window.Window;
import sun.security.ssl.HandshakeOutStream;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;

public class Main {
    // The window handle
    private Window window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        this.window.dispose();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        this.window = Window.create("My window!", 800, 450);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(this.window.getWindowId(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.window.getWindowId(), pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    this.window.getWindowId(),
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
                            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        this.window.glfwMakeContextCurrent();

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        this.window.show();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // glfwSetWindowRefreshCallback(this.window, new GLFWWindowRefreshCallbackI() {
        //     @Override
        //     public void invoke(long window) {
        //         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        //         glfwSwapBuffers(window); // swap the color buffers
        //     }
        // });

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(this.window.getWindowId())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(this.window.getWindowId()); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        // new Main().run();

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        Application application = new Application();
        application.setup();

        FrameworkElement element = new Rectangle();
        element.setMargin(new Thickness(50, 50, 50, 50));
        element.setWidth(50d);
        element.setHeight(50d);
        element.setValue(FrameworkElement.HORIZONTAL_ALIGNMENT, HorizontalAlignment.Stretch);
        element.setValue(FrameworkElement.VERTICAL_ALIGNMENT, VerticalAlignment.Center);

        Window window = Window.create("Hello!!!", 500, 500);
        window.setContent(element);
        application.run(window);
    }
}