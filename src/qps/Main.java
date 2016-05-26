package qps;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import qps.input_listeners.CursorListener;
import qps.input_listeners.KeyListener;
import qps.window_listeners.WindowCloseListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class Main {

    private static boolean running;
    private static Window window;
    private static MainProgram mainProgram;
    private static Scene mainScene;

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
        if (!initInput()) {
            return false;
        }

        if (!Utils.checkGLErr()) {
            System.err.println("Initialization OpenGL error!");
            return false;
        }

        return true;
    }

    private static boolean initLWJGL() {
        System.out.println("LWJGL version: " + Version.getVersion());

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        if ( glfwInit() != GLFW_TRUE ) {
           System.err.println("Unable to initialize GLFW!");
            return false;
        }

        window = new Window(1280, 720);
        window.setCurrent();

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

        if (!Utils.checkGLErr()) {
            System.err.println("OpenGL initialization error!");
            return false;
        }

        return true;
    }

    private static boolean initShaders() {
        mainProgram = new MainProgram();
        if (!mainProgram.init()) {
            System.err.println("Failed to initialize main shader program!");
            return false;
        }

        return true;
    }

    private static boolean initScene() {
        mainScene = new Scene();
        Mesh cubeMesh = null;
        Entity cube = new Entity(MeshManager.cubeMesh, MeshManager.cubeMesh.buffer());
        mainScene.addEntity(cube);

        return true;
    }

    private static boolean initInput() {
        window.windowHandler().addWindowCloseListener(new WindowCloseListener() {
            @Override
            public void wantsToClose(WindowHandler handler) {
                running = false;
            }
        });

        window.inputHandler().addKeyListener(new KeyListener() {
            static final float CAM_SPEED = 0.1f;

            @Override
            public void keyPressed(int key, boolean repeat, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
                switch (key) {
                    case GLFW_KEY_W: {
                        mainScene.camera().translate(mainScene.camera().forward().mult(CAM_SPEED));
                        break;
                    }
                    case GLFW_KEY_A: {
                        mainScene.camera().translate(mainScene.camera().right().mult(-CAM_SPEED));
                        break;
                    }
                    case GLFW_KEY_S: {
                        mainScene.camera().translate(mainScene.camera().forward().mult(-CAM_SPEED));
                        break;
                    }
                    case GLFW_KEY_D: {
                        mainScene.camera().translate(mainScene.camera().right().mult(CAM_SPEED));
                        break;
                    }
                    case GLFW_KEY_SPACE: {
                        mainScene.camera().translate(Vec3.POSZ.mult(CAM_SPEED));
                        break;
                    }
                    case GLFW_KEY_LEFT_SHIFT: {
                        mainScene.camera().translate(Vec3.NEGZ.mult(CAM_SPEED));
                        break;
                    }
                }
            }

            @Override
            public void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {

            }
        });

        window.inputHandler().addCursorListener(new CursorListener() {
            private static final float CAMERA_THETA_PER_PIXEL = 0.01f;

            @Override
            public void cursorMoved(double x, double y, double dx, double dy, InputHandler handler) {
                mainScene.camera().yaw(CAMERA_THETA_PER_PIXEL * (float)-dx);
                mainScene.camera().pitch(CAMERA_THETA_PER_PIXEL * (float)-dy);
            }
        });

        return true;
    }

    private static void tempLoop() {
        running = true;

        while (running) {// glfwWindowShouldClose(window.id()) == 0 ) {
            update();

            glfwPollEvents();

            draw();

            Utils.checkGLErr();
        }
    }

    private static void update() {
        updateCamera();
        updateUniforms();
    }

    private static void updateCamera() {
    }

    private static void updateUniforms() {
        Camera camera = mainScene.camera();
        mainProgram.setViewMat(Mat4.view(camera.loc(), camera.forward(), camera.up()));
        mainProgram.setProjMat(Mat4.perspective((float)Math.toRadians(90), (float)window.width() / window.height(), 0.1f, 100.0f));
    }

    private static void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        Utils.checkGLErr();

        glUseProgram(mainProgram.id());
        Utils.checkGLErr();

        mainScene.draw();

        window.swap();
    }

    private static boolean cleanup() {
        window.cleanup();

        glfwTerminate();

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