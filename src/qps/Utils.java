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

    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    public static double clamp(double val, double min, double max) {
        return val < min ? min : val > max ? max : val;
    }

}
