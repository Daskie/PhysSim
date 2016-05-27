package qps;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL20.*;

/**
 * @since 5/20/2016
 */
public class ShaderProgram {


    protected String vertFilePath, geomFilePath, fragFilePath;
    protected int id;

    public ShaderProgram(String vertFilePath, String geomFilePath, String fragFilePath) {
        this.vertFilePath = vertFilePath;
        this.geomFilePath = geomFilePath;
        this.fragFilePath = fragFilePath;
    }

    public boolean init() {
        id = ShaderLoader.createProgram(vertFilePath, geomFilePath, fragFilePath);

        if (id == 0) {
            System.err.println("Failed to initialize shader program!");
            return false;
        }

        return true;
    }

    public int id() {
        return id;
    }

    protected void setUniform(int u_id, float v) {
        glUseProgram(id);
        glUniform1f(u_id, v);
    }

    protected void setUniform(int u_id, Vec2 v) {
        glUseProgram(id);
        glUniform2f(u_id, v.x, v.y);
    }

    protected void setUniform(int u_id, Vec3 v) {
        glUseProgram(id);
        glUniform3f(u_id, v.x, v.y, v.z);
    }

    protected void setUniform(int u_id, Vec4 v) {
        glUseProgram(id);
        glUniform4f(u_id, v.x, v.y, v.z, v.w);
    }

    private FloatBuffer matBuffer = createFloatBuffer(16); //enough to store up to a mat4

    protected void setUniform(int u_id, Mat2 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix2fv(u_id, false, matBuffer);
    }

    protected void setUniform(int u_id, Mat3 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix3fv(u_id, false, matBuffer);
    }

    protected void setUniform(int u_id, Mat4 mat) {
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_id, false, matBuffer);
    }

    protected void setUniform(int u_id, int x) {
        glUseProgram(id);
        glUniform1i(u_id, x);
    }

    protected void setUniform(int u_id, int x, int y) {
        glUseProgram(id);
        glUniform2i(u_id, x, y);
    }

    protected void setUniform(int u_id, int x, int y, int z) {
        glUseProgram(id);
        glUniform3i(u_id, x, y, z);
    }

    protected void setUniform(int u_id, int x, int y, int z, int w) {
        glUseProgram(id);
        glUniform4i(u_id, x, y, z, w);
    }

}
