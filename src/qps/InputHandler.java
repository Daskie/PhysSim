package qps;

import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/19/2016
 */
public class InputHandler {

    private Window window;
    private KeyCallback kcb;
    private CursorPosCallback cpc;
    private CursorEnterCallback cec;
    private MouseButtonCallback mbc;
    private ScrollCallback sc;

    private boolean[] keyState = new boolean[GLFW_KEY_LAST + 1];
    private int mouseX, mouseY;
    boolean cursorPresent;
    private boolean[] mouseButtonState = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    public InputHandler(Window window) {
        this.window = window;

        glfwSetKeyCallback(window.getID(), kcb = new KeyCallback());
        glfwSetCursorPosCallback(window.getID(), cpc = new CursorPosCallback());
        glfwSetCursorEnterCallback(window.getID(), cec = new CursorEnterCallback());
        glfwSetMouseButtonCallback(window.getID(), mbc = new MouseButtonCallback());
        glfwSetScrollCallback(window.getID(), sc = new ScrollCallback());
    }

    public boolean keyState(int key) {
        return keyState[key];
    }

    public int mouseX() {
        return mouseX;
    }

    public int mouseY() {
        return mouseY;
    }

    public boolean cursorPresent() {
        return cursorPresent;
    }

    public boolean mouseButtonState(int button) {
        return mouseButtonState[button];
    }

    public void cleanup() {
        kcb.release();
        cpc.release();
        cec.release();
        mbc.release();
        sc.release();
    }

    private class KeyCallback extends GLFWKeyCallback {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            keyState[key] = action != GLFW_RELEASE;
        }
    }

    private class CursorPosCallback extends GLFWCursorPosCallback {
        @Override
        public void invoke(long windowH, double x, double y) {
            mouseX = (int)x;
            mouseY = (int)y;
        }
    }

    private class CursorEnterCallback extends GLFWCursorEnterCallback {
        @Override
        public void invoke(long windowH, int entered) {
            cursorPresent = entered != 0;
        }
    }

    private class MouseButtonCallback extends GLFWMouseButtonCallback {
        @Override
        public void invoke(long windowH, int button, int action, int mods) {
            mouseButtonState[button] = action != GLFW_RELEASE;
        }
    }

    private class ScrollCallback extends GLFWScrollCallback {
        @Override
        public void invoke(long windowH, double xOffset, double yOffset) {

        }
    }
}
