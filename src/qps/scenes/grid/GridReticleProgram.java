package qps.scenes.grid;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.utils.Utils;
import qps.utils.Vec4;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 6/1/2016
 */
public class GridReticleProgram extends ShaderProgram {

    private int u_color;

    public GridReticleProgram() {
        super("shaders/i.vert", null, "shaders/color.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        u_color = glGetUniformLocation(id, "u_color");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize grid reticle program!");
            return false;
        }

        return true;
    }

    public void setColor(Vec4 color) {
        setUniform(u_color, color);
    }

}
