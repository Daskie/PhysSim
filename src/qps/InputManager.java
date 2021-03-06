package qps;

import org.lwjgl.glfw.*;
import qps.input_listeners.*;

import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @since 5/19/2016
 */
public class InputManager {

    private Window window;

    private KeyCallback kcb;
    private CharModsCallback cmcb;
    private CursorPosCallback cpcb;
    private CursorEnterCallback cecb;
    private MouseButtonCallback mbcb;
    private ScrollCallback scb;
    private DropCallback dcb;

    private LinkedList<KeyListener> keyListeners;
    private LinkedList<CharListener> charListeners;
    private LinkedList<CursorListener> cursorListeners;
    private LinkedList<EnterListener> enterListeners;
    private LinkedList<MouseListener> mouseListeners;
    private LinkedList<ScrollListener> scrollListeners;
    private LinkedList<DropListener> dropListeners;

    public InputManager(Window window) {
        this.window = window;

        keyListeners = new LinkedList<KeyListener>();
        charListeners = new LinkedList<CharListener>();
        cursorListeners = new LinkedList<CursorListener>();
        enterListeners = new LinkedList<EnterListener>();
        mouseListeners = new LinkedList<MouseListener>();
        scrollListeners = new LinkedList<ScrollListener>();
        dropListeners = new LinkedList<DropListener>();

        glfwSetKeyCallback(window.id(), kcb = new KeyCallback());
        glfwSetCharModsCallback(window.id(), cmcb = new CharModsCallback());
        glfwSetCursorPosCallback(window.id(), cpcb = new CursorPosCallback());
        glfwSetCursorEnterCallback(window.id(), cecb = new CursorEnterCallback());
        glfwSetMouseButtonCallback(window.id(), mbcb = new MouseButtonCallback());
        glfwSetScrollCallback(window.id(), scb = new ScrollCallback());
        glfwSetDropCallback(window.id(), dcb = new DropCallback());
    }

    public void cleanup() {
        kcb.release();
        cpcb.release();
        cecb.release();
        mbcb.release();
        scb.release();
        dcb.release();
    }

    public void addKeyListener(KeyListener listener) {
        keyListeners.addLast(listener);
    }

    public void addCharListener(CharListener listener) {
        charListeners.addLast(listener);
    }

    public void addCursorListener(CursorListener listener) {
        cursorListeners.addLast(listener);
    }

    public void addEnterListener(EnterListener listener) {
        enterListeners.addLast(listener);
    }

    public void addMouseListener(MouseListener listener) {
        mouseListeners.addLast(listener);
    }

    public void addScrollListener(ScrollListener listener) {
        scrollListeners.addLast(listener);
    }

    public void addDropListener(DropListener listener) {
        dropListeners.addLast(listener);
    }

    private class KeyCallback extends GLFWKeyCallback {
        @Override
        public void invoke(long windowID, int key, int scancode, int action, int mods) {
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean ctrl = (mods & GLFW_MOD_CONTROL) != 0;
            boolean alt = (mods & GLFW_MOD_ALT) != 0;

            switch (action) {
                case GLFW_PRESS: {
                    for (KeyListener listener : keyListeners) {
                        listener.keyPressed(key, shift, ctrl, alt, false, InputManager.this);
                    }
                    break;
                }
                case GLFW_REPEAT: {
                    for (KeyListener listener : keyListeners) {
                        listener.keyPressed(key, shift, ctrl, alt, true, InputManager.this);
                    }
                    break;
                }
                case GLFW_RELEASE: {
                    for (KeyListener listener : keyListeners) {
                        listener.keyReleased(key, shift, ctrl, alt, InputManager.this);
                    }
                    break;
                }
            }
        }
    }

    private class CharModsCallback extends GLFWCharModsCallback {
        @Override
        public void invoke(long windowID, int codepoint, int mods) {
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean ctrl = (mods & GLFW_MOD_CONTROL) != 0;
            boolean alt = (mods & GLFW_MOD_ALT) != 0;

            for (CharListener listener : charListeners) {
                listener.charPressed((char)codepoint, shift, ctrl, alt, InputManager.this);
            }
        }
    }

    private class CursorPosCallback extends GLFWCursorPosCallback {
        private double lastX = 0.0, lastY = 0.0;
        @Override
        public void invoke(long windowID, double x, double y) {
            for (CursorListener listener : cursorListeners) {
                listener.cursorMoved(x, y, x - lastX, y - lastY, InputManager.this);
            }

            lastX = x;
            lastY = y;
        }
    }

    private class CursorEnterCallback extends GLFWCursorEnterCallback {
        @Override
        public void invoke(long windowID, int entered) {
            for (EnterListener listener : enterListeners) {
                listener.entered(InputManager.this);
            }
        }
    }

    private class MouseButtonCallback extends GLFWMouseButtonCallback {
        @Override
        public void invoke(long windowID, int button, int action, int mods) {
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean ctrl = (mods & GLFW_MOD_CONTROL) != 0;
            boolean alt = (mods & GLFW_MOD_ALT) != 0;

            switch (action) {
                case GLFW_PRESS: {
                    for (MouseListener listener : mouseListeners) {
                        listener.mousePressed(button, shift, ctrl, alt, false, InputManager.this);
                    }
                    break;
                }
                case GLFW_REPEAT: {
                    for (MouseListener listener : mouseListeners) {
                        listener.mousePressed(button, shift, ctrl, alt, true, InputManager.this);
                    }
                    break;
                }
                case GLFW_RELEASE: {
                    for (MouseListener listener : mouseListeners) {
                        listener.mouseReleased(button, shift, ctrl, alt, InputManager.this);
                    }
                    break;
                }
            }
        }
    }

    private class ScrollCallback extends GLFWScrollCallback {
        @Override
        public void invoke(long windowID, double xOffset, double yOffset) {
            for (ScrollListener listener : scrollListeners) {
                listener.scrolled(xOffset, yOffset, InputManager.this);
            }
        }
    }

    private class DropCallback extends GLFWDropCallback {
        @Override
        public void invoke(long windowID, int count, long pathsPointer) {
            String[] filePaths = getNames(count, pathsPointer);

            for (DropListener listener : dropListeners) {
                listener.dropped(filePaths, InputManager.this);
            }
        }
    }

}
