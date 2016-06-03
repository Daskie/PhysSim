package qps.scenes.main;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.utils.Utils;

import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/21/2016
 */
public class MainProgram extends ShaderProgram {

    public MainProgram() {
        super("shaders/main.vert", null, "shaders/main.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "View"), UniformGlobals.ViewGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Light"), UniformGlobals.LightGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ID"), UniformGlobals.IDGlobals.BINDING);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize main program!");
            return false;
        }

        return true;
    }



}
