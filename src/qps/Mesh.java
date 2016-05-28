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
    private ByteBuffer coordsData;
    private ByteBuffer colorsData;
    private ByteBuffer uvsData;
    private ByteBuffer normsData;
    private int nIndices;
    private ByteBuffer indicesData;

    public Mesh(String name, int nVerts, ByteBuffer coordsData, ByteBuffer colorsData, ByteBuffer uvsData, ByteBuffer normsData, int nIndices, ByteBuffer indicesData) {
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
    int nVerts() {
        return nVerts;
    }

    int nIndices() {
        return nIndices;
    }

    ByteBuffer coordsData() {
        return coordsData;
    }

    ByteBuffer colorsData() {
        return colorsData;
    }

    ByteBuffer uvsData() {
        return uvsData;
    }

    ByteBuffer normsData() {
        return normsData;
    }

    ByteBuffer indicesData() {
        return indicesData;
    }

}
