package qps.window_listeners;

import qps.WindowManager;

/**
 * @since 5/20/2016
 */
public interface WindowFocusListener {

    void focusGained(WindowManager handler);

    void focusLost(WindowManager handler);

}
