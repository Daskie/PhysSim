package qps;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/28/2016
 */
public class CardinalProgram extends ShaderProgram {

    private int u_camLoc;
    private int u_screenPos;
    private int u_lightDir;

    public CardinalProgram() {
        super("shaders/cardinal.vert", null, "shaders/cardinal.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ID"), UniformGlobals.IDGlobals.BINDING);
        u_camLoc = glGetUniformLocation(id, "u_camLoc");
        u_screenPos = glGetUniformLocation(id, "u_screenPos");
        u_lightDir = glGetUniformLocation(id, "u_lightDir");
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for main shader program!");
            return false;
        }

        return true;
    }

    public void setCamLoc(Vec3 camLoc) {
        setUniform(u_camLoc, camLoc);
    }

    public void setScreenPos(Vec2 screenPos) {
        setUniform(u_screenPos, screenPos);
    }

    public void setLightDir(Vec3 lightDir) {
        setUniform(u_lightDir, lightDir);
    }

}