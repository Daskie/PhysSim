package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface MouseListener {

    void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager);

    void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager);

}
