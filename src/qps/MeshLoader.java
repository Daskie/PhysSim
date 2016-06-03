package qps;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.BufferUtils.createByteBuffer;

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

    public static Mesh square() {
        int nVerts = 4;
        ByteBuffer coords = createByteBuffer(nVerts * Mesh.COORDS_BYTES);
        ByteBuffer colors = createByteBuffer(nVerts * Mesh.COLOR_BYTES);
        ByteBuffer uvs = createByteBuffer(nVerts * Mesh.UV_BYTES);
        ByteBuffer norms = createByteBuffer(nVerts * Mesh.NORM_BYTES);

        coords.putFloat(-1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);
        coords.putFloat(-1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);
        coords.flip();

        byte w = (byte)255;
        byte b = (byte)0;
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.flip();

        uvs.putFloat(0.0f); uvs.putFloat(0.0f);
        uvs.putFloat(1.0f); uvs.putFloat(0.0f);
        uvs.putFloat(1.0f); uvs.putFloat(1.0f);
        uvs.putFloat(0.0f); uvs.putFloat(1.0f);
        uvs.flip();

        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.flip();

        int nIndices = 6;
        ByteBuffer indices = createByteBuffer(nIndices * 4);

        indices.putInt(0); indices.putInt(1); indices.putInt(2);
        indices.putInt(2); indices.putInt(3); indices.putInt(0);
        indices.flip();

        return new Mesh("SimpleSquare", nVerts, coords, colors, uvs, norms, nIndices, indices);
    }

    public static Mesh doubleSquare() {
        int nVerts = 8;
        ByteBuffer coords = createByteBuffer(nVerts * Mesh.COORDS_BYTES);
        ByteBuffer colors = createByteBuffer(nVerts * Mesh.COLOR_BYTES);
        ByteBuffer uvs = createByteBuffer(nVerts * Mesh.UV_BYTES);
        ByteBuffer norms = createByteBuffer(nVerts * Mesh.NORM_BYTES);

        coords.putFloat(-1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);
        coords.putFloat(-1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);

        coords.putFloat(-1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(1.0f); coords.putFloat(0.0f);
        coords.putFloat(1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);
        coords.putFloat(-1.0f); coords.putFloat(-1.0f); coords.putFloat(0.0f);

        coords.flip();

        byte w = (byte)255;
        byte b = (byte)0;
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);

        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);
        colors.put(w); colors.put(w); colors.put(w); colors.put(w);

        colors.flip();

        uvs.putFloat(0.0f); uvs.putFloat(0.0f);
        uvs.putFloat(1.0f); uvs.putFloat(0.0f);
        uvs.putFloat(1.0f); uvs.putFloat(1.0f);
        uvs.putFloat(0.0f); uvs.putFloat(1.0f);

        uvs.putFloat(0.0f); uvs.putFloat(1.0f);
        uvs.putFloat(1.0f); uvs.putFloat(1.0f);
        uvs.putFloat(1.0f); uvs.putFloat(0.0f);
        uvs.putFloat(0.0f); uvs.putFloat(0.0f);

        uvs.flip();

        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(1.0f);

        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(-1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(-1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(-1.0f);
        norms.putFloat(0.0f); norms.putFloat(0.0f); norms.putFloat(-1.0f);

        norms.flip();

        int nIndices = 12;
        ByteBuffer indices = createByteBuffer(nIndices * 4);

        indices.putInt(0); indices.putInt(1); indices.putInt(2);
        indices.putInt(2); indices.putInt(3); indices.putInt(0);

        indices.putInt(4); indices.putInt(5); indices.putInt(6);
        indices.putInt(6); indices.putInt(7); indices.putInt(4);

        indices.flip();

        return new Mesh("DoubleSquare", nVerts, coords, colors, uvs, norms, nIndices, indices);
    }

    public static Mesh fromFile(String filePath) throws IOException {

        Meta meta = new Meta();
        String name = filePath;
        ByteBuffer coordsData = null;
        ByteBuffer colorsData = null;
        ByteBuffer uvsData = null;
        ByteBuffer normsData = null;
        ByteBuffer indicesData = null;

        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        FileChannel channel = file.getChannel();

        ByteBuffer metaByteBuffer = createByteBuffer(Meta.META_BYTES).order(ByteOrder.LITTLE_ENDIAN);
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
            ByteBuffer nameBuffer = createByteBuffer(meta.nameLength).order(ByteOrder.LITTLE_ENDIAN);
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
            coordsData = createByteBuffer(coordsBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(coordsData) != coordsBytes) {
                throw new IOException("Unexpected number of coords bytes read from qmesh!");
            }
            coordsData.flip();
        }

        if (meta.hasColors != 0) {
            int colorsBytes = meta.nVerts * Mesh.COLOR_BYTES;
            colorsData = createByteBuffer(colorsBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(colorsData) != colorsBytes) {
                throw new IOException("Unexpected number of colors bytes read from qmesh!");
            }
            colorsData.flip();
        }

        if (meta.hasUVs != 0) {
            int uvsBytes = meta.nVerts * Mesh.UV_BYTES;
            uvsData = createByteBuffer(uvsBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(uvsData) != uvsBytes) {
                throw new IOException("Unexpected number of uvs bytes read from qmesh!");
            }
            uvsData.flip();
        }

        if (meta.hasNorms != 0) {
            int normsBytes = meta.nVerts * Mesh.NORM_BYTES;
            normsData = createByteBuffer(normsBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (channel.read(normsData) != normsBytes) {
                throw new IOException("Unexpected number of norms bytes read from qmesh!");
            }
            normsData.flip();
        }

        int indicesBytes = meta.nIndices * 4;
        indicesData = createByteBuffer(indicesBytes).order(ByteOrder.LITTLE_ENDIAN);
        if (channel.read(indicesData) != indicesBytes) {
            throw new IOException("Unexpected number of indices bytes read from qmesh!");
        }
        indicesData.flip();

        channel.close();
        file.close();

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
