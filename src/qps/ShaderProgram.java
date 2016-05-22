package qps;

/**
 * @since 5/20/2016
 */
public class ShaderProgram {

    protected String vertFilePath, geomFilePath, fragFilePath;
    protected int id;

    public ShaderProgram(String vertFilePath, String geomFilePath, String fragFilePath) {
        this.vertFilePath = vertFilePath;
        this.geomFilePath = geomFilePath;
        this.fragFilePath = fragFilePath;
    }

    public boolean init() {
        id = ShaderLoader.createProgram(vertFilePath, geomFilePath, fragFilePath);

        if (id == 0) {
            System.err.println("Failed to initialize shader program!");
            return false;
        }

        return true;
    }

    public int id() {
        return id;
    }

}
