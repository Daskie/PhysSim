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
import static org.lwjgl.opengl.GL31.GL_COPY_READ_BUFFER;
import static org.lwjgl.opengl.GL31.GL_COPY_WRITE_BUFFER;
import static org.lwjgl.opengl.GL31.glCopyBufferSubData;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @since 5/17/2016
 */

public class VAO {

    private int nInstances;

    private int coordsSize;
    private int colorsSize;
    private int uvsSize;
    private int normsSize;
    private int vertsSize;
    private int indicesSize;
    private int instanceModelMatsSize;
    private int instanceNormMatsSize;
    private int instanceChargesSize;
    private int instanceIDsSize;
    private int instancesSize;
    private int size;

    private int coordsOffset;
    private int colorsOffset;
    private int uvsOffset;
    private int normsOffset;
    private int instanceModelMatsOffset;
    private int instanceNormMatsOffset;
    private int instanceChargesOffset;
    private int instanceIDsOffset;

    private int vao, vbo, ebo;

    private ByteBuffer buffer;

    public VAO(Mesh mesh, int nInstances, Mat4[] instanceModelMats, Mat4[] instanceNormMats, float[] instanceCharges, int[] instanceIDs, int usage) {
        this.nInstances = nInstances;
        coordsSize = mesh.hasCoords() ? mesh.nVerts() * Mesh.COORDS_BYTES : 0;
        colorsSize = mesh.hasColors() ? mesh.nVerts() * Mesh.COLOR_BYTES : 0;
        uvsSize = mesh.hasUVs() ? mesh.nVerts() * Mesh.UV_BYTES : 0;
        normsSize = mesh.hasNorms() ? mesh.nVerts() * Mesh.NORM_BYTES : 0;
        vertsSize = coordsSize + colorsSize + uvsSize + normsSize;
        indicesSize = mesh.nIndices() * 4;
        instanceModelMatsSize = nInstances * 64;
        instanceNormMatsSize = nInstances * 64;
        instanceChargesSize = nInstances * 4;
        instanceIDsSize = nInstances * 4;
        instancesSize = instanceModelMatsSize + instanceNormMatsSize + instanceChargesSize + instanceIDsSize;
        size = vertsSize + instancesSize;

        buffer = createByteBuffer(Utils.max(coordsSize, colorsSize, uvsSize, normsSize, instanceModelMatsSize, instanceNormMatsSize, instanceChargesSize, instanceIDsSize));

        int offset = 0;
        coordsOffset = offset; offset += coordsSize;
        colorsOffset = offset; offset += colorsSize;
        uvsOffset = offset; offset += uvsSize;
        normsOffset = offset; offset += normsSize;
        offset = vertsSize;
        instanceModelMatsOffset = offset; offset += instanceModelMatsSize;
        instanceNormMatsOffset = offset; offset += instanceNormMatsSize;
        instanceChargesOffset = offset; offset += instanceChargesSize;
        instanceIDsOffset = offset; offset += instanceIDsSize;

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
            if (instanceModelMats != null) {
                buffer.clear();
                for (int i = 0; i < nInstances; ++i) {
                    instanceModelMats[i].buffer(buffer);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instanceModelMatsOffset, instanceModelMatsSize, buffer);
            }
            if (instanceNormMats != null) {
                buffer.clear();
                for (int i = 0; i < nInstances; ++i) {
                    instanceNormMats[i].buffer(buffer);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instanceNormMatsOffset, instanceNormMatsSize, buffer);
            }
            if (instanceCharges != null) {
                buffer.clear();
                for (int i = 0; i < nInstances; ++i) {
                    buffer.putFloat(instanceCharges[i]);
                }
                buffer.flip();
                glBufferSubData(GL_ARRAY_BUFFER, instanceChargesOffset, instanceChargesSize, buffer);
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
                    instanceModelMatsOffset
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
                    instanceModelMatsOffset + 16
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
                    instanceModelMatsOffset + 32
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
                    instanceModelMatsOffset + 48
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
                    instanceNormMatsOffset
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
                    instanceNormMatsOffset + 16
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
                    instanceNormMatsOffset + 32
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
                    instanceNormMatsOffset + 48
            );
            glVertexAttribDivisor(attribI, 1);
            ++attribI;

            glEnableVertexAttribArray(attribI);
            glVertexAttribPointer(
                    attribI,
                    1,
                    GL_FLOAT,
                    false,
                    0,
                    instanceChargesOffset
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

    public void bufferCoords(int i, Vec3 coords) {
        bufferCoords(i, 1, new Vec3[]{ coords });
    }

    public void bufferCoords(int i, int n, Vec3[] coords) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, coordsOffset + i * Mesh.COORDS_BYTES, n * Mesh.COORDS_BYTES, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            coords[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer coords!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferColor(int i, Vec4 color) {
        bufferColors(i, 1, new Vec4[]{ color });
    }

    public void bufferColors(int i, int n, Vec4[] colors) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, colorsOffset + i * Mesh.COLOR_BYTES, n * Mesh.COLOR_BYTES, GL_MAP_WRITE_BIT, buffer);

        for (int j = 0; j < n; ++j) {
            buffer.put((byte)Math.round(colors[j].x * 255.0f)).put((byte)Math.round(colors[j].y * 255.0f)).put((byte)Math.round(colors[j].z * 255.0f)).put((byte)Math.round(colors[j].w * 255.0f));
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer colors!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferColors(int i, int n, Vec4 color) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, colorsOffset + i * Mesh.COLOR_BYTES, n * Mesh.COLOR_BYTES, GL_MAP_WRITE_BIT, buffer);

        byte red = (byte)Math.round(color.x * 255.0f);
        byte green = (byte)Math.round(color.y * 255.0f);
        byte blue = (byte)Math.round(color.z * 255.0f);
        byte alpha = (byte)Math.round(color.w * 255.0f);

        for (int j = 0; j < n; ++j) {
            buffer.put(red).put(green).put(blue).put(alpha);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer colors!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferInstanceModelMat(int i, Mat4 mat) {
        bufferInstanceModelMats(i, 1, new Mat4[]{ mat });
    }

    public void bufferInstanceModelMats(int i, int n, Mat4[] mats) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instanceModelMatsOffset + i * 64, n * 64, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            mats[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance model mats!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferInstanceNormMat(int i, Mat4 mat) {
        bufferInstanceNormMats(i, 1, new Mat4[]{ mat });
    }

    public void bufferInstanceNormMats(int i, int n, Mat4[] mats) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instanceNormMatsOffset + i * 64, n * 64, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            mats[j].buffer(buffer);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance norm mats!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferInstanceCharge(int i, float charge) {
        bufferInstanceCharges(i, 1, new float[]{ charge });
    }

    public void bufferInstanceCharges(int i, int n, float[] charges) {
        buffer.clear();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glMapBufferRange(GL_ARRAY_BUFFER, instanceChargesOffset + i * 4, n * 4, GL_MAP_WRITE_BIT, buffer);
        for (int j = 0; j < n; ++j) {
            buffer.putFloat(charges[j]);
        }
        glUnmapBuffer(GL_ARRAY_BUFFER);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to buffer instance charges!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferInstanceID(int i, int id) {
        bufferInstanceIDs(i, 1, new int[]{ id });
    }

    public void bufferInstanceIDs(int i, int n, int[] ids) {
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

    public void removeInstance(int i) {
        if (i >= nInstances) {
            return;
        }

        glBindBuffer(GL_COPY_READ_BUFFER, vbo);
        glBindBuffer(GL_COPY_WRITE_BUFFER, vbo);

        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, instanceModelMatsOffset + (i + 1) * 64, instanceModelMatsOffset + i * 64, 64);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, instanceNormMatsOffset + (i + 1) * 64, instanceNormMatsOffset + i * 64, 64);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, instanceChargesOffset + (i + 1) * 4, instanceChargesOffset + i * 4, 4);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, instanceIDsOffset + (i + 1) * 4, instanceIDsOffset + i * 4, 4);

        glBindBuffer(GL_COPY_READ_BUFFER, 0);
        glBindBuffer(GL_COPY_WRITE_BUFFER, 0);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to remove instance!");
        }
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
