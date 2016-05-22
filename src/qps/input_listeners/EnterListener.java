package qps.input_listeners;

import qps.InputHandler;

/**
 * @since 5/20/2016
 */
public interface EnterListener {

    void entered(InputHandler handler);

    void exited(InputHandler handler);

}