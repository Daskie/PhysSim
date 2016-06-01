package qps;

import qps.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glUniform1ui;

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

    protected void setUniform(int u_id, int x) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform1i(u_id, x);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, int x, int y) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform2i(u_id, x, y);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, int x, int y, int z) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform3i(u_id, x, y, z);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, int x, int y, int z, int w) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform4i(u_id, x, y, z, w);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, float v) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform1f(u_id, v);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, Vec2 v) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform2f(u_id, v.x, v.y);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, Vec3 v) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform3f(u_id, v.x, v.y, v.z);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, Vec4 v) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        glUniform4f(u_id, v.x, v.y, v.z, v.w);
        glUseProgram(p);
    }

    private ByteBuffer matBuffer = createByteBuffer(64); //enough to store up to a mat4

    protected void setUniform(int u_id, Mat2 mat) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix2fv(u_id, 1, false, matBuffer);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, Mat3 mat) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix3fv(u_id, 1, false, matBuffer);
        glUseProgram(p);
    }

    protected void setUniform(int u_id, Mat4 mat) {
        int p = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(id);
        mat.buffer(matBuffer);
        matBuffer.flip();
        glUniformMatrix4fv(u_id, 1, false, matBuffer);
        glUseProgram(p);
    }

}
