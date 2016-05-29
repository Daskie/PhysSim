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

    public static Vec3 sphericalToCartesian(float r, float theta, float phi) {
        float sinTheta = (float)Math.sin(theta);
        float cosTheta = (float)Math.cos(theta);
        float sinPhi = (float)Math.sin(phi);
        float cosPhi = (float)Math.cos(phi);

        return new Vec3(
                r * sinPhi * cosTheta,
                r * sinPhi * sinTheta,
                r * cosPhi
        );
    }

    public static <T extends Comparable<T>> T min(T... ts) {
        T min = ts[0];

        for (int i = 1; i < ts.length; ++i) {
            if (ts[i].compareTo(min) < 0) {
                min = ts[i];
            }
        }

        return min;
    }

    public static <T extends Comparable<T>> T max(T... ts) {
        T max = ts[0];

        for (int i = 1; i < ts.length; ++i) {
            if (ts[i].compareTo(max) > 0) {
                max = ts[i];
            }
        }

        return max;
    }

}
