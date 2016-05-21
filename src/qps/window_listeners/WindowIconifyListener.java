package qps.window_listeners;

import qps.WindowHandler;

/**
 * @since 5/20/2016
 */
public interface WindowIconifyListener {

    void minimized(WindowHandler handler);

    void maximized(WindowHandler handler);

}
