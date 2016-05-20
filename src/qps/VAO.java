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

    private int vao, vbo, ebo;

    public VAO(int vao, int vbo, int ebo) {
        this.vao = vao;
        this.vbo = vbo;
        this.ebo = ebo;
    }

}
