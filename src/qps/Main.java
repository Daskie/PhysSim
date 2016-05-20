package qps;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static Window window;
    private static InputHandler inputHandler;

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
        System.out.println("LWJGL version: " + Version.getVersion());

        glfwSetErrorCallback(glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err)));

        if ( glfwInit() != GLFW_TRUE ) {
           System.err.println("Unable to initialize GLFW");
            return false;
        }

        window = new Window(1280, 720);
        window.setCurrent();

        inputHandler = new InputHandler(window);

        GL.createCapabilities();

        return true;
    }

    private static boolean initGL() {
        int majVer = glGetInteger(GL_MAJOR_VERSION);
        int minVer = glGetInteger(GL_MINOR_VERSION);
        System.out.println("OpenGL Version: " + majVer + "." + minVer);

        glClearColor(0.25f, 0.0f, 0.25f, 1.0f);

        int glErr = glGetError();
        if (glErr != GL_NO_ERROR) {
            System.err.println("GL initialization error! " + glErr);
            return false;
        }

        return true;
    }

    private static void tempLoop() {
        while ( glfwWindowShouldClose(window.getID()) == 0 ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer

            window.swap();

            glfwPollEvents();
        }
    }

    private static boolean cleanup() {
        inputHandler.cleanup();
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