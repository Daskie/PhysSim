package qps;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/16/2016
 */
public class Window {

    private long windowID;
    private int width, height;

    public Window(int width, int height) {
        this.width = width;
        this.height = height;

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowID = glfwCreateWindow(width, height, "Hello World!", 0, 0);
        if (windowID == 0) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(windowID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwMakeContextCurrent(windowID);
        //glfwSwapInterval(1); //enable v-sync
        glfwShowWindow(windowID);
    }

    public void setCurrent() {
        glfwMakeContextCurrent(windowID);
    }

    public void swap() {
        glfwSwapBuffers(windowID);
    }

    public void cleanup() {
        glfwDestroyWindow(windowID);
    }

    public long getID() {
        return windowID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
