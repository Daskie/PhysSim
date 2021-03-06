package qps.scenes.hud;

import qps.*;
import qps.charges.ChargedLine;
import qps.charges.ChargedPlane;
import qps.charges.ChargedSphere;
import qps.input_listeners.InputAdapter;
import qps.scenes.main.MainScene;
import qps.utils.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * @since 5/28/2016
 */
public abstract class HUDScene {

    private static final float MOVE_SLIDE_SPEED = 1.0f;
    private static final float MOVE_STEP_SIZE = 0.1f;
    private static final float MOVE_JUMP_SIZE = 1.0f;

    private static final float SPIN_SLIDE_SPEED = (float)Math.PI / 2.0f;
    private static final float SPIN_STEP_SIZE = (float)Math.PI / 12.0f;

    private static HUDProgram program;
    private static VAO sphereVAO;
    private static VAO lineVAO;
    private static VAO planeVAO;
    private static VAO deleteVAO;

    private static int pSphereID;
    private static int nSphereID;
    private static int pLineID;
    private static int nLineID;
    private static int pPlaneID;
    private static int nPlaneID;
    private static int deleteID;

    private static Vec2 pSpherePos;
    private static Vec2 nSpherePos;
    private static Vec2 pLinePos;
    private static Vec2 nLinePos;
    private static Vec2 pPlanePos;
    private static Vec2 nPlanePos;
    private static Vec2 deletePos;

    private static Mat4 sphereModelMat;
    private static Mat4 sphereNormMat;
    private static Mat4 lineModelMat;
    private static Mat4 lineNormMat;
    private static Mat4 planeModelMat;
    private static Mat4 planeNormMat;
    private static Mat4 deleteModelMat;
    private static Mat4 deleteNormMat;

    private static Vec3 pColor;
    private static Vec3 nColor;
    private static Vec3 deleteColor;

    public static boolean init() {
        program = new HUDProgram();
        program.init();

        program.setCamLoc(new Vec3(0.0f, 0.0f, 10.0f));
        program.setLightDir(new Vec3(-1.0f, -1.0f, -1.0f));
        program.setLightColor(new Vec3(1.0f, 1.0f, 1.0f));
        program.setLightStrength(0.75f);

        sphereVAO = new VAO(MeshManager.sphereMesh, 0, null, null, null, null, GL_STATIC_DRAW);
        lineVAO = new VAO(MeshManager.cylinderMesh, 0, null, null, null, null, GL_STATIC_DRAW);
        planeVAO = new VAO(MeshManager.doubleSquareMesh, 0, null, null, null, null, GL_STATIC_DRAW);
        deleteVAO = new VAO(MeshManager.crossMesh, 0, null, null, null, null, GL_STATIC_DRAW);

        pSphereID = Main.registerIdentity(null, new SphereListener(true), null);
        nSphereID = Main.registerIdentity(null, new SphereListener(false), null);
        pLineID = Main.registerIdentity(null, new LineListener(true), null);
        nLineID = Main.registerIdentity(null, new LineListener(false), null);
        pPlaneID = Main.registerIdentity(null, new PlaneListener(true), null);
        nPlaneID = Main.registerIdentity(null, new PlaneListener(false), null);
        deleteID = Main.registerIdentity(null, new DeleteListener(), null);

        Vec2 anchor = new Vec2(-0.95f, -0.9f);
        Vec2 spacing = new Vec2(0.1f, 0.0f);
        int i = 0;
        deletePos = anchor.add(spacing.mult(i++));
        pSpherePos = anchor.add(spacing.mult(i++));
        nSpherePos = anchor.add(spacing.mult(i++));
        pLinePos = anchor.add(spacing.mult(i++));
        nLinePos = anchor.add(spacing.mult(i++));
        pPlanePos = anchor.add(spacing.mult(i++));
        nPlanePos = anchor.add(spacing.mult(i++));

        sphereModelMat = new Mat4();
        sphereNormMat = new Mat4(sphereModelMat.inv().trans());
        lineModelMat = new Mat4(Mat3.rotate((float)Math.PI / 3.0f, Vec3.NEGX).mult(Mat3.scale(0.1f, 0.1f, 1.5f)));
        lineNormMat = new Mat4(lineModelMat.inv().trans());
        planeModelMat = new Mat4(Mat3.rotate((float)Math.PI / 3.0f, Vec3.NEGX).mult(Mat3.scale(0.8f)));
        planeNormMat = new Mat4(planeModelMat.inv().trans());
        deleteModelMat = new Mat4(Mat3.rotate((float)Math.PI / 6.0f, Vec3.NEGX).mult(Mat3.rotate((float)Math.PI / 4.0f, Vec3.POSZ).mult(Mat3.scale(0.5f))));
        deleteNormMat = new Mat4(deleteModelMat.inv().trans());

        pColor = new Vec3(1.0f, 0.0f, 0.0f);
        nColor = new Vec3(0.0f, 0.0f, 1.0f);
        deleteColor = new Vec3(0.2f, 0.2f, 0.2f);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize cardinal scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());

