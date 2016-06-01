package qps.window_listeners;

import qps.WindowManager;

/**
 * @since 5/20/2016
 */
public interface WindowIconifyListener {

    void minimized(WindowManager handler);

    void maximized(WindowManager handler);

}
