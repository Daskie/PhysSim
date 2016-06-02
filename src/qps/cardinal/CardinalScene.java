package qps.cardinal;

import qps.*;
import qps.input_listeners.InputAdapter;

import java.util.Arrays;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class CardinalScene {

    private static final float SLIDE_SPEED = 1.0f;
    private static final float STEP_SIZE = 0.1f;
    private static final float JUMP_SIZE = 1.0f;

    private static CardinalProgram program;
    private static VAO axesVAO;
    private static VAO coneVAO;
    private static VAO sphereVao;

    private static Mat4 axesMat;
    private static Mat4 axesNormMat;

    private static Mat4[] moveMats;     //px, nx, py, ny, pz, nz
    private static Mat4[] moveNormMats;
    private static int[] moveIDs;
    private static Vec3[] moveDirs;

    private static Mat4 roundMat;
    private static Mat4 roundNormMat;
    private static int roundID;

    private static Vec3 slideVelocity;

    private static HashMap<Integer, CardinalListener> cardinalListeners;

    public static boolean init() {
        program = new CardinalProgram();
        program.init();

        axesVAO = new VAO(MeshManager.axesMesh, 0, null, null, null, GL_STATIC_DRAW);
        coneVAO = new VAO(MeshManager.coneMesh, 0, null, null, null, GL_STATIC_DRAW);

        program.setCamLoc(new Vec3(0.0f, 0.0f, 4.0f));
        program.setLightDir(new Vec3(1.0f, -1.0f, -1.0f));
        program.setScreenPos(new Vec2(0.75f, -0.75f));

        axesMat = new Mat4();
        axesNormMat = new Mat4();

        Mat4 mat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.5f)).mult(new Mat4(Mat3.scale(0.33f))));
        moveMats = new Mat4[]{
            new Mat4((new Mat4(Mat3.rotate((float) Math.PI / 2.0f, Vec3.POSY)).mult(mat))),
            new Mat4(new Mat4(Mat3.rotate((float) Math.PI / 2.0f, Vec3.NEGY)).mult(mat)),
            new Mat4(new Mat4(Mat3.rotate((float) Math.PI / 2.0f, Vec3.NEGX)).mult(mat)),
            new Mat4(new Mat4(Mat3.rotate((float) Math.PI / 2.0f, Vec3.POSX)).mult(mat)),
            mat,
            new Mat4(new Mat4(Mat3.rotate((float) Math.PI, Vec3.POSX)).mult(mat))
        };
        moveNormMats = new Mat4[6];
        for (int i = 0; i < 6; ++i) {
            moveNormMats[i] = new Mat4(moveMats[i].inv().trans());
        }

        MoveListener slideListener = new MoveListener();
        moveIDs = new int[6];
        for (int i = 0; i < 6; ++i) {
            moveIDs[i] = Main.registerIdentity(slideListener, slideListener, null);
        }

        moveDirs = new Vec3[]{
            Vec3.POSX,
            Vec3.NEGX,
            Vec3.POSY,
            Vec3.NEGY,
            Vec3.POSZ,
            Vec3.NEGZ
        };

        roundMat = new Mat4();
        roundNormMat = new Mat4(roundMat.inv().trans());
        RoundListener roundListener = new RoundListener();
        roundID = Main.registerIdentity(null, roundListener, null);

        cardinalListeners = new HashMap<Integer, CardinalListener>();

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize cardinal scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {
        if (slideVelocity != null && cardinalListeners.containsKey(Main.getSelected())) {
            cardinalListeners.get(Main.getSelected()).move(Main.getSelected(), slideVelocity.mult(dt / 1000.0f));
        }
    }

    public static void draw() {
        glUseProgram(program.id());

        glBindVertexArray(axesVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(axesMat);
        UniformGlobals.ModelGlobals.setNormMat(axesNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(Main.NO_IDENTITY);
        glDrawElements(GL_TRIANGLES, MeshManager.axesMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(coneVAO.vao());

        for (int i = 0; i < 6; ++i) {
            UniformGlobals.ModelGlobals.setModelMat(moveMats[i]);
            UniformGlobals.ModelGlobals.setNormMat(moveNormMats[i]);
            UniformGlobals.ModelGlobals.buffer();
            program.setID(moveIDs[i]);
            glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void registerListener(int id, CardinalListener listener) {
        cardinalListeners.put(id, listener);
    }

    private static class MoveListener extends InputAdapter implements IdentityListener {

        private int currentI = -1;

        @Override
        public void gainedHover(int id) {
            currentI = Arrays.binarySearch(moveIDs, id);
        }

        @Override
        public void lostHover(int id) {
            currentI = -1;
            slideVelocity = null;
        }

        @Override
        public boolean gainedSelect(int id) {
            return false;
        }

        @Override
        public boolean lostSelect(int id) {
            return true;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
            if (!cardinalListeners.containsKey(Main.getSelected())) {
                return;
            }

            Vec3 delta = moveDirs[currentI];

            if (ctrl) {
                cardinalListeners.get(Main.getSelected()).move(Main.getSelected(), delta.mult(STEP_SIZE));
            }
            else if (shift) {
                cardinalListeners.get(Main.getSelected()).move(Main.getSelected(), delta.mult(JUMP_SIZE));
            }
            else {
                slideVelocity = delta.mult(SLIDE_SPEED);
            }
        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            slideVelocity = null;
        }
    }

    private static class RoundListener extends InputAdapter {

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {
            if (!cardinalListeners.containsKey(Main.getSelected())) {
                return;
            }

            /*Vec3 loc = Main.getSelected()

            if (ctrl) {
                cardinalListeners.get(Main.getSelected()).move(Main.getSelected(), delta.mult(STEP_SIZE));
            }
            else if (shift) {
                cardinalListeners.get(Main.getSelected()).move(Main.getSelected(), delta.mult(JUMP_SIZE));
            }
            else {
                slideVelocity = delta.mult(SLIDE_SPEED);
            }*/
        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            slideVelocity = null;
        }
    }
}
