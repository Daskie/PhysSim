package qps;

import org.lwjgl.glfw.GLFWVidMode;
import qps.input_listeners.*;
import qps.window_listeners.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/16/2016
 */
public class Window {

    private long id;
    private WindowHandler windowHandler;
    private InputHandler inputHandler;

    int width, height;
    int framebufferWidth, framebufferHeight;
    int xPos, yPos;
    boolean iconified;
    boolean focused;

    private boolean[] keyStates;
    private double mouseX, mouseY;
    private boolean cursorPresent;
    private boolean[] mouseButtonStates;

    public Window(int width, int height) {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        id = glfwCreateWindow(width, height, "Hello World!", 0, 0);
        if (id == 0) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(id, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwMakeContextCurrent(id);
        //glfwSwapInterval(1); //enable v-sync
        glfwShowWindow(id);

        //---

        ByteBuffer buffer1 = ByteBuffer.allocate(8);
        ByteBuffer buffer2 = ByteBuffer.allocate(8);

        glfwGetWindowSize(id, buffer1, buffer2);
        this.width = buffer1.getInt(0);
        this.height = buffer2.getInt(0);
        glfwGetFramebufferSize(id, buffer1, buffer2);
        framebufferWidth = buffer1.getInt(0);
        framebufferHeight = buffer2.getInt(0);
        glfwGetWindowPos(id, buffer1, buffer2);
        xPos = buffer1.getInt(0);
        yPos = buffer2.getInt(0);
        iconified = glfwGetWindowAttrib(id, GLFW_ICONIFIED) != 0;
        focused = glfwGetWindowAttrib(id, GLFW_FOCUSED) != 0;

        keyStates = new boolean[GLFW_KEY_LAST + 1];
        glfwGetCursorPos(id, buffer1, buffer2);
        mouseX = buffer1.getDouble(0);
        mouseY = buffer2.getDouble(0);
        mouseButtonStates = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

        windowHandler = new WindowHandler(this);
        WindowListener windowListener = new WindowListener();
        windowHandler.addWindowCloseListener(windowListener);
        windowHandler.addWindowSizeListener(windowListener);
        windowHandler.addFramebufferSizeListener(windowListener);
        windowHandler.addWindowPosListener(windowListener);
        windowHandler.addWindowIconifyListener(windowListener);
        windowHandler.addWindowFocusListener(windowListener);
        windowHandler.addWindowRefreshListener(windowListener);

        inputHandler = new InputHandler(this);
        InputListener inputListener = new InputListener();
        inputHandler.addKeyListener(inputListener);
        inputHandler.addCharListener(inputListener);
        inputHandler.addCursorListener(inputListener);
        inputHandler.addEnterListener(inputListener);
        inputHandler.addMouseListener(inputListener);
        inputHandler.addScrollListener(inputListener);
        inputHandler.addDropListener(inputListener);
    }

    public void setCurrent() {
        glfwMakeContextCurrent(id);
    }

    public void swap() {
        glfwSwapBuffers(id);
    }

    public void cleanup() {
        windowHandler.cleanup();
        inputHandler.cleanup();
        glfwDestroyWindow(id);
    }

    public long id() {
        return id;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean keyState(int key) {
        return keyStates[key];
    }

    public double mouseX() {
        return mouseX;
    }

    public double mouseY() {
        return mouseY;
    }

    public boolean cursorPresent() {
        return cursorPresent;
    }

    public boolean mouseButtonState(int button) {
        return mouseButtonStates[button];
    }

    public WindowHandler windowHandler() {
        return windowHandler;
    }

    public InputHandler inputHandler() {
        return inputHandler;
    }

    private class WindowListener implements WindowCloseListener, WindowSizeListener, FramebufferSizeListener, WindowPosListener, WindowIconifyListener, WindowFocusListener, WindowRefreshListener {

        @Override
        public void wantsToClose(WindowHandler handler) {

        }

        @Override
        public void focusGained(WindowHandler handler) {
            focused = true;
        }

        @Override
        public void focusLost(WindowHandler handler) {
            focused = false;
        }

        @Override
        public void minimized(WindowHandler handler) {
            iconified = true;
        }

        @Override
        public void maximized(WindowHandler handler) {
            iconified = false;
        }

        @Override
        public void moved(int xPos, int yPos, WindowHandler handler) {
            Window.this.xPos = xPos;
            Window.this.yPos = yPos;
        }

        @Override
        public void needsRefreshed(WindowHandler handler) {

        }

        @Override
        public void resized(int width, int height, WindowHandler handler) {
            Window.this.width = width;
            Window.this.height = height;
        }
    }

    private class InputListener implements KeyListener, CharListener, CursorListener, EnterListener, MouseListener, ScrollListener, DropListener {

        @Override
        public void charPressed(char c, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {

        }

        @Override
        public void cursorMoved(double x, double y, InputHandler handler) {
            mouseX = x;
            mouseY = y;
        }

        @Override
        public void dropped(String[] filePaths, InputHandler handler) {

        }

        @Override
        public void entered(InputHandler handler) {
            cursorPresent = true;
        }

        @Override
        public void exited(InputHandler handler) {
            cursorPresent = false;
        }

        @Override
        public void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
            keyStates[key] = true;
        }

        @Override
        public void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
            keyStates[key] = false;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
            mouseButtonStates[button] = true;
        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
            mouseButtonStates[button] = false;
        }

        @Override
        public void scrolled(double xScroll, double yScroll, InputHandler handler) {

        }
    }
}
