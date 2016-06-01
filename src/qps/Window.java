package qps;

import org.lwjgl.glfw.GLFWVidMode;
import qps.input_listeners.*;
import qps.window_listeners.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.BufferUtils.*;

/**
 * @since 5/16/2016
 */
public class Window {

    private long id;
    private WindowManager windowManager;
    private InputManager inputManager;

    private int width, height;
    private int framebufferWidth, framebufferHeight;
    private int xPos, yPos;
    private boolean iconified;
    private boolean focused;

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

        ByteBuffer buffer1 = createByteBuffer(8);
        ByteBuffer buffer2 = createByteBuffer(8);

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

        windowManager = new WindowManager(this);
        WindowListener windowListener = new WindowListener();
        windowManager.addWindowCloseListener(windowListener);
        windowManager.addWindowSizeListener(windowListener);
        windowManager.addFramebufferSizeListener(windowListener);
        windowManager.addWindowPosListener(windowListener);
        windowManager.addWindowIconifyListener(windowListener);
        windowManager.addWindowFocusListener(windowListener);
        windowManager.addWindowRefreshListener(windowListener);

        inputManager = new InputManager(this);
        InputListener inputListener = new InputListener();
        inputManager.addKeyListener(inputListener);
        inputManager.addCharListener(inputListener);
        inputManager.addCursorListener(inputListener);
        inputManager.addEnterListener(inputListener);
        inputManager.addMouseListener(inputListener);
        inputManager.addScrollListener(inputListener);
        inputManager.addDropListener(inputListener);
    }

    public void setCurrent() {
        glfwMakeContextCurrent(id);
    }

    public void swap() {
        glfwSwapBuffers(id);
    }

    public void cleanup() {
        windowManager.cleanup();
        inputManager.cleanup();
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

    public double mouseXGL() {
        return mouseX;
    }

    public double mouseYGL() {
        return height - mouseY - 1;
    }

    public boolean cursorPresent() {
        return cursorPresent;
    }

    public boolean mouseButtonState(int button) {
        return mouseButtonStates[button];
    }

    public WindowManager windowHandler() {
        return windowManager;
    }

    public InputManager inputHandler() {
        return inputManager;
    }

    private class WindowListener implements WindowCloseListener, WindowSizeListener, FramebufferSizeListener, WindowPosListener, WindowIconifyListener, WindowFocusListener, WindowRefreshListener {

        @Override
        public void wantsToClose(WindowManager handler) {

        }

        @Override
        public void focusGained(WindowManager handler) {
            focused = true;
        }

        @Override
        public void focusLost(WindowManager handler) {
            focused = false;
        }

        @Override
        public void minimized(WindowManager handler) {
            iconified = true;
        }

        @Override
        public void maximized(WindowManager handler) {
            iconified = false;
        }

        @Override
        public void moved(int xPos, int yPos, WindowManager handler) {
            Window.this.xPos = xPos;
            Window.this.yPos = yPos;
        }

        @Override
        public void needsRefreshed(WindowManager handler) {

        }

        @Override
        public void resized(int width, int height, WindowManager handler) {
            Window.this.width = width;
            Window.this.height = height;
        }
    }

    private class InputListener implements KeyListener, CharListener, CursorListener, EnterListener, MouseListener, ScrollListener, DropListener {

        @Override
        public void charPressed(char c, boolean shift, boolean ctrl, boolean alt, InputManager manager) {

        }

        @Override
        public void cursorMoved(double x, double y, double dx, double dy, InputManager manager) {
            mouseX = x;
            mouseY = y;
        }

        @Override
        public void dropped(String[] filePaths, InputManager manager) {

        }

        @Override
        public void entered(InputManager manager) {
            cursorPresent = true;
        }

        @Override
        public void exited(InputManager manager) {
            cursorPresent = false;
        }

        @Override
        public void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
            keyStates[key] = true;
        }

        @Override
        public void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            keyStates[key] = false;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
            mouseButtonStates[button] = true;
        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            mouseButtonStates[button] = false;
        }

        @Override
        public void scrolled(double xScroll, double yScroll, InputManager manager) {

        }
    }
}
