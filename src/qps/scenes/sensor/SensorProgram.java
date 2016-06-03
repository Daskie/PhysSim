package qps.scenes.sensor;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.utils.Utils;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/21/2016
 */
public class SensorProgram extends ShaderProgram {

    private int u_id;

    public SensorProgram() {
        super("shaders/a.vert", null, "shaders/sensor.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "View"), UniformGlobals.ViewGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Light"), UniformGlobals.LightGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ID"), UniformGlobals.IDGlobals.BINDING);
        u_id = glGetUniformLocation(id, "u_id");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize main program!");
            return false;
        }

        return true;
    }

    public void setID(int id) {
        setUniform(u_id, id);
    }

}
