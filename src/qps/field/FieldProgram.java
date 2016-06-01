package qps.field;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.Utils;
import qps.Vec3;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/27/2016
 */
public class FieldProgram extends ShaderProgram {

    private int u_fieldLoc;
    private int u_fieldSize;
    private int u_fieldCount;


    public FieldProgram() {
        super("shaders/field.vert", null, "shaders/a.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "View"), UniformGlobals.ViewGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Light"), UniformGlobals.LightGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ChargeCounts"), UniformGlobals.ChargeCountsGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "SphereCharges"), UniformGlobals.SphereChargesGlobals.BINDING);
        u_fieldLoc = glGetUniformLocation(id, "u_fieldLoc");
        u_fieldSize = glGetUniformLocation(id, "u_fieldSize");
        u_fieldCount = glGetUniformLocation(id, "u_fieldCount");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize field program!");
            return false;
        }

        return true;
    }

    public void setFieldLoc(Vec3 loc) {
        setUniform(u_fieldLoc, loc);
    }

    public void setFieldSize(Vec3 size) {
        setUniform(u_fieldSize, size);
    }

    public void setFieldCount(int xCount, int yCount, int zCount) {
        setUniform(u_fieldCount, xCount, yCount, zCount);
    }

}