        glBindVertexArray(sphereVAO.vao());
        UniformGlobals.ModelGlobals.setModelMat(sphereModelMat);
        UniformGlobals.ModelGlobals.setNormMat(sphereNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pSphereID);
        program.setScreenPos(pSpherePos);
        program.setLightColor(pColor);
        glDrawElements(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0);
        program.setID(nSphereID);
        program.setScreenPos(nSpherePos);
        program.setLightColor(nColor);
        glDrawElements(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(lineVAO.vao());
        UniformGlobals.ModelGlobals.setModelMat(lineModelMat);
        UniformGlobals.ModelGlobals.setNormMat(lineNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pLineID);
        program.setScreenPos(pLinePos);
        program.setLightColor(pColor);
        glDrawElements(GL_TRIANGLES, MeshManager.cylinderMesh.nIndices(), GL_UNSIGNED_INT, 0);
        program.setID(nLineID);
        program.setScreenPos(nLinePos);
        program.setLightColor(nColor);
        glDrawElements(GL_TRIANGLES, MeshManager.cylinderMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(planeVAO.vao());
        UniformGlobals.ModelGlobals.setModelMat(planeModelMat);
        UniformGlobals.ModelGlobals.setNormMat(planeNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(pPlaneID);
        program.setScreenPos(pPlanePos);
        program.setLightColor(pColor);
        glDrawElements(GL_TRIANGLES, MeshManager.doubleSquareMesh.nIndices(), GL_UNSIGNED_INT, 0);
        program.setID(nPlaneID);
        program.setScreenPos(nPlanePos);
        program.setLightColor(nColor);
        glDrawElements(GL_TRIANGLES, MeshManager.doubleSquareMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(deleteVAO.vao());
        UniformGlobals.ModelGlobals.setModelMat(deleteModelMat);
        UniformGlobals.ModelGlobals.setNormMat(deleteNormMat);
        UniformGlobals.ModelGlobals.buffer();
        program.setID(deleteID);
        program.setScreenPos(deletePos);
        program.setLightColor(deleteColor);
        glDrawElements(GL_TRIANGLES, MeshManager.crossMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        return true;
    }

    private static class SphereListener extends InputAdapter {

        private boolean charge;

        public SphereListener(boolean charge) {
            this.charge = charge;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {

        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            MainScene.addSphere(new ChargedSphere(charge ? Main.UNIT_CHARGE : -Main.UNIT_CHARGE, new Vec3()));
        }
    }

    private static class LineListener extends InputAdapter {

        private boolean charge;

        public LineListener(boolean charge) {
            this.charge = charge;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {

        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            MainScene.addLine(new ChargedLine(charge ? Main.LINEAR_CHARGE : -Main.LINEAR_CHARGE, new Vec3()));
        }
    }

    private static class PlaneListener extends InputAdapter {

        private boolean charge;

        public PlaneListener(boolean charge) {
            this.charge = charge;
        }

        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {

        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            MainScene.addPlane(new ChargedPlane(charge ? Main.SURFACE_CHARGE : -Main.SURFACE_CHARGE, new Vec3()));
        }
    }

    private static class DeleteListener extends InputAdapter {
        @Override
        public void mousePressed(int button, boolean shift, boolean ctrl, boolean alt, boolean repeat, InputManager manager) {

        }

        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            if (shift) {
                MainScene.clear();
            }
            else {
                MainScene.remove();
            }
        }
    }
}
