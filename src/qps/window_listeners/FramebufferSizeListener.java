package qps.window_listeners;

import qps.WindowHandler;

/**
 * @since 5/20/2016
 */
public interface FramebufferSizeListener {

    void resized(int width, int height, WindowHandler handler);

}
