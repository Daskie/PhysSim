package qps.scenes.hud;

import qps.*;
import qps.utils.Utils;
import qps.utils.Vec2;
import qps.utils.Vec3;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/28/2016
 */
public class HUDProgram extends ShaderProgram {

    private int u_camLoc;
    private int u_screenPos;
    private int u_lightDir;
    private int u_id;
    private int u_lightColor;
    private int u_lightStrength;

    public HUDProgram() {
        super("shaders/hud.vert", null, "shaders/cardinal.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Light"), UniformGlobals.LightGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ID"), UniformGlobals.IDGlobals.BINDING);
        u_camLoc = glGetUniformLocation(id, "u_camLoc");
        u_screenPos = glGetUniformLocation(id, "u_screenPos");
        u_lightDir = glGetUniformLocation(id, "u_lightDir");
        u_id = glGetUniformLocation(id, "u_id");
        u_lightColor = glGetUniformLocation(id, "u_lightColor");
        u_lightStrength = glGetUniformLocation(id, "u_lightStrength");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize cardinal program!");
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

    public void setID(int id) {
        setUniform(u_id, id);
    }

    public void setLightColor(Vec3 color) {
        setUniform(u_lightColor, color);
    }

    public void setLightStrength(float strength) {
        setUniform(u_lightStrength, strength);
    }

}
