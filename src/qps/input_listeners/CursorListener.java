package qps.input_listeners;

import qps.InputHandler;

/**
 * @since 5/20/2016
 */
public interface CursorListener {

    void cursorMoved(double x, double y, double dx, double dy, InputHandler handler);

}
