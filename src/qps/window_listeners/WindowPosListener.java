package qps.window_listeners;

import qps.WindowHandler;

/**
 * @since 5/20/2016
 */
public interface WindowPosListener {

    void moved(int xPos, int yPos, WindowHandler handler);

}
