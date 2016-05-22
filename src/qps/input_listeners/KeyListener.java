package qps.input_listeners;

import qps.InputHandler;

/**
 * @since 5/20/2016
 */
public interface KeyListener {

    void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler);

    void keyRepeated(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler);

    void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler);

}
