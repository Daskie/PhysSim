package qps.window_listeners;

import qps.WindowHandler;

/**
 * @since 5/20/2016
 */
public interface WindowFocusListener {

    void focusGained(WindowHandler handler);

    void focusLost(WindowHandler handler);

}
