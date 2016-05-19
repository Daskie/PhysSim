import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private static QWindow window;

    private static boolean init() {
        if (!initLWJGL()) {
            System.err.println("Failed to initialize LWJGL!");
            return false;
        }

        if (!initGL()) {
            System.err.println("Failed to initialize OpenGL!");
            return false;
        }


        return true;
    }

    private static boolean initLWJGL() {
        System.out.println("LWJGL version " + Version.getVersion());

        glfwSetErrorCallback(glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err)));

        if ( glfwInit() != GLFW_TRUE ) {
           System.err.println("Unable to initialize GLFW");
            return false;
        }

        window = new QWindow(1280, 720);
        window.setCurrent();

        GL.createCapabilities();

        return true;
    }

    private static boolean initGL() {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        int glErr = glGetError();
        if (glErr != GL_NO_ERROR) {
            System.err.println("GL initialization error! " + glErr);
            return false;
        }

        return true;
    }

    private static void tempLoop() {
        glClearColor(0.25f, 0.0f, 0.25f, 1.0f);
        while ( glfwWindowShouldClose(window.getH()) == 0 ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer

            window.swap();

            glfwPollEvents();
        }
    }

    private static boolean cleanup() {
        window.cleanup();

        glfwTerminate();

        return true;
    }

    public static void main(String[] args) {

        if (!init()) {
            System.err.println("Initialization failed!");
            System.exit(-1);
        }

        tempLoop();

        if (!cleanup()) {
            System.err.println("Cleanup failed!");
            System.exit(-1);
        }
    }
}