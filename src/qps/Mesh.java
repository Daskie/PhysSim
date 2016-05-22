package qps;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @since 5/17/2016
 */
public class Mesh {

    public static final int COORDS_BYTES = 3 * 4;
    public static final int COLOR_BYTES = 4;
    public static final int UV_BYTES = 2 * 4;
    public static final int NORM_BYTES = 3 * 4;

    private String name;
    private int nVerts;
    private FloatBuffer coordsData;
    private ByteBuffer colorsData;
    private FloatBuffer uvsData;
    private FloatBuffer normsData;
    private int nIndices;
    private IntBuffer indicesData;

    public Mesh(String name, int nVerts, FloatBuffer coordsData, ByteBuffer colorsData, FloatBuffer uvsData, FloatBuffer normsData, int nIndices, IntBuffer indicesData) {
        this.name = name;
        this.nVerts = nVerts;
        this.coordsData = coordsData;
        this.colorsData = colorsData;
        this.uvsData = uvsData;
        this.normsData = normsData;
        this.nIndices = nIndices;
        this.indicesData = indicesData;
    }

    boolean hasCoords() {
        return coordsData != null;
    }

    boolean hasColors() {
        return colorsData != null;
    }

    boolean hasUVs() {
        return uvsData != null;
    }

    boolean hasNorms() {
        return normsData != null;
    }

    public VAO buffer() {
        return buffer(null);
    }

    public VAO buffer(Mat4[] instanceMats) {
        boolean instanced = instanceMats != null && instanceMats.length > 0;

        int coordsSize = hasCoords() ? nVerts * COORDS_BYTES : 0;
        int colorsSize = hasColors() ? nVerts * COLOR_BYTES : 0;
        int uvsSize = hasUVs() ? nVerts * UV_BYTES : 0;
        int normsSize = hasNorms() ? nVerts * NORM_BYTES : 0;
        int vertsSize = coordsSize + colorsSize + uvsSize + normsSize;
        int indicesSize = nIndices * 4;
        int instancesSize = instanced ? instanceMats.length * 64 : 0;

        int coordsOffset = 0;
        int colorsOffset = coordsOffset + coordsSize;
        int uvsOffset = colorsOffset + colorsSize;
        int normsOffset = uvsOffset + uvsSize;
        int indicesOffset = vertsSize;
        int instancesOffset = vertsSize + indicesSize;

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, vertsSize + instancesSize, GL_STATIC_DRAW);
        if (hasCoords()) glBufferSubData(GL_ARRAY_BUFFER, coordsOffset, coordsData);
        if (hasColors()) glBufferSubData(GL_ARRAY_BUFFER, colorsOffset, colorsData);
        if (hasUVs()) glBufferSubData(GL_ARRAY_BUFFER, uvsOffset, uvsData);
        if (hasNorms()) glBufferSubData(GL_ARRAY_BUFFER, normsOffset, normsData);

        if (instanced) {
            FloatBuffer matData = createFloatBuffer(instanceMats.length * 16);
            for (Mat4 mat : instanceMats) {
                mat.buffer(matData);
            }
            matData.flip();
            glBufferSubData(GL_ARRAY_BUFFER, instancesOffset, matData);
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesData, GL_STATIC_DRAW);

        int attribI = 0;
        if (hasCoords()) {
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
        if (hasColors()) {
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
        if (hasUVs()) {
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
        if (hasNorms()) {
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
        if (instanced) {
            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    16,
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
                    16,
                    instancesOffset + 4
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset + 8
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset + 12
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        return new VAO(vao, vbo, ebo);
    }

    int nVerts() {
        return nVerts;
    }

    int nIndices() {
        return nIndices;
    }

    IntBuffer indices() {
        return indicesData;
    }

}
