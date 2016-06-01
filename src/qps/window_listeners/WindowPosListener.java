package qps.window_listeners;

import qps.WindowManager;

/**
 * @since 5/20/2016
 */
public interface WindowPosListener {

    void moved(int xPos, int yPos, WindowManager handler);

}
