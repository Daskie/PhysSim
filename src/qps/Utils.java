package qps;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

/**
 * @since 5/21/2016
 */
public abstract class Utils {

    public static boolean checkGLErr() {
        int glErr = glGetError();

        if (glErr != GL_NO_ERROR) {
            System.err.println("OpenGL error: " + glErr);
            return false;
        }

        return true;
    }


}
