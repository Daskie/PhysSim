package qps.input_listeners;

import qps.InputHandler;

/**
 * @since 5/20/2016
 */
public interface DropListener {

    void dropped(String[] filePaths, InputHandler handler);

}
