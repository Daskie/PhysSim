package qps;

import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
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
    private int instanceMatsSize;
    private int instanceIDsSize;
    private int instancesSize;
    private int size;

    private int coordsOffset;
    private int colorsOffset;
    private int uvsOffset;
    private int normsOffset;
    private int instanceMatsOffset;
    private int instanceIDsOffset;

    private int vao, vbo, ebo;

    private ByteBuffer buffer;

    public VAO(Mesh mesh, int nInstances, Mat4[] instanceMats, int[] instanceIDs, int usage) {
        coordsSize = mesh.hasCoords() ? mesh.nVerts() * Mesh.COORDS_BYTES : 0;
        colorsSize = mesh.hasColors() ? mesh.nVerts() * Mesh.COLOR_BYTES : 0;
        uvsSize = mesh.hasUVs() ? mesh.nVerts() * Mesh.UV_BYTES : 0;
        normsSize = mesh.hasNorms() ? mesh.nVerts() * Mesh.NORM_BYTES : 0;
        vertsSize = coordsSize + colorsSize + uvsSize + normsSize;
        indicesSize = mesh.nIndices() * 4;
        instanceMatsSize = nInstances * 64;
        instanceIDsSize = nInstances * 4;
        instancesSize = instanceMatsSize + instanceIDsSize;
        size = vertsSize + instancesSize;

        buffer = createByteBuffer(Utils.max(coordsSize, colorsSize, uvsSize, normsSize, instanceMatsSize, instanceIDsSize));

        coordsOffset = 0;
        colorsOffset = coordsOffset + coordsSize;
        uvsOffset = colorsOffset + colorsSize;
        normsOffset = uvsOffset + uvsSize;
        instanceMatsOffset = vertsSize;
        instanceIDsOffset = instanceMatsOffset + instanceMatsSize;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, size, usage);
        if (coordsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, coordsOffset, mesh.coordsData());
        if (colorsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, colorsOffset, mesh.colorsData());
        if (uvsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, uvsOffset, mesh.uvsData());
        if (normsSize > 0) glBufferSubData(GL_ARRAY_BUFFER, normsOffset, mesh.normsData());

        if (nInstances > 0) {
            if (instanceMats != null) {
                buffer.clear();
                for (int i = 0; i < nInstances; ++i) {
                    instanceMats[i].buffer(buffer);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instanceMatsOffset, instanceMatsSize, buffer);
            }
            if (instanceIDs != null) {
                buffer.clear();
                for (int i = 0; i < nInstances; ++i) {
                    buffer.putInt(instanceIDs[i]);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instanceIDsOffset, instanceIDsSize, buffer);
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
        if (nInstances > 0) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    64,
                    instanceMatsOffset
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
                    instanceMatsOffset + 16
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
                    instanceMatsOffset + 32
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
                    instanceMatsOffset + 48
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribIPointer(
                    attribI,
                    1,
                    GL_INT,
                    0,
                    instanceIDsOffset
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

        glMapBufferRange(GL_ARRAY_BUFFER, instanceMatsOffset + i * Mesh.COORDS_BYTES, n * Mesh.COORDS_BYTES, GL_MAP_WRITE_BIT, buffer);
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

        glMapBufferRange(GL_ARRAY_BUFFER, instanceMatsOffset + i * 64, n * 64, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            mats[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance mats!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    void bufferInstanceID(int i, int id) {
        bufferInstanceIDs(i, 1, new int[]{ id });
    }

    void bufferInstanceIDs(int i, int n, int[] ids) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instanceIDsOffset + i * 4, n * 4, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            buffer.putInt(ids[j]);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance ids!");
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
