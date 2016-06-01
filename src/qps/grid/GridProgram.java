package qps.grid;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.Utils;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/31/2016
 */
public class GridProgram extends ShaderProgram {

    private int u_divisions;

    public GridProgram() {
        super("shaders/a.vert", null, "shaders/grid.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        u_divisions = glGetUniformLocation(id, "u_divisions");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for main shader program!");
            return false;
        }

        return true;
    }

    public void setDivisions(int divisions) {
        setUniform(u_divisions, divisions);
    }

}
