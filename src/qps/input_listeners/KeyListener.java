package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface KeyListener {

    void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager);

    void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputManager manager);

}
