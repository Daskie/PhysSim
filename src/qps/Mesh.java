package qps;

import java.nio.ByteBuffer;

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

    public boolean hasCoords() {
        return coordsData != null;
    }

    public boolean hasColors() {
        return colorsData != null;
    }

    public boolean hasUVs() {
        return uvsData != null;
    }

    public boolean hasNorms() {
        return normsData != null;
    }
    int nVerts() {
        return nVerts;
    }

    public int nIndices() {
        return nIndices;
    }

    public ByteBuffer coordsData() {
        return coordsData;
    }

    public ByteBuffer colorsData() {
        return colorsData;
    }

    public ByteBuffer uvsData() {
        return uvsData;
    }

    public ByteBuffer normsData() {
        return normsData;
    }

    public ByteBuffer indicesData() {
        return indicesData;
    }

}
