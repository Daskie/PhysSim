package qps;

/**
 * @since 5/17/2016
 */

public class VAO {

    private int size;
    private int coordsOffset;
    private int colorOffset;
    private int uvOffset;
    private int normOffset;
    private int instanceMatsOffset;

    private int vaoID, vboID, eboID;

    public VAO(int vaoID, int vboID, int eboID) {
        this.vaoID = vaoID;
        this.vboID = vboID;
        this.eboID = eboID;
    }

    public int vao() {
        return vaoID;
    }

    public int vbo() {
        return vboID;
    }

    public int ebo() {
        return eboID;
    }

}
