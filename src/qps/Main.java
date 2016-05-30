package qps;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import qps.input_listeners.CursorListener;
import qps.input_listeners.KeyListener;
import qps.window_listeners.WindowCloseListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public abstract class Main {

    private static boolean running;
    private static Window window;
    private static FieldProgram fieldProgram;
    private static Camera camera = new Camera();

    private static float nearFrust = 0.1f;
    private static float farFrust = 100.0f;
    private static float fov = (float)Math.PI / 2.0f;
    private static Vec3 lightDir = new Vec3(-1.0f, -1.0f, -1.0f);
    private static float lightStrength = 1.0f;
    private static Vec3 lightColor = new Vec3(1.0f, 1.0f, 1.0f);
    private static float lightAmbience = 0.1f;

    private static FrameBuffer fb;

    private static boolean init() {
        if (!initLWJGL()) {
            return false;
        }
        if (!initOpenGL()) {
            return false;
        }
        if (!MeshManager.initMeshes()) {
            return false;
        }
        if (!initShaders()) {
            return false;
        }
        if (!initUniformGlobals()) {
            return false;
        }
        if (!initScene()) {
            return false;
        }
        if (!initInput()) {
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

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        if (!Utils.checkGLErr()) {
            System.err.println("OpenGL initialization error!");
            return false;
        }

        return true;
    }

    private static boolean initShaders() {
        fieldProgram = new FieldProgram();
        if (!fieldProgram.init()) {
            System.err.println("Failed to initialize field shader program!");
            return false;
        }

        return true;
    }

    private static boolean initUniformGlobals() {
        if (!UniformGlobals.init()) {
            System.err.println("Failed to initialize uniform globals!");
            return false;
        }

        UniformGlobals.ViewGlobals.setCamLoc(camera.loc());
        UniformGlobals.ViewGlobals.setCamForward(camera.forward());
        UniformGlobals.ViewGlobals.setCamUp(camera.up());
        UniformGlobals.ViewGlobals.setNearFrust(nearFrust);
        UniformGlobals.ViewGlobals.setFarFrust(farFrust);
        UniformGlobals.ViewGlobals.setFov(fov);

        UniformGlobals.TransformGlobals.setModelMat(new Mat4());
        UniformGlobals.TransformGlobals.setNormMat(new Mat4());
        UniformGlobals.TransformGlobals.setViewMat(new Mat4());
        UniformGlobals.TransformGlobals.setProjMat(new Mat4());

        UniformGlobals.LightGlobals.setDir(lightDir);
        UniformGlobals.LightGlobals.setStrength(lightStrength);
        UniformGlobals.LightGlobals.setColor(lightColor);
        UniformGlobals.LightGlobals.setAmbience(lightAmbience);

        UniformGlobals.ChargeCountsGlobals.setSphereCount(0);

        UniformGlobals.buffer();

        if (!Utils.checkGLErr()) {
            System.err.println("failed to buffer initial uniform globals!");
            return false;
        }

        return true;
    }

    private static boolean initScene() {
        MainScene.init();
        MainScene.addSphere(new ChargedSphere(1.0f, new Vec3(1.0f, 0.0f, 0.0f)));
        MainScene.addSphere(new ChargedSphere(-1.0f, new Vec3(-1.0f, 0.0f, 0.0f)));

        FieldScene.init();

        MapScene.init();

        FBScene.init();
        fb = FrameBuffer.createFrameBuffer(window.width(), window.height(), FrameBuffer.Profile.Tne_R);
        FBScene.setTex(fb.colorBuffer(0));

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
            @Override
            public void keyPressed(int key, boolean repeat, boolean shift, boolean ctrl, boolean alt, InputHandler handler) {
                switch (key) {

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
                if (dx <= 10) {
                    camera.yaw(CAMERA_THETA_PER_PIXEL * (float) -dx);
                }
                if (dy <= 10) {
                    camera.pitch(CAMERA_THETA_PER_PIXEL * (float) -dy);
                }
            }
        });

        return true;
    }

    private static void update(int t, int  dt) {
        updateCamera(t, dt);

        MainScene.update(t, dt);

        FieldScene.update(t, dt);

        MapScene.update(t, dt);

        FBScene.update(t, dt);

        UniformGlobals.buffer();
    }

    private static final float CAM_SPEED = 0.1f;
    private static void updateCamera(int t, int dt) {
        if (window.keyState(GLFW_KEY_W)) {
            camera.translate(camera.forward().mult(CAM_SPEED));
        }
        if (window.keyState(GLFW_KEY_A)) {
            camera.translate(camera.right().mult(-CAM_SPEED));
        }
        if (window.keyState(GLFW_KEY_S)) {
            camera.translate(camera.forward().mult(-CAM_SPEED));
        }
        if (window.keyState(GLFW_KEY_D)) {
            camera.translate(camera.right().mult(CAM_SPEED));
        }
        if (window.keyState(GLFW_KEY_SPACE)) {
            camera.translate(Vec3.POSZ.mult(CAM_SPEED));
        }
        if (window.keyState(GLFW_KEY_LEFT_SHIFT)) {
            camera.translate(Vec3.NEGZ.mult(CAM_SPEED));
        }

        UniformGlobals.ViewGlobals.setCamLoc(camera.loc());
        UniformGlobals.TransformGlobals.setViewMat(Mat4.view(camera.loc(), camera.forward(), camera.up()));
        UniformGlobals.TransformGlobals.setProjMat(Mat4.perspective(fov, (float)window.width() / window.height(), nearFrust, farFrust));
    }

    private static void draw() {
        glBindFramebuffer(GL_FRAMEBUFFER, fb.id());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        MainScene.draw();
        Utils.checkGLErr();

        //FieldScene.draw();
        //Utils.checkGLErr();

        MapScene.draw();
        Utils.checkGLErr();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        FBScene.draw();
        Utils.checkGLErr();

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

        running = true;

        long orig = System.currentTimeMillis(), then = orig, now;
        while (running) {// glfwWindowShouldClose(window.id()) == 0 ) {
            now = System.currentTimeMillis();

            update((int)(now - orig), (int)(now - then));

            glfwPollEvents();

            draw();

            Utils.checkGLErr();

            then = now;
        }

        if (!cleanup()) {
            System.err.println("Cleanup failed!");
            System.exit(-1);
        }
    }
}