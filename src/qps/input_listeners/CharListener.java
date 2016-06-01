package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface CharListener {

    void charPressed(char c, boolean shift, boolean ctrl, boolean alt, InputManager manager);
}
