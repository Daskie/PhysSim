package qps;

/**
 * @since 5/20/2016
 */
public class ShaderProgram {

    private int programID;

    public ShaderProgram(int programID) {
        this.programID = programID;
    }

    public ShaderProgram(String vertFilePath, String geomFilePath, String fragFilePath) {
        programID = ShaderLoader.createProgram(vertFilePath, geomFilePath, fragFilePath);
    }

    public int getID() {
        return programID;
    }

}
