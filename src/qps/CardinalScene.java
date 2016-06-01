package qps;

import qps.input_listeners.InputAdapter;

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
    private static final float STEP_SPEED = 1.0f;

    private static CardinalProgram program;
    private static VAO axesVAO;
    private static VAO coneVAO;

    private static Mat4 axesMat;
    private static Mat4 axesNormMat;

    private static Mat4 pxStepMat;
    private static Mat4 nxStepMat;
    private static Mat4 pyStepMat;
    private static Mat4 nyStepMat;
    private static Mat4 pzStepMat;
    private static Mat4 nzStepMat;
    private static Mat4 pxStepNormMat;
    private static Mat4 nxStepNormMat;
    private static Mat4 pyStepNormMat;
    private static Mat4 nyStepNormMat;
    private static Mat4 pzStepNormMat;
    private static Mat4 nzStepNormMat;

    private static Mat4 pxSlideMat;
    private static Mat4 nxSlideMat;
    private static Mat4 pySlideMat;
    private static Mat4 nySlideMat;
    private static Mat4 pzSlideMat;
    private static Mat4 nzSlideMat;
    private static Mat4 pxSlideNormMat;
    private static Mat4 nxSlideNormMat;
    private static Mat4 pySlideNormMat;
    private static Mat4 nySlideNormMat;
    private static Mat4 pzSlideNormMat;
    private static Mat4 nzSlideNormMat;

    private static int pxSlideID;
    private static int nxSlideID;
    private static int pySlideID;
    private static int nySlideID;
    private static int pzSlideID;
    private static int nzSlideID;
    private static int pxStepID;
    private static int nxStepID;
    private static int pyStepID;
    private static int nyStepID;
    private static int pzStepID;
    private static int nzStepID;

    private static Vec3 slideVelocity;

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

        Mat4 stepMat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.4f)).mult(new Mat4(Mat3.scale(0.3f))));
        pxStepMat = new Mat4((new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(stepMat)));
        nxStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(stepMat));
        pyStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(stepMat));
        nyStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(stepMat));
        pzStepMat = stepMat;
        nzStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(stepMat));
        pxStepNormMat = new Mat4(pxStepMat.inv().trans());
        nxStepNormMat = new Mat4(nxStepMat.inv().trans());
        pyStepNormMat = new Mat4(pyStepMat.inv().trans());
        nyStepNormMat = new Mat4(nyStepMat.inv().trans());
        pzStepNormMat = new Mat4(pzStepMat.inv().trans());
        nzStepNormMat = new Mat4(nzStepMat.inv().trans());

        Mat4 slideMat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.65f)).mult(new Mat4(Mat3.scale(0.2f))));
        pxSlideMat = new Mat4((new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(slideMat)));
        nxSlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(slideMat));
        pySlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(slideMat));
        nySlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(slideMat));
        pzSlideMat = slideMat;
        nzSlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(slideMat));
        pxSlideNormMat = new Mat4(pxSlideMat.inv().trans());
        nxSlideNormMat = new Mat4(nxSlideMat.inv().trans());
        pySlideNormMat = new Mat4(pySlideMat.inv().trans());
        nySlideNormMat = new Mat4(nySlideMat.inv().trans());
        pzSlideNormMat = new Mat4(pzSlideMat.inv().trans());
        nzSlideNormMat = new Mat4(nzSlideMat.inv().trans());

        SlideListener slideIL = new SlideListener();
        pxSlideID = Main.registerIdentity(slideIL, slideIL, null);
        nxSlideID = Main.registerIdentity(slideIL, slideIL, null);
        pySlideID = Main.registerIdentity(slideIL, slideIL, null);
        nySlideID = Main.registerIdentity(slideIL, slideIL, null);
        pzSlideID = Main.registerIdentity(slideIL, slideIL, null);
        nzSlideID = Main.registerIdentity(slideIL, slideIL, null);
        StepListener stepIL = new StepListener();
        pxStepID = Main.registerIdentity(stepIL, stepIL, null);
        nxStepID = Main.registerIdentity(stepIL, stepIL, null);
        pyStepID = Main.registerIdentity(stepIL, stepIL, null);
        nyStepID = Main.registerIdentity(stepIL, stepIL, null);
        pzStepID = Main.registerIdentity(stepIL, stepIL, null);
        nzStepID = Main.registerIdentity(stepIL, stepIL, null);

        return true;
    }

    public static void update(int t, int dt) {
        if (slideVelocity != null) {
            MainScene.moveSphere(slideVelocity.mult(dt / 1000.0f));
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

        UniformGlobals.ModelGlobals.setModelMat(pxSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pxSlideNormMat);
        program.setID(pxSlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nxSlideNormMat);
        program.setID(nxSlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pySlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pySlideNormMat);
        program.setID(pySlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nySlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nySlideNormMat);
        program.setID(nySlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pzSlideNormMat);
        program.setID(pzSlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nzSlideNormMat);
        program.setID(nzSlideID);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(coneVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(pxStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pxStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pxStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nxStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nxStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pyStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pyStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pyStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nyStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nyStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nyStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pzStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pzStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nzStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(nzStepID);
        glDrawElements(GL_TRIANGLES, MeshManager.coneMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
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
            if (repeat) {
                return;
            }

            Vec3 delta = new Vec3();

            if      (currentID == pxSlideID) delta.x = 1.0f;
            else if (currentID == nxSlideID) delta.x = -1.0f;
            else if (currentID == pySlideID) delta.y = 1.0f;
            else if (currentID == nySlideID) delta.y = -1.0f;
            else if (currentID == pzSlideID) delta.z = 1.0f;
            else if (currentID == nzSlideID) delta.z = -1.0f;

            slideVelocity = delta.mult(SLIDE_SPEED);
        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            slideVelocity = null;
        }
    }

    private static class StepListener extends InputAdapter implements IdentityListener {

        private int currentID = Main.NO_IDENTITY;

        @Override
        public void gainedHover(int id) {
            currentID = id;
        }

        @Override
        public void lostHover(int id) {
            currentID = Main.NO_IDENTITY;
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
            if (repeat) {
                return;
            }

            Vec3 delta = new Vec3();

            if (currentID == pxStepID) delta.x += STEP_SPEED;
            else if (currentID == nxStepID) delta.x -= STEP_SPEED;
            else if (currentID == pyStepID) delta.y += STEP_SPEED;
            else if (currentID == nyStepID) delta.y -= STEP_SPEED;
            else if (currentID == pzStepID) delta.z += STEP_SPEED;
            else if (currentID == nzStepID) delta.z -= STEP_SPEED;

            MainScene.moveSphere(delta);
        }
    }
}
