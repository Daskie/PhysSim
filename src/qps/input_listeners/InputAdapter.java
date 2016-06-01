package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/31/2016
 */
public abstract class InputAdapter implements AllInputListener {

    @Override
    public void charPressed(char c, boolean shift, boolean ctrl, boolean alt, InputManager manager) {}

    @Override
    public void cursorMoved(double x, double y, double dx, double dy, InputManager manager) {}

    @Override
    public void dropped(String[] filePaths, InputManager manager) {}

    @Override
    public void entered(InputManager manager) {}

    @Override
    public void exited(InputManager manager) {}

    @Override
    public void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {}

    @Override
    public void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputManager manager) {}

    @Override
    public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {}

    @Override
    public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {}

    @Override
    public void scrolled(double xScroll, double yScroll, InputManager manager) {}

}
