package qps;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

/**
 * @since 5/27/2016
 */
public class FieldProgram extends ShaderProgram {

    public static final Vec3 DEFAULT_FIELD_LOC = new Vec3();
    public static final Vec3 DEFAULT_FIELD_SIZE = new Vec3(24.0f, 12.0f, 6.0f);

    private static final Vec3 DEFAULT_CAM_LOC = new Vec3();
    private static final Vec3 DEFAULT_LIGHT_DIR = new Vec3(-1.0f, -1.0f, -1.0f);
    private static final Vec3 DEFAULT_AMBIENT_COL = new Vec3(0.1f, 0.1f, 0.1f);
    private static final Vec3 DEFAULT_DIFFUSE_COL = new Vec3(1.0f, 1.0f, 1.0f);
    private static final Vec3 DEFAULT_SPECULAR_COL = new Vec3(1.0f, 1.0f, 1.0f);

    private int u_modelMat;
    private int u_normMat;
    private int u_viewMat;
    private int u_projMat;
    private int u_camLoc;
    private int u_lightDir;
    private int u_ambientCol;
    private int u_diffuseCol;
    private int u_specularCol;
    private int u_fieldLoc;
    private int u_fieldSize;
    private int u_fieldCount;


    public FieldProgram() {
        super("shaders/field.vert", null, "shaders/a.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        u_modelMat = glGetUniformLocation(id, "u_modelMat");
        u_normMat = glGetUniformLocation(id, "u_normMat");
        u_viewMat = glGetUniformLocation(id, "u_viewMat");
        u_projMat = glGetUniformLocation(id, "u_projMat");
        u_camLoc = glGetUniformLocation(id, "u_camLoc");
        u_lightDir = glGetUniformLocation(id, "u_lightDir");
        u_ambientCol = glGetUniformLocation(id, "u_ambientCol");
        u_diffuseCol = glGetUniformLocation(id, "u_diffuseCol");
        u_specularCol = glGetUniformLocation(id, "u_specularCol");
        u_fieldLoc = glGetUniformLocation(id, "u_fieldLoc");
        u_fieldSize = glGetUniformLocation(id, "u_fieldSize");
        u_fieldCount = glGetUniformLocation(id, "u_fieldCount");
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for main shader program!");
            return false;
        }

        setModelMat(new Mat4());
        setViewMat(new Mat4());
        setProjMat(new Mat4());
        setCamLoc(DEFAULT_CAM_LOC);
        setLightDir(DEFAULT_LIGHT_DIR);
        setAmbientCol(DEFAULT_AMBIENT_COL);
        setDiffuseCol(DEFAULT_DIFFUSE_COL);
        setSpecularCol(DEFAULT_SPECULAR_COL);
        setFieldLoc(DEFAULT_FIELD_LOC);
        setFieldSize(DEFAULT_FIELD_SIZE);
        setFieldCount((int)DEFAULT_FIELD_SIZE.x + 1, (int)DEFAULT_FIELD_SIZE.y + 1, (int)DEFAULT_FIELD_SIZE.z + 1);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to set initial uniform values for main shader program!");
            return false;
        }

        return true;
    }

    public void setModelMat(Mat4 mat) {
        setUniform(u_modelMat, mat);
        setUniform(u_normMat, new Mat4((new Mat3(mat)).inv().trans()));
    }

    public void setViewMat(Mat4 mat) {
        setUniform(u_viewMat, mat);
    }

    public void setProjMat(Mat4 mat) {
        setUniform(u_projMat, mat);
    }

    public void setCamLoc(Vec3 camLoc) {
        setUniform(u_camLoc, camLoc);
    }

    public void setLightDir(Vec3 lightDir) {
        setUniform(u_lightDir, lightDir);
    }

    public void setAmbientCol(Vec3 ambientCol) {
        setUniform(u_ambientCol, ambientCol);
    }

    public void setDiffuseCol(Vec3 diffuseCol) {
        setUniform(u_diffuseCol, diffuseCol);
    }

    public void setSpecularCol(Vec3 specularCol) {
        setUniform(u_specularCol, specularCol);
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
