package qps;

import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @since 5/17/2016
 */

public class VAO {

    private int coordsSize;
    private int colorsSize;
    private int uvsSize;
    private int normsSize;
    private int vertsSize;
    private int indicesSize;
    private int instancesSize;
    private int coordsOffset;
    private int colorsOffset;
    private int uvsOffset;
    private int normsOffset;
    private int instancesOffset;

    private int vao, vbo, ebo;

    private ByteBuffer buffer;

    public VAO(Mesh mesh, int nInstances, Mat4[] instanceMats, int usage) {
        coordsSize = mesh.hasCoords() ? mesh.nVerts() * Mesh.COORDS_BYTES : 0;
        colorsSize = mesh.hasColors() ? mesh.nVerts() * Mesh.COLOR_BYTES : 0;
        uvsSize = mesh.hasUVs() ? mesh.nVerts() * Mesh.UV_BYTES : 0;
        normsSize = mesh.hasNorms() ? mesh.nVerts() * Mesh.NORM_BYTES : 0;
        vertsSize = coordsSize + colorsSize + uvsSize + normsSize;
        indicesSize = mesh.nIndices() * 4;
        instancesSize = nInstances * 64;

        buffer = createByteBuffer(Utils.max(coordsSize, instancesSize));

        coordsOffset = 0;
        colorsOffset = coordsOffset + coordsSize;
        uvsOffset = colorsOffset + colorsSize;
        normsOffset = uvsOffset + uvsSize;
        instancesOffset = vertsSize;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, vertsSize + instancesSize, usage);
        if (coordsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, coordsOffset, mesh.coordsData());
        if (colorsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, colorsOffset, mesh.colorsData());
        if (uvsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, uvsOffset, mesh.uvsData());
        if (normsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, normsOffset, mesh.normsData());

        if (instancesSize > 0) {
            if (instanceMats != null) {
                for (Mat4 mat : instanceMats) {
                    mat.buffer(buffer);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instancesOffset, buffer);
            }
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.indicesData(), GL_STATIC_DRAW);

        int attribI = 0;
        if (coordsSize > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,                        // attribute 0. Match the layout in the shader.
                    3,                              // size
                    GL_FLOAT,                       // type
                    false,                          // normalized?
                    0,                              // stride
                    coordsOffset                    // array buffer offset
            );
            ++attribI;
        }
        if (colorsSize > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_UNSIGNED_BYTE,
                    true,
                    0,
                    colorsOffset
            );
            ++attribI;
        }
        if (uvsSize > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    2,
                    GL_FLOAT,
                    false,
                    0,
                    uvsOffset
            );
            ++attribI;
        }
        if (normsSize > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    3,
                    GL_FLOAT,
                    false,
                    0,
                    normsOffset
            );
            ++attribI;
        }
        if (instancesSize > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    64,
                    instancesOffset
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    64,
                    instancesOffset + 16
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    64,
                    instancesOffset + 32
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    64,
                    instancesOffset + 48
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;
        }

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    void bufferCoords(int i, int n, Vec3[] coords) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instancesOffset + i * Mesh.COORDS_BYTES, n * Mesh.COORDS_BYTES, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            coords[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer coords!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    void bufferInstanceMat(int i, Mat4 mat) {
        bufferInstanceMats(i, 1, new Mat4[]{ mat });
    }

    void bufferInstanceMats(int i, int n, Mat4[] mats) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instancesOffset + i * 64, n * 64, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            mats[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance mats!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int vao() {
        return vao;
    }

    public int vbo() {
        return vbo;
    }

    public int ebo() {
        return ebo;
    }

}
