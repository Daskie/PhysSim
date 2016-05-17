import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/16/2016
 */
public class QWindow {

    private long window_h;
    private int width, height;

    public QWindow(int width, int height) {
        this.width = width;
        this.height = height;

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window_h = glfwCreateWindow(width, height, "Hello World!", 0, 0);
        if (window_h == 0) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window_h, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwMakeContextCurrent(window_h);
        //glfwSwapInterval(1); //enable v-sync
        glfwShowWindow(window_h);
    }

    public void setCurrent() {
        glfwMakeContextCurrent(window_h);
    }

    public void swap() {
        glfwSwapBuffers(window_h);
    }

    public void cleanup() {
        glfwDestroyWindow(window_h);
    }

    public long getH() {
        return window_h;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
