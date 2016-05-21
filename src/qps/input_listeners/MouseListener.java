package qps.input_listeners;

import qps.InputHandler;

/**
 * @since 5/20/2016
 */
public interface MouseListener {

    void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, InputHandler handler);

    void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputHandler handler);

}
