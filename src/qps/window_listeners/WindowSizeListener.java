package qps.window_listeners;

import qps.WindowManager;

/**
 * @since 5/20/2016
 */
public interface WindowSizeListener {

    void resized(int width, int height, WindowManager handler);

}
