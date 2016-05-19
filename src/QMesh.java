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

    static final int COORDS_BYTES = 3 * 4;
    static final int COLOR_BYTES = 4 * 4;
    static final int UV_BYTES = 2 * 4;
    static final int NORM_BYTES = 3 * 4;
    static final int VERT_BYTES = COORDS_BYTES + COLOR_BYTES + UV_BYTES + NORM_BYTES;

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
                System.err.println("Invalid qmesh meta!");
                channel.close();
                file.close();
                return null;
            }

            if (meta.nameLength > 0) {
                ByteBuffer nameBuffer = ByteBuffer.allocateDirect(meta.nameLength);
                channel.read(nameBuffer);
                nameBuffer.flip();
                name = new String(nameBuffer.array(), Charset.forName("UTF-8"));
            }

            if (meta.hasCoords != 0) {
                int coordsBytes = meta.nVerts * COORDS_BYTES;
                coordsData = ByteBuffer.allocateDirect(coordsBytes);
                channel.read(coordsData);
                coordsData.flip();

            }

            if (meta.hasColors != 0) {
                int colorsBytes = meta.nVerts * COLOR_BYTES;
                colorsData = ByteBuffer.allocateDirect(colorsBytes);
                channel.read(colorsData);
                colorsData.flip();

            }

            if (meta.hasUVs != 0) {
                int uvsBytes = meta.nVerts * UV_BYTES;
                uvsData = ByteBuffer.allocateDirect(uvsBytes);
                channel.read(uvsData);
                uvsData.flip();

            }

            if (meta.hasNorms != 0) {
                int normsBytes = meta.nVerts * NORM_BYTES;
                normsData = ByteBuffer.allocateDirect(normsBytes);
                channel.read(normsData);
                normsData.flip();

            }

            int indicesBytes = meta.nIndices * 4;
            indicesData = ByteBuffer.allocateDirect(indicesBytes);
            channel.read(indicesData);
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

        FloatBuffer vertBuffer = createFloatBuffer(nVerts * 12 + nInstances * 16);
        for (int i = 0; i < nVerts; ++i) {
            vertBuffer.put(coords[i].x);
            vertBuffer.put(coords[i].y);
            vertBuffer.put(coords[i].z);

            vertBuffer.put(colors[i].r);
            vertBuffer.put(colors[i].g);
            vertBuffer.put(colors[i].b);
            vertBuffer.put(colors[i].a);

            vertBuffer.put(uvs[i].u);
            vertBuffer.put(uvs[i].v);

            vertBuffer.put(norms[i].x);
            vertBuffer.put(norms[i].y);
            vertBuffer.put(norms[i].z);
        }
        for (int i = 0; i < nInstances; ++i) {
            for (int ci = 0; ci < 4; ++ci) {
                for (int ri = 0; ri < 4; ++ri) {
                    vertBuffer.put(instanceMats[i].get(ci, ri));
                }
            }
        }
        vertBuffer.flip();

        IntBuffer indexBuffer = createIntBuffer(nIndices);
        indexBuffer.put(indices);
        indexBuffer.flip();

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int offset = 0;
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(
                0,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       				// array buffer offset
        );
        offset += nVerts * COORDS_BYTES;
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(
                1,										// attribute 0. Match the layout in the shader.
                4,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                                  // array buffer offset
        );
        offset += nVerts * COLOR_BYTES;
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(
                2,										// attribute 0. Match the layout in the shader.
                2,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       				// array buffer offset
        );
        offset += nVerts * UV_BYTES;
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(
                3,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       			// array buffer offset
        );
        offset += nVerts * NORM_BYTES;
        if (nInstances > 0) {
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(
                    4,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(
                    5,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(6);
            glVertexAttribPointer(
                    6,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(7);
            glVertexAttribPointer(
                    7,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
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
