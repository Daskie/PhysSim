package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface ScrollListener {

    void scrolled(double xScroll, double yScroll, InputManager manager);

}
