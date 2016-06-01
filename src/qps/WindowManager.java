package qps;

import org.lwjgl.glfw.*;
import qps.window_listeners.FramebufferSizeListener;
import qps.window_listeners.*;

import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/20/2016
 */
public class WindowManager {

    private Window window;

    private WindowCloseCallback ccb;
    private WindowSizeCallback scb;
    private FramebufferSizeCallback fbscb;
    private WindowPosCallback pcb;
    private WindowIconifyCallback icb;
    private WindowFocusCallback fcb;
    private WindowRefreshCallback rcb;

    private LinkedList<WindowCloseListener> closeListeners;
    private LinkedList<WindowSizeListener> sizeListeners;
    private LinkedList<FramebufferSizeListener> framebufferSizeListeners;
    private LinkedList<WindowPosListener> posListeners;
    private LinkedList<WindowIconifyListener> iconifyListeners;
    private LinkedList<WindowFocusListener> focusListeners;
    private LinkedList<WindowRefreshListener> refreshListeners;

    public WindowManager(Window window) {
        this.window = window;

        closeListeners = new LinkedList<WindowCloseListener>();
        sizeListeners = new LinkedList<WindowSizeListener>();
        framebufferSizeListeners = new LinkedList<FramebufferSizeListener>();
        posListeners = new LinkedList<WindowPosListener>();
        iconifyListeners = new LinkedList<WindowIconifyListener>();
        focusListeners = new LinkedList<WindowFocusListener>();
        refreshListeners = new LinkedList<WindowRefreshListener>();

        glfwSetWindowCloseCallback(window.id(), ccb = new WindowCloseCallback());
        glfwSetWindowSizeCallback(window.id(), scb = new WindowSizeCallback());
        glfwSetFramebufferSizeCallback(window.id(), fbscb = new FramebufferSizeCallback());
        glfwSetWindowPosCallback(window.id(), pcb = new WindowPosCallback());
        glfwSetWindowIconifyCallback(window.id(), icb = new WindowIconifyCallback());
        glfwSetWindowFocusCallback(window.id(), fcb = new WindowFocusCallback());
        glfwSetWindowRefreshCallback(window.id(), rcb = new WindowRefreshCallback());
    }

    public void cleanup() {
        ccb.release();
        scb.release();
        fbscb.release();
        pcb.release();
        icb.release();
        fcb.release();
        rcb.release();
    }

    public void addWindowCloseListener(WindowCloseListener listener) {
        closeListeners.push(listener);
    }

    public void addWindowSizeListener(WindowSizeListener listener) {
        sizeListeners.push(listener);
    }

    public void addFramebufferSizeListener(FramebufferSizeListener listener) {
        framebufferSizeListeners.push(listener);
    }

    public void addWindowPosListener(WindowPosListener listener) {
        posListeners.push(listener);
    }

    public void addWindowIconifyListener(WindowIconifyListener listener) {
        iconifyListeners.push(listener);
    }

    public void addWindowFocusListener(WindowFocusListener listener) {
        focusListeners.push(listener);
    }

    public void addWindowRefreshListener(WindowRefreshListener listener) {
        refreshListeners.push(listener);
    }

    private class WindowCloseCallback extends GLFWWindowCloseCallback {
        @Override
        public void invoke(long windowID) {
            for (WindowCloseListener listener : closeListeners) {
                listener.wantsToClose(WindowManager.this);
            }
        }
    }

    private class WindowSizeCallback extends GLFWWindowSizeCallback {
        @Override
        public void invoke(long windowID, int width, int height) {
            for (WindowSizeListener listener : sizeListeners) {
                listener.resized(width, height, WindowManager.this);
            }
        }
    }

    private class FramebufferSizeCallback extends GLFWFramebufferSizeCallback {
        @Override
        public void invoke(long windowID, int width, int height) {
            for (FramebufferSizeListener listener : framebufferSizeListeners) {
                listener.resized(width, height, WindowManager.this);
            }
        }
    }

    private class WindowPosCallback extends GLFWWindowPosCallback {
        @Override
        public void invoke(long windowID, int xPos, int yPos) {
            for (WindowPosListener listener : posListeners) {
                listener.moved(xPos, yPos, WindowManager.this);
            }
        }
    }

    private class WindowIconifyCallback extends GLFWWindowIconifyCallback {
        @Override
        public void invoke(long windowID, int iconified) {
            if (iconified != 0) {
                for (WindowIconifyListener listener : iconifyListeners) {
                    listener.minimized(WindowManager.this);
                }
            }
            else {
                for (WindowIconifyListener listener : iconifyListeners) {
                    listener.maximized(WindowManager.this);
                }
            }
        }
    }

    private class WindowFocusCallback extends GLFWWindowFocusCallback {
        @Override
        public void invoke(long windowID, int focused) {
            if (focused != 0) {
                for (WindowFocusListener listener : focusListeners) {
                    listener.focusGained(WindowManager.this);
                }
            }
            else {
                for (WindowFocusListener listener : focusListeners) {
                    listener.focusLost(WindowManager.this);
                }
            }
        }
    }

    //window needsRefreshed and needs refreshed
    private class WindowRefreshCallback extends GLFWWindowRefreshCallback {
        @Override
        public void invoke(long windowID) {
            for (WindowRefreshListener listener : refreshListeners) {
                listener.needsRefreshed(WindowManager.this);
            }
        }
    }

}
