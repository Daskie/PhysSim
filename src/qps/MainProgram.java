package qps;

import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL20.*;

/**
 * @since 5/21/2016
 */
public class MainProgram extends ShaderProgram {

    private FloatBuffer matBuffer;

    private int u_modelMat;
    private int u_normMat;
    private int u_viewMat;
    private int u_projMat;

    public MainProgram() {
        super("shaders/a.vert", null, "shaders/a.frag");

        matBuffer = createFloatBuffer(16);
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        u_modelMat = glGetUniformLocation(id, "u_modelMat");
        u_normMat = glGetUniformLocation(id, "u_normMat");
        u_viewMat = glGetUniformLocation(id, "u_viewMat");
        u_projMat = glGetUniformLocation(id, "u_projMat");
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for main shader program!");
            return false;
        }

        setModelMat(new Mat4());
        setViewMat(new Mat4());
        setProjMat(new Mat4());
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to set initial uniform values for main shader program!");
            return false;
        }

        return true;
    }

    public void setModelMat(Mat4 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_modelMat, false, matBuffer);
        (new MatN(4, 4, (new MatN(3, 3, mat)).inv().trans())).buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_normMat, false, matBuffer);
    }

    public void setViewMat(Mat4 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_viewMat, false, matBuffer);
    }

    public void setProjMat(Mat4 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_projMat, false, matBuffer);
    }

}
