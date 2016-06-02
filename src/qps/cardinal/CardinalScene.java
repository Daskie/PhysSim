package qps.cardinal;

import qps.*;
import qps.input_listeners.InputAdapter;
import qps.main.MainScene;

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

    private static Mat4 axesMat;
    private static Mat4 axesNormMat;

    private static Mat4 pxMat;
    private static Mat4 nxMat;
    private static Mat4 pyMat;
    private static Mat4 nyMat;
    private static Mat4 pzMat;
    private static Mat4 nzMat;
    private static Mat4 pxNormMat;
    private static Mat4 nxNormMat;
    private static Mat4 pyNormMat;
    private static Mat4 nyNormMat;
    private static Mat4 pzNormMat;
    private static Mat4 nzNormMat;

    private static int pxID;
    private static int nxID;
    private static int pyID;
    private static int nyID;
    private static int pzID;
    private static int nzID;

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

        Mat4 mat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.4f)).mult(new Mat4(Mat3.scale(0.3f))));
        pxMat = new Mat4((new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(mat)));
        nxMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(mat));
        pyMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(mat));
        nyMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(mat));
        pzMat = mat;
        nzMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(mat));
        pxNormMat = new Mat4(pxMat.inv().trans());
        nxNormMat = new Mat4(nxMat.inv().trans());
        pyNormMat = new Mat4(pyMat.inv().trans());
        nyNormMat = new Mat4(nyMat.inv().trans());
        pzNormMat = new Mat4(pzMat.inv().trans());
        nzNormMat = new Mat4(nzMat.inv().trans());

        SlideListener slideIL = new SlideListener();
        pxID = Main.registerIdentity(slideIL, slideIL, null);
        nxID = Main.registerIdentity(slideIL, slideIL, null);
        pyID = Main.registerIdentity(slideIL, slideIL, null);
        nyID = Main.registerIdentity(slideIL, slideIL, null);
        pzID = Main.registerIdentity(slideIL, slideIL, null);
        nzID = Main.registerIdentity(slideIL, slideIL, null);

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

        UniformGlobals.ModelGlobals.setModelMat(pxMat);
        UniformGlobals.ModelGlobals.setNormMat(pxNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pxID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxMat);
        UniformGlobals.ModelGlobals.setNormMat(nxNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nxID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pyMat);
        UniformGlobals.ModelGlobals.setNormMat(pyNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pyID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nyMat);
        UniformGlobals.ModelGlobals.setNormMat(nyNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nyID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzMat);
        UniformGlobals.ModelGlobals.setNormMat(pzNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pzID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzMat);
        UniformGlobals.ModelGlobals.setNormMat(nzNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nzID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void registerListener(int id, CardinalListener listener) {
        cardinalListeners.put(id, listener);
    }

    private static class SlideListener extends InputAdapter implements IdentityListener {

        private int currentID = Main.NO_IDENTITY;

        @Override
        public void gainedHover(int id) {
            currentID = id;
        }

        @Override
        public void lostHover(int id) {
            currentID = Main.NO_IDENTITY;
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

            Vec3 delta = new Vec3();

            if      (currentID == pxID) delta.x = 1.0f;
            else if (currentID == nxID) delta.x = -1.0f;
            else if (currentID == pyID) delta.y = 1.0f;
            else if (currentID == nyID) delta.y = -1.0f;
            else if (currentID == pzID) delta.z = 1.0f;
            else if (currentID == nzID) delta.z = -1.0f;

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
}
