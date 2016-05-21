package qps;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.BufferUtils.createIntBuffer;

/**
 * @since 5/20/2016
 */
public abstract class MeshLoader {

    private static class Meta {
        static final int META_BYTES = 20;

        int nVerts;
        int nIndices;
        byte hasCoords, hasColors, hasUVs, hasNorms;
        byte hasTransparency, hasTranslucency, filler0, filler1;
        int nameLength;
    }

    public static Mesh simpleSquare() {
        int nVerts = 4;
        FloatBuffer coords = createFloatBuffer(nVerts * 3);
        ByteBuffer colors = createByteBuffer(nVerts * 4);
        FloatBuffer uvs = createFloatBuffer(nVerts * 2);
        FloatBuffer norms = createFloatBuffer(nVerts * 3);

        coords.put(0); coords.put(0); coords.put(0);
        coords.put(1); coords.put(0); coords.put(0);
        coords.put(1); coords.put(1); coords.put(0);
        coords.put(0); coords.put(1); coords.put(0);
        coords.flip();

        byte w = (byte)255;
        byte b = (byte)0;
        colors.put(w); colors.put(b); colors.put(b); colors.put(b);
        colors.put(b); colors.put(w); colors.put(b); colors.put(b);
        colors.put(b); colors.put(b); colors.put(w); colors.put(b);
        colors.put(w); colors.put(w); colors.put(w); colors.put(b);
        colors.flip();

        uvs.put(0.0f); uvs.put(0.0f);
        uvs.put(1.0f); uvs.put(0.0f);
        uvs.put(1.0f); uvs.put(1.0f);
        uvs.put(0.0f); uvs.put(1.0f);
        uvs.flip();

        norms.put(0.0f); norms.put(0.0f); norms.put(1.0f);
        norms.put(0.0f); norms.put(0.0f); norms.put(1.0f);
        norms.put(0.0f); norms.put(0.0f); norms.put(1.0f);
        norms.put(0.0f); norms.put(0.0f); norms.put(1.0f);
        norms.flip();

        int nIndices = 6;
        IntBuffer indices = createIntBuffer(nIndices);

        indices.put(0); indices.put(1); indices.put(2);
        indices.put(2); indices.put(3); indices.put(0);
        indices.flip();

        return new Mesh("SimpleSquare", nVerts, coords, colors, uvs, norms, nIndices, indices);
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
                int coordsBytes = meta.nVerts * Mesh.COORDS_BYTES;
                ByteBuffer tempCoordsBuff = createByteBuffer(coordsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempCoordsBuff) != coordsBytes) {
                    throw new IOException("Unexpected number of coords bytes read from qmesh!");
                }
                tempCoordsBuff.flip();
                coordsData = tempCoordsBuff.asFloatBuffer();
            }

            if (meta.hasColors != 0) {
                int colorsBytes = meta.nVerts * Mesh.COLOR_BYTES;
                ByteBuffer tempColordsBuff = createByteBuffer(colorsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempColordsBuff) != colorsBytes) {
                    throw new IOException("Unexpected number of colors bytes read from qmesh!");
                }
                tempColordsBuff.flip();
                colorsData = tempColordsBuff;
            }

            if (meta.hasUVs != 0) {
                int uvsBytes = meta.nVerts * Mesh.UV_BYTES;
                ByteBuffer tempUVsBuffer = createByteBuffer(uvsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempUVsBuffer) != uvsBytes) {
                    throw new IOException("Unexpected number of uvs bytes read from qmesh!");
                }
                tempUVsBuffer.flip();
                uvsData = tempUVsBuffer.asFloatBuffer();
            }

            if (meta.hasNorms != 0) {
                int normsBytes = meta.nVerts * Mesh.NORM_BYTES;
                ByteBuffer tempNormsBuff = createByteBuffer(normsBytes).order(ByteOrder.LITTLE_ENDIAN);
                if (channel.read(tempNormsBuff) != normsBytes) {
                    throw new IOException("Unexpected number of norms bytes read from qmesh!");
                }
                tempNormsBuff.flip();
                normsData = tempNormsBuff.asFloatBuffer();
            }

            int indicesBytes = meta.nIndices * 4;
            ByteBuffer tempIndicesBuff = createByteBuffer(indicesBytes).order(ByteOrder.LITTLE_ENDIAN);
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
}
