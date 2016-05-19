import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @since 5/17/2016
 */
public class QMesh {

    private static final int COORDS_BYTES = 3 * 4;
    private static final int COLOR_BYTES = 4 * 4;
    private static final int UV_BYTES = 2 * 4;
    private static final int NORM_BYTES = 3 * 4;
    private static final int VERT_BYTES = COORDS_BYTES + COLOR_BYTES + UV_BYTES + NORM_BYTES;

    private static class Meta {
        int nVerts;
        int nIndices;
        byte hasCoords, hasColors, hasUVs, hasNorms;
        byte hasTransparency, hasTranslucency;
        int nameLength;
    }

    public static QMesh fromFile(String filePath) {

        Meta meta = new Meta();
        String name = filePath;
        ByteBuffer coordsData = null;
        ByteBuffer colorsData = null;
        ByteBuffer uvsData = null;
        ByteBuffer normsData = null;
        ByteBuffer indicesData = null;

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            FileChannel channel = file.getChannel();

            meta.nVerts = file.readInt();
            meta.nIndices = file.readInt();
            meta.hasCoords = file.readByte();
            meta.hasColors = file.readByte();
            meta.hasUVs = file.readByte();
            meta.hasNorms = file.readByte();
            meta.hasTransparency = file.readByte();
            meta.hasTranslucency = file.readByte();
            meta.nameLength = file.readInt();

            //verify meta
            if (meta.nVerts < 1 || meta.nIndices < 1 || meta.hasCoords == 0 || meta.hasColors == 0 || meta.hasUVs == 0 || meta.hasNorms == 0 || meta.nameLength < 0) {
                throw new IOException("Invalid qmesh meta!");
            }

            if (meta.nameLength > 0) {
                ByteBuffer nameBuffer = ByteBuffer.allocateDirect(meta.nameLength);
                if (channel.read(nameBuffer) != meta.nameLength) {
                    throw new IOException("Unexpected number of name bytes read from qmesh!");
                }
                nameBuffer.flip();
                name = new String(nameBuffer.array(), Charset.forName("UTF-8"));
            }

            if (meta.hasCoords != 0) {
                int coordsBytes = meta.nVerts * COORDS_BYTES;
                coordsData = ByteBuffer.allocateDirect(coordsBytes);
                if (channel.read(coordsData) != coordsBytes) {
                    throw new IOException("Unexpected number of coords bytes read from qmesh!");
                }
                coordsData.flip();
            }

            if (meta.hasColors != 0) {
                int colorsBytes = meta.nVerts * COLOR_BYTES;
                colorsData = ByteBuffer.allocateDirect(colorsBytes);
                if (channel.read(colorsData) != colorsBytes) {
                    throw new IOException("Unexpected number of colors bytes read from qmesh!");
                }
                colorsData.flip();
            }

            if (meta.hasUVs != 0) {
                int uvsBytes = meta.nVerts * UV_BYTES;
                uvsData = ByteBuffer.allocateDirect(uvsBytes);
                if (channel.read(uvsData) != uvsBytes) {
                    throw new IOException("Unexpected number of uvs bytes read from qmesh!");
                }
                uvsData.flip();
            }

            if (meta.hasNorms != 0) {
                int normsBytes = meta.nVerts * NORM_BYTES;
                normsData = ByteBuffer.allocateDirect(normsBytes);
                if (channel.read(normsData) != normsBytes) {
                    throw new IOException("Unexpected number of norms bytes read from qmesh!");
                }
                normsData.flip();
            }

            int indicesBytes = meta.nIndices * 4;
            indicesData = ByteBuffer.allocateDirect(indicesBytes);
            if (channel.read(indicesData) != indicesBytes) {
                throw new IOException("Unexpected number of indices bytes read from qmesh!");
            }
            indicesData.flip();

            channel.close();
            file.close();

        } catch (IOException e) {
            System.err.println("Failed to read file " + filePath + "!");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return new QMesh(name, meta.nVerts, coordsData, colorsData, uvsData, normsData, meta.nIndices, indicesData);
    }

    private String name;
    private int nVerts;
    private ByteBuffer coordsData, colorsData, uvsData, normsData;
    private int nIndices;
    private ByteBuffer indicesData;

    public QMesh(String name, int nVerts, ByteBuffer coordsData, ByteBuffer colorsData, ByteBuffer uvsData, ByteBuffer normsData, int nIndices, ByteBuffer indicesData) {
        this.nVerts = nVerts;
        this.coordsData = coordsData;
        this.colorsData = colorsData;
        this.uvsData = uvsData;
        this.normsData = normsData;
        this.nIndices = nIndices;
        this.indicesData = indicesData;
    }

    QVAO buffer(int nInstances, QMat4[] instanceMats) {

        int coordsSize = nVerts * COORDS_BYTES;
        int colorsSize = nVerts * COLOR_BYTES;
        int uvsSize = nVerts * UV_BYTES;
        int normsSize = nVerts * NORM_BYTES;
        int vertsSize = nVerts * VERT_BYTES;
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
        glBufferSubData(GL_ARRAY_BUFFER, coordsOffset, coordsData);
        glBufferSubData(GL_ARRAY_BUFFER, colorsOffset, colorsData);
        glBufferSubData(GL_ARRAY_BUFFER, uvsOffset, uvsData);
        glBufferSubData(GL_ARRAY_BUFFER, normsOffset, normsData);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesData, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(
                0,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                coordsOffset                       		// array buffer offset
        );
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(
                1,										// attribute 0. Match the layout in the shader.
                4,				                        // size
                GL_UNSIGNED_BYTE,					    // type
                true,								    // normalized?
                0,										// stride
                colorsOffset                            // array buffer offset
        );
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(
                2,										// attribute 0. Match the layout in the shader.
                2,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                uvsOffset                       		// array buffer offset
        );
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(
                3,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                normsOffset                       		// array buffer offset
        );
        if (nInstances > 0) {
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(
                    4,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset
            );
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(
                    5,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset + 4
            );
            glEnableVertexAttribArray(6);
            glVertexAttribPointer(
                    6,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset + 8
            );
            glEnableVertexAttribArray(7);
            glVertexAttribPointer(
                    7,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    instancesOffset + 12
            );
            glVertexAttribDivisor(4, 1);
            glVertexAttribDivisor(5, 1);
            glVertexAttribDivisor(6, 1);
            glVertexAttribDivisor(7, 1);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        return new QVAO(vao, vbo, ebo);
    }

}
