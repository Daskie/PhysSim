package qps.main;

import qps.*;
import qps.cardinal.CardinalScene;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class MainScene {

    private static final int MAX_SPHERES = 128;

    private static MainProgram program;
    private static VAO spheresVAO;
    private static ArrayList<ChargedSphere> spheres;
    private static int selectedSphere;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheres = new ArrayList<ChargedSphere>(MAX_SPHERES);
        selectedSphere = -1;
        spheresVAO = new VAO(MeshManager.sphereMesh, MAX_SPHERES, null, null, null, GL_STREAM_DRAW);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize main scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(spheresVAO.vao());

        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0, spheres.size());

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void addSphere(ChargedSphere sphere) {
        spheres.add(sphere);
        spheresVAO.bufferInstanceMat(spheres.size() - 1, sphere.modelMat());
        spheresVAO.bufferInstanceCharge(spheres.size() - 1, sphere.getCharge());
        int id = Main.registerIdentity(new SphereIdentityListener(spheres.size() - 1), null, null);
        CardinalScene.registerListener(id, new SphereCardinalListener());
        spheresVAO.bufferInstanceID(spheres.size() - 1, id);
        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size());
        UniformGlobals.SphereChargesGlobals.set(spheres.size() - 1, sphere.getLoc(), sphere.getCharge());
    }

    public static void moveSphere(Vec3 delta) {
        if (selectedSphere >= 0) {
            ChargedSphere sphere = spheres.get(selectedSphere);
            sphere.translate(delta);
            spheresVAO.bufferInstanceMat(selectedSphere, sphere.modelMat());
            UniformGlobals.SphereChargesGlobals.set(selectedSphere, sphere.getLoc(), sphere.getCharge());
        }
    }

    public static ChargedSphere getSelected() {
        if (selectedSphere >= 0) {
            return spheres.get(selectedSphere);
        }

        return null;
    }

    private static class SphereCardinalListener implements CardinalListener {
        @Override
        public void move(int id, Vec3 delta) {
            moveSphere(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = spheres.get(selectedSphere).getLoc();
            spheres.get(selectedSphere).resetRotation();
            moveSphere(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {

        }
    }

    private static class SphereIdentityListener implements IdentityListener {

        private int sphereI;

        public SphereIdentityListener(int sphereI) {
            this.sphereI = sphereI;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedSphere = sphereI;
            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedSphere = -1;
            return true;
        }
    }

}
