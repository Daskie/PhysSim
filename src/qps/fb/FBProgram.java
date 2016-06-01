package qps.fb;

import qps.ShaderProgram;
import qps.Utils;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

/**
 * @since 5/29/2016
 */
public class FBProgram extends ShaderProgram {

    public FBProgram() {
        super("shaders/fb.vert", null, "shaders/fb.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize fb program!");
            return false;
        }

        return true;
    }

}
