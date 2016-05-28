package qps;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/27/2016
 */
public class FieldProgram extends ShaderProgram {

    public static final Vec3 DEFAULT_FIELD_LOC = new Vec3();
    public static final Vec3 DEFAULT_FIELD_SIZE = new Vec3(24.0f, 12.0f, 6.0f);

    private int u_fieldLoc;
    private int u_fieldSize;
    private int u_fieldCount;


    public FieldProgram() {
        super("shaders/field.vert", null, "shaders/a.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "View"), UniformGlobals.ViewGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Transform"), UniformGlobals.TransformGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Light"), UniformGlobals.LightGlobals.BINDING);
        u_fieldLoc = glGetUniformLocation(id, "u_fieldLoc");
        u_fieldSize = glGetUniformLocation(id, "u_fieldSize");
        u_fieldCount = glGetUniformLocation(id, "u_fieldCount");
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for field shader program!");
            return false;
        }

        setFieldLoc(DEFAULT_FIELD_LOC);
        setFieldSize(DEFAULT_FIELD_SIZE);
        setFieldCount((int)DEFAULT_FIELD_SIZE.x + 1, (int)DEFAULT_FIELD_SIZE.y + 1, (int)DEFAULT_FIELD_SIZE.z + 1);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to set initial uniform values for field shader program!");
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

    public void setFieldCount(int x, int y, int z) {
        setUniform(u_fieldCount, x, y, z);
    }

}
