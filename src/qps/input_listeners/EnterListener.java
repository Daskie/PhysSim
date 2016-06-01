package qps.input_listeners;

import qps.InputManager;

/**
 * @since 5/20/2016
 */
public interface EnterListener {

    void entered(InputManager manager);

    void exited(InputManager manager);

}
