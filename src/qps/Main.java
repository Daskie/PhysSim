package qps;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static Window window;
    private static InputHandler inputHandler;
    private static ShaderProgram shaderProgram;
    private static Mesh myMesh;
    private static VAO myVAO;

    private static boolean init() {
        if (!initLWJGL()) {
            return false;
        }
        if (!initOpenGL()) {
            return false;
        }
        if (!initShaders()) {
            return false;
        }
        if (!MeshManager.initMeshes()) {
            return false;
        }
        if (!initScene()) {
            return false;
        }

        if (!checkGLErr()) {
            System.err.println("Initialization OpenGL error!");
            return false;
        }

        return true;
    }

    private static boolean initLWJGL() {
        System.out.println("LWJGL version: " + Version.getVersion());

        glfwSetErrorCallback(glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err)));

        if ( glfwInit() != GLFW_TRUE ) {
           System.err.println("Unable to initialize GLFW!");
            return false;
        }

        window = new Window(1280, 720);
        window.setCurrent();

        inputHandler = new InputHandler(window);

        GL.createCapabilities();

        return true;
    }

    private static boolean initOpenGL() {
        int majVer = glGetInteger(GL_MAJOR_VERSION);
        int minVer = glGetInteger(GL_MINOR_VERSION);
        System.out.println("OpenGL Version: " + majVer + "." + minVer);

        glClearColor(0.25f, 0.0f, 0.25f, 1.0f);
        glViewport(0, 0, window.width(), window.height());

        //glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);

        if (!checkGLErr()) {
            System.err.println("OpenGL initialization error!");
            return false;
        }

        return true;
    }

    private static boolean initShaders() {
        shaderProgram = new ShaderProgram("shaders/a.vert", null, "shaders/a.frag");

        if (shaderProgram.getID() == 0) {
            System.err.println("Failed to initialize shaders!");
            return false;
        }

        return true;
    }

    private static boolean initScene() {
        myMesh = MeshManager.cubeMesh;
        myVAO = myMesh.buffer();

        return true;
    }

    private static void tempLoop() {
        int glErr;

        while ( glfwWindowShouldClose(window.getID()) == 0 ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer
            checkGLErr();

            glfwPollEvents();

            draw();

            checkGLErr();
        }
    }

    private static void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        checkGLErr();

        glUseProgram(shaderProgram.getID());
        checkGLErr();

        glBindVertexArray(myVAO.vao());
        checkGLErr();
        glDrawElements(GL_TRIANGLES, myMesh.nIndices(), GL_UNSIGNED_INT, 0);
        checkGLErr();

        window.swap();
    }

    private static boolean cleanup() {
        inputHandler.cleanup();
        window.cleanup();

        glfwTerminate();

        return true;
    }

    private static boolean checkGLErr() {
        int glErr = glGetError();

        if (glErr != GL_NO_ERROR) {
            System.err.println("OpenGL error: " + glErr);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        if (!init()) {
            System.err.println("Initialization failed!");
            System.exit(-1);
        }

        tempLoop();

        if (!cleanup()) {
            System.err.println("Cleanup failed!");
            System.exit(-1);
        }
    }
}