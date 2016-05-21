package qps;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

/**
 * @since 5/20/2016
 */
public abstract class ShaderLoader {

    public static String loadShader(String filePath) {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            byte[] buffer = new byte[(int)file.length()];
            file.read(buffer);
            return new String(buffer, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read shader file: " + filePath);
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean compileShader(int shaderID, String shaderStr) {
        glShaderSource(shaderID, shaderStr);
        glCompileShader(shaderID);

        int compiled = glGetShaderi(shaderID, GL_COMPILE_STATUS);
        if (compiled != GL_TRUE) {
            System.err.println("Failed to compile shader: " + shaderID);
            System.err.println(glGetShaderInfoLog(shaderID));
            return false;
        }

        return true;
    }

    public static boolean linkShaders(int programID, int vertShaderID, int fragShaderID, int geomShaderID) {
        if (vertShaderID != 0) {
            glAttachShader(programID, vertShaderID);
        }
        if (geomShaderID != 0) {
            glAttachShader(programID, geomShaderID);
        }
        if (fragShaderID != 0) {
            glAttachShader(programID, fragShaderID);
        }
        glLinkProgram(programID);

        int linked = glGetProgrami(programID, GL_LINK_STATUS);
        if (linked != GL_TRUE) {
            System.err.println("Failed to link shaders: " + vertShaderID + " " + fragShaderID + " " + geomShaderID);
            System.err.println(glGetProgramInfoLog(programID));
            return false;
        }

        if (vertShaderID != 0) {
            glDetachShader(programID, vertShaderID);
        }
        if (geomShaderID != 0) {
            glDetachShader(programID, geomShaderID);
        }
        if (fragShaderID != 0) {
            glDetachShader(programID, fragShaderID);
        }

        return true;
    }

    public static int createProgram(String vertPath, String geomPath, String fragPath) {
        String vertShaderStr = null;
        String geomShaderStr = null;
        String fragShaderStr = null;

        if (vertPath != null) {
            vertShaderStr = loadShader(vertPath);
            if (vertShaderStr == null) {
                System.err.println("Failed to load vertex shader: " + vertPath);
                return 0;
            }
        }
        if (geomPath != null) {
            geomShaderStr = loadShader(geomPath);
            if (geomShaderStr == null) {
                System.err.println("Failed to load geometry shader: " + geomPath);
                return 0;
            }
        }
        if (fragPath != null) {
            fragShaderStr = loadShader(fragPath);
            if (fragShaderStr == null) {
                System.err.println("Failed to load fragment shader: " + fragPath);
                return 0;
            }
        }

        int vertShaderID = 0;
        int geomShaderID = 0;
        int fragShaderID = 0;

        if (vertPath != null) {
            vertShaderID = glCreateShader(GL_VERTEX_SHADER);
            if (!compileShader(vertShaderID, vertShaderStr)) {
                System.err.println("Failed to compile vertex shader: " + vertPath);
                return 0;
            }
        }
        if (geomPath != null) {
            geomShaderID = glCreateShader(GL_GEOMETRY_SHADER);
            if (!compileShader(geomShaderID, geomShaderStr)) {
                System.err.println("Failed to compile geometry shader: " + geomPath);
                return 0;
            }
        }
        if (fragPath != null) {
            fragShaderID = glCreateShader(GL_FRAGMENT_SHADER);
            if (!compileShader(fragShaderID, fragShaderStr)) {
                System.err.println("Failed to compile fragment shader: " + fragPath);
                return 0;
            }
        }

        int programID = glCreateProgram();

        if (!linkShaders(programID, vertShaderID, geomShaderID, fragShaderID)) {
            System.err.println("Failed to link shaders: " + vertPath + ", " + fragPath + ", and " + geomPath);
            glDeleteProgram(programID);
            glDeleteShader(vertShaderID);
            glDeleteShader(geomShaderID);
            glDeleteShader(fragShaderID);
            return 0;
        }

        glDeleteShader(vertShaderID);
        glDeleteShader(geomShaderID);
        glDeleteShader(fragShaderID);

        return programID;
    }
}
