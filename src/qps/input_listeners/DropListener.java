package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface DropListener {

    void dropped(String[] filePaths, InputManager manager);

}
