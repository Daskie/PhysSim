package qps;

import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL20.*;

/**
 * @since 5/21/2016
 */
public class MainProgram extends ShaderProgram {

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

    private int[] uniformIDs;

    public MainProgram() {
        super("shaders/a.vert", null, "shaders/a.frag");
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

}
