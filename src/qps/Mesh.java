package qps;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @since 5/17/2016
 */
public class Mesh {

    private static final int COORDS_BYTES = 3 * 4;
    private static final int COLOR_BYTES = 4;
    private static final int UV_BYTES = 2 * 4;
    private static final int NORM_BYTES = 3 * 4;

    private static class Meta {
        static final int META_BYTES = 20;

        int nVerts;
        int nIndices;
        byte hasCoords, hasColors, hasUVs, hasNorms;
        byte hasTransparency, hasTranslucency, filler0, filler1;
        int nameLength;
    }

    public static Mesh fromFile(String filePath) {

        Meta meta = new Meta();
        String name = filePath;
        FloatBuffer coordsData = null;
        ByteBuffer colorsData = null;
        FloatBuffer uvsData = null;
        FloatBuffer normsData = null;
        IntBuffer indicesData = null;

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            FileChannel channel = file.getChannel();

            ByteBuffer metaByteBuffer = ByteBuffer.allocateDirect(Meta.META_BYTES).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(metaByteBuffer) != Meta.META_BYTES) {
                throw new IOException("Unexpected number of meta bytes read from qmesh!");
            }
            metaByteBuffer.flip();

            meta.nVerts = metaByteBuffer.getInt();
            meta.nIndices = metaByteBuffer.getInt();
            meta.hasCoords = metaByteBuffer.get();
            meta.hasColors = metaByteBuffer.get();
            meta.hasUVs = metaByteBuffer.get();
            meta.hasNorms = metaByteBuffer.get();
            meta.hasTransparency = metaByteBuffer.get();
            meta.hasTranslucency = metaByteBuffer.get();
            meta.filler0 = metaByteBuffer.get();
            meta.filler1 = metaByteBuffer.get();
            meta.nameLength = metaByteBuffer.getInt();

            //verify meta
            if (meta.nVerts < 1 || meta.nIndices < 1 || meta.nameLength < 0) {
                throw new IOException("Invalid qmesh meta!");
            }

            if (meta.nameLength > 0) {
                ByteBuffer nameBuffer = ByteBuffer.allocateDirect(meta.nameLength).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(nameBuffer) != meta.nameLength) {
                    throw new IOException("Unexpected number of name bytes read from qmesh!");
                }
                nameBuffer.flip();
                byte[] tempBuff = new byte[meta.nameLength];
                nameBuffer.get(tempBuff);
                name = new String(tempBuff, Charset.forName("UTF-8"));
            }

            int namePaddBytes = (4 - meta.nameLength % 4) % 4;
            channel.position(channel.position() + namePaddBytes);

            if (meta.hasCoords != 0) {
                int coordsBytes = meta.nVerts * COORDS_BYTES;
                ByteBuffer tempCoordsBuff = ByteBuffer.allocateDirect(coordsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempCoordsBuff) != coordsBytes) {
                    throw new IOException("Unexpected number of coords bytes read from qmesh!");
                }
                tempCoordsBuff.flip();
                coordsData = tempCoordsBuff.asFloatBuffer();
            }

            if (meta.hasColors != 0) {
                int colorsBytes = meta.nVerts * COLOR_BYTES;
                ByteBuffer tempColordsBuff = ByteBuffer.allocateDirect(colorsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempColordsBuff) != colorsBytes) {
                    throw new IOException("Unexpected number of colors bytes read from qmesh!");
                }
                tempColordsBuff.flip();
                colorsData = tempColordsBuff;
            }

            if (meta.hasUVs != 0) {
                int uvsBytes = meta.nVerts * UV_BYTES;
                ByteBuffer tempUVsBuffer = ByteBuffer.allocateDirect(uvsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempUVsBuffer) != uvsBytes) {
                    throw new IOException("Unexpected number of uvs bytes read from qmesh!");
                }
                tempUVsBuffer.flip();
                uvsData = tempUVsBuffer.asFloatBuffer();
            }

            if (meta.hasNorms != 0) {
                int normsBytes = meta.nVerts * NORM_BYTES;
                ByteBuffer tempNormsBuff = ByteBuffer.allocateDirect(normsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempNormsBuff) != normsBytes) {
                    throw new IOException("Unexpected number of norms bytes read from qmesh!");
                }
                tempNormsBuff.flip();
                normsData = tempNormsBuff.asFloatBuffer();
            }

            int indicesBytes = meta.nIndices * 4;
            ByteBuffer tempIndicesBuff = ByteBuffer.allocateDirect(indicesBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(tempIndicesBuff) != indicesBytes) {
                throw new IOException("Unexpected number of indices bytes read from qmesh!");
            }
            tempIndicesBuff.flip();
            indicesData = tempIndicesBuff.asIntBuffer();

            channel.close();
            file.close();

        } catch (IOException e) {
            System.err.println("Failed to read file " + filePath + "!");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return new Mesh(
                name,
                meta.nVerts,
                coordsData,
                colorsData,
                uvsData,
                normsData,
                meta.nIndices,
                indicesData
        );
    }

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

    VAO buffer(int nInstances, Mat4[] instanceMats) {

        int coordsSize = hasCoords() ? nVerts * COORDS_BYTES : 0;
        int colorsSize = hasColors() ? nVerts * COLOR_BYTES : 0;
        int uvsSize = hasUVs() ? nVerts * UV_BYTES : 0;
        int normsSize = hasNorms() ? nVerts * NORM_BYTES : 0;
        int vertsSize = coordsSize + colorsSize + uvsSize + normsSize;
        int indicesSize = nIndices * 4;
        int instancesSize = nInstances * 64;

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

        glBufferData(GL_ARRAY_BUFFER, vertsSize, GL_STATIC_DRAW);
        if (hasCoords()) glBufferSubData(GL_ARRAY_BUFFER, coordsOffset, coordsData);
        if (hasColors()) glBufferSubData(GL_ARRAY_BUFFER, colorsOffset, colorsData);
        if (hasUVs()) glBufferSubData(GL_ARRAY_BUFFER, uvsOffset, uvsData);
        if (hasNorms()) glBufferSubData(GL_ARRAY_BUFFER, normsOffset, normsData);

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
        if (nInstances > 0) {
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

}
