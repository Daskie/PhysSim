package qps;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import qps.cardinal.CardinalScene;
import qps.charges.ChargedSphere;
import qps.fb.FBScene;
import qps.field.FieldProgram;
import qps.grid.GridScene;
import qps.grid.GridReticleScene;
import qps.hud.HUDScene;
import qps.input_listeners.*;
import qps.main.MainScene;
import qps.map.MapScene;
import qps.sensor.SensorScene;
import qps.window_listeners.WindowCloseListener;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.BufferUtils.createIntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public abstract class Main {

    public static final float UNIT_CHARGE = 1.0e-9f;
    public static final float LINEAR_CHARGE = UNIT_CHARGE / 10.0f;
    public static final float SURFACE_CHARGE = LINEAR_CHARGE / 10.0f;
    public static final float MIN_MAG_E = 0.0f;
    public static final float MAX_MAG_E = 1.0f;
    public static final float MIN_MAG_V = 0.0f;
    public static final float MAX_MAG_V = 1.0f;
    public static final float V_STEP = 1.0f;

    public static final int NO_IDENTITY = -1;
    public static final int NO_POTENTIAL = -2;

    public static final boolean MULTISAMPLED = false; //turned out to be a bitch. what a surprise.
    public static final int SAMPLES = 8;
    public static final Vec4 CLEAR_COLOR = new Vec4(0.9f, 0.9f, 0.9f, 1.0f);
    public static final float CAM_LATERAL_SPEED = 0.00275f;
    public static final float CAM_RADIAL_SPEED = 0.2f;
    public static final float CAM_ANGULAR_SPEED = 0.1f;
    public static final float CAM_RANGE = 50.0f;

    public static final float NEAR_FRUST = 0.1f;
    public static final float FAR_FRUST = 100.0f;
    public static final float FOV = (float)Math.PI / 2.0f;
    public static final Vec3 LIGHT_DIR = new Vec3(-1.0f, -1.0f, -1.0f);
    public static final float LIGHT_STRENGTH = 0.75f;
    public static final Vec3 LIGHT_COLOR = new Vec3(1.0f, 1.0f, 1.0f);
    public static final float LIGHT_AMBIENCE = 0.33f;
    public static final float LIGHT_SPECULAR_INTENSITY = 0.5f;
    public static final float LIGHT_SHININESS = 32.0f;

    private static boolean running;
    private static Window window;
    private static FieldProgram fieldProgram;
    private static Camera camera = new Camera(new Vec3(), 10.0f, CAM_RANGE, CAM_RANGE);
    private static ArrayList<IdentityListener> identityListeners = new ArrayList<IdentityListener>();
    private static ArrayList<InputAdapter> identityHoveredAdapters = new ArrayList<InputAdapter>();
    private static ArrayList<InputAdapter> identitySelectedAdapters = new ArrayList<InputAdapter>();
    private static int hovoredID = NO_IDENTITY;
    private static int selectedID = NO_IDENTITY;
    private static int potentialID = NO_POTENTIAL;

    private static IntBuffer attachmentsBuffer;
    private static IntBuffer identityBuffer;

    private static FrameBuffer fb;

    public static int registerIdentity(IdentityListener identityListener, InputAdapter hoveredAdapter, InputAdapter selectedAdapter) {
        identityListeners.add(identityListener);
        identityHoveredAdapters.add(hoveredAdapter);
        identitySelectedAdapters.add(selectedAdapter);
        return identityListeners.size() - 1;
    }

    public static void unregisterIdentity(int id) {
        identityListeners.set(id, null); // I know, this is awful, but i'm out of time
        identityHoveredAdapters.set(id, null);
        identitySelectedAdapters.set(id, null);
        if (id == hovoredID) hovoredID = NO_IDENTITY;
        if (id == selectedID) selectedID = NO_IDENTITY;
        if (id == potentialID) potentialID = NO_POTENTIAL;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static int getSelected() {
        return selectedID;
    }

    public static boolean select(int id) {
        if (id < 0 || id >= identityListeners.size()) {
            return false;
        }

        if (!deselect()) {
            return false;
        }

        if (identityListeners.get(id) != null) {
            if (identityListeners.get(id).gainedSelect(id)) {
                selectedID = id;
                UniformGlobals.IDGlobals.setSelectedID(selectedID);
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean deselect() {
        if (selectedID != NO_IDENTITY) {
            if (identityListeners.get(selectedID) != null) {
                if (identityListeners.get(selectedID).lostSelect(selectedID)) {
                    selectedID = NO_IDENTITY;
                    UniformGlobals.IDGlobals.setSelectedID(selectedID);
                    return true;
                }
                return false;
            }
            selectedID = NO_IDENTITY;
            UniformGlobals.IDGlobals.setSelectedID(selectedID);
        }
        return true;
    }

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

        attachmentsBuffer = createIntBuffer(2);
        attachmentsBuffer.put(0, GL_COLOR_ATTACHMENT0);
        attachmentsBuffer.put(1, GL_COLOR_ATTACHMENT1);

        identityBuffer = createIntBuffer(1);

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
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glViewport(0, 0, window.width(), window.height());

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (MULTISAMPLED) {
            glEnable(GL_MULTISAMPLE);
        }

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

        UniformGlobals.CameraGlobals.setViewMat(new Mat4());
        UniformGlobals.CameraGlobals.setProjMat(new Mat4());

        UniformGlobals.ModelGlobals.setModelMat(new Mat4());
        UniformGlobals.ModelGlobals.setNormMat(new Mat4());

        UniformGlobals.ViewGlobals.setCamLoc(camera.loc());
        UniformGlobals.ViewGlobals.setCamForward(camera.forward());
        UniformGlobals.ViewGlobals.setCamUp(camera.up());
        UniformGlobals.ViewGlobals.setNearFrust(NEAR_FRUST);
        UniformGlobals.ViewGlobals.setFarFrust(FAR_FRUST);
        UniformGlobals.ViewGlobals.setFov(FOV);

        UniformGlobals.LightGlobals.setDir(LIGHT_DIR);
        UniformGlobals.LightGlobals.setStrength(LIGHT_STRENGTH);
        UniformGlobals.LightGlobals.setColor(LIGHT_COLOR);
        UniformGlobals.LightGlobals.setAmbience(LIGHT_AMBIENCE);
        UniformGlobals.LightGlobals.setSpecularIntensity(LIGHT_SPECULAR_INTENSITY);
        UniformGlobals.LightGlobals.setShininess(LIGHT_SHININESS);

        UniformGlobals.ChargeCountsGlobals.setSphereCount(0);
        UniformGlobals.ChargeCountsGlobals.setPlaneCount(0);

        UniformGlobals.ThresholdGlobals.setMinMagE(MIN_MAG_E);
        UniformGlobals.ThresholdGlobals.setMaxMagE(MAX_MAG_E);
        UniformGlobals.ThresholdGlobals.setMinMagV(MIN_MAG_V);
        UniformGlobals.ThresholdGlobals.setMaxMagV(MAX_MAG_V);
        UniformGlobals.ThresholdGlobals.setVStep(V_STEP);

        UniformGlobals.IDGlobals.setHoveredID(NO_IDENTITY);
        UniformGlobals.IDGlobals.setSelectedID(NO_IDENTITY);

        UniformGlobals.buffer();

        if (!Utils.checkGLErr()) {
            System.err.println("failed to buffer initial uniform globals!");
            return false;
        }

        return true;
    }

    private static boolean initScene() {
        if (!CardinalScene.init()) return false;

        if (!HUDScene.init()) return false;

        if (!MainScene.init()) return false;
        MainScene.addSphere(new ChargedSphere(1.0e-9f, new Vec3(1.0f, 0.0f, 0.0f)));
        MainScene.addSphere(new ChargedSphere(-1.0e-9f, new Vec3(-1.0f, 0.0f, 0.0f)));
        //MainScene.addLine(new ChargedLine(-0.1e-9f, new Vec3()));
        deselect();

        if (!SensorScene.init()) return false;

        //FieldScene.init();

        if (!GridScene.init()) return false;

        if (!GridReticleScene.init()) return false;

        if (!MapScene.init()) return false;

        if (!FBScene.init()) return false;
        fb = FrameBuffer.createMainIdentity(window.width(), window.height(), CLEAR_COLOR, NO_IDENTITY, MULTISAMPLED, SAMPLES);
        FBScene.setTex(fb.colorBuffer(0));

        return true;
    }

    private static boolean initInput() {
        window.windowHandler().addWindowCloseListener(new WindowCloseListener() {
            @Override
            public void wantsToClose(WindowManager handler) {
                running = false;
            }
        });

        window.inputHandler().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(int key, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
                switch (key) {

                }
            }

            @Override
            public void keyReleased(int key, boolean shift, boolean ctrl, boolean alt, InputManager manager) {

            }
        });

        window.inputHandler().addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
                if (repeat) {
                    return;
                }
                potentialID = hovoredID;
                if (hovoredID != NO_IDENTITY && identityHoveredAdapters.get(hovoredID) != null) {
                    identityHoveredAdapters.get(hovoredID).mousePressed(button, shift, ctrl, alt, repeat, manager);
                }
                if (selectedID != NO_IDENTITY && identitySelectedAdapters.get(selectedID) != null) {
                    identitySelectedAdapters.get(selectedID).mousePressed(button, shift, ctrl, alt, repeat, manager);
                }
            }

            @Override
            public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
                if (button == 0) {
                    if (potentialID != NO_POTENTIAL && potentialID != selectedID) {
                        //System.out.println(selectedID);
                        if (potentialID != NO_IDENTITY) {
                            if (identityListeners.get(potentialID) != null) {
                                if (identityListeners.get(potentialID).gainedSelect(potentialID)) {
                                    selectedID = potentialID;
                                }
                            }
                        }
                        else {
                            if (identityListeners.get(selectedID) != null) {
                                if (identityListeners.get(selectedID).lostSelect(selectedID)) {
                                    selectedID = NO_IDENTITY;
                                }
                            }
                        }
                        UniformGlobals.IDGlobals.setSelectedID(selectedID);
                        potentialID = NO_POTENTIAL;
                    }
                }
                if (hovoredID != NO_IDENTITY && identityHoveredAdapters.get(hovoredID) != null) {
                    identityHoveredAdapters.get(hovoredID).mouseReleased(button, shift, ctrl, alt, manager);
                }
                if (selectedID != NO_IDENTITY && identitySelectedAdapters.get(selectedID) != null) {
                    identitySelectedAdapters.get(selectedID).mouseReleased(button, shift, ctrl, alt, manager);
                }
            }
        });

        window.inputHandler().addCursorListener(new CursorListener() {
            private static final float CAMERA_THETA_PER_PIXEL = 0.01f;

            @Override
            public void cursorMoved(double x, double y, double dx, double dy, InputManager manager) {
                potentialID = NO_POTENTIAL;
                if (window.mouseButtonState(0)) {
                    Vec3 vec = (new Vec3((float)dx, (float)-dy, 0.0f)).mult(camera.distance() * CAM_LATERAL_SPEED);
                    Mat3 mapMat = Mat3.mapFrom(camera.right(), camera.up(), camera.forward());
                    camera.translate(mapMat.mult(vec).mult(-1.0f));
                }
                if (window.mouseButtonState(1)) {
                    camera.yaw(CAMERA_THETA_PER_PIXEL * (float) -dx);
                    camera.pitch(CAMERA_THETA_PER_PIXEL * (float) dy);
                }
            }
        });

        window.inputHandler().addScrollListener(new ScrollListener() {
            @Override
            public void scrolled(double xScroll, double yScroll, InputManager manager) {
                camera.move((float)-yScroll * camera.distance() * CAM_RADIAL_SPEED);
            }
        });

        return true;
    }

    private static void update(int t, int  dt) {
        updateCamera(t, dt);

        CardinalScene.update(t, dt);

        HUDScene.update(t, dt);

        MainScene.update(t, dt);

        SensorScene.update(t, dt);

        //FieldScene.update(t, dt);

        GridScene.update(t, dt);

        GridReticleScene.update(t, dt);

        MapScene.update(t, dt);

        FBScene.update(t, dt);

        UniformGlobals.buffer();
    }

    private static void updateCamera(int t, int dt) {
        UniformGlobals.ViewGlobals.setCamLoc(camera.loc());
        UniformGlobals.CameraGlobals.setViewMat(Mat4.view(camera.loc(), camera.forward(), camera.up()));
        UniformGlobals.CameraGlobals.setProjMat(Mat4.perspective(FOV, (float)window.width() / window.height(), NEAR_FRUST, FAR_FRUST));
    }

    private static void draw() {
        glBindFramebuffer(GL_FRAMEBUFFER, fb.id());
        fb.clear();

        glDrawBuffers(attachmentsBuffer);

        MainScene.draw();

        SensorScene.draw();

        glDrawBuffers(GL_COLOR_ATTACHMENT0);

        //FieldScene.draw();

        GridScene.draw();

        GridReticleScene.draw();

        MapScene.draw();

        glClear(GL_DEPTH_BUFFER_BIT);

        glDrawBuffers(attachmentsBuffer);

        CardinalScene.draw();

        HUDScene.draw();

        glDrawBuffers(GL_COLOR_ATTACHMENT0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if (MULTISAMPLED) {
            glBindFramebuffer(GL_READ_BUFFER, fb.id());
            glBindFramebuffer(GL_DRAW_BUFFER, 0);
            glBlitFramebuffer(0, 0, fb.width(), fb.height(), 0, 0, window.width(), window.height(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
            //glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
        else {
            FBScene.draw();
        }

        window.swap();
    }

    private static void identify() {
        glBindFramebuffer(GL_FRAMEBUFFER, fb.id());
        glReadBuffer(GL_COLOR_ATTACHMENT1);

        glReadPixels((int)window.mouseXGL(), (int)window.mouseYGL(), 1, 1, GL_RED_INTEGER, GL_INT, identityBuffer);

        int oldHovoredID = hovoredID;
        hovoredID = identityBuffer.get(0);
        if (hovoredID != oldHovoredID) {
            //System.out.println(hovoredID);
            if (oldHovoredID != NO_IDENTITY && identityListeners.get(oldHovoredID) != null) {
                identityListeners.get(oldHovoredID).lostHover(oldHovoredID);
            }
            if (hovoredID != NO_IDENTITY && identityListeners.get(hovoredID) != null) {
                identityListeners.get(hovoredID).gainedHover(hovoredID);
            }
        }
        UniformGlobals.IDGlobals.setHoveredID(hovoredID);

        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private static boolean cleanup() {
        if (!CardinalScene.cleanup()) return false;
        if (!HUDScene.cleanup()) return false;
        if (!MainScene.cleanup()) return false;
        if (!SensorScene.cleanup()) return false;
        //if (!FieldScene.cleanup()) return false;
        if (!GridScene.cleanup()) return false;
        if (!GridReticleScene.cleanup()) return false;
        if (!MapScene.cleanup()) return false;
        if (!FBScene.cleanup()) return false;

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

            glfwPollEvents();

            now = System.currentTimeMillis();
            update((int)(now - orig), (int)(now - then));
            then = now;

            draw();

            identify();
        }

        if (!cleanup()) {
            System.err.println("Cleanup failed!");
            System.exit(-1);
        }
    }

}