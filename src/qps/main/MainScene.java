package qps.main;

import qps.*;

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
    private static int selectedIndex;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheres = new ArrayList<ChargedSphere>(MAX_SPHERES);
        spheresVAO = new VAO(MeshManager.sphereMesh, MAX_SPHERES, null, null, null, GL_STREAM_DRAW);
        selectedIndex = Main.NO_IDENTITY;

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

        UniformGlobals.ModelGlobals.setModelMat(new Mat4());
        UniformGlobals.ModelGlobals.setNormMat(new Mat4());
        UniformGlobals.ModelGlobals.buffer();
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0, spheres.size());

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void addSphere(ChargedSphere sphere) {
        spheres.add(sphere);
        spheresVAO.bufferInstanceMat(spheres.size() - 1, sphere.modelMat());
        spheresVAO.bufferInstanceCharge(spheres.size() - 1, sphere.getCharge());
        int id = Main.registerIdentity(new SphereIdentityListener(spheres.size() - 1), null, null);
        spheresVAO.bufferInstanceID(spheres.size() - 1, id);
        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size());
        UniformGlobals.SphereChargesGlobals.set(spheres.size() - 1, sphere.getLoc(), sphere.getCharge());
    }

    public static void moveSphere(Vec3 delta) {
        if (selectedIndex != Main.NO_IDENTITY) {
            ChargedSphere sphere = spheres.get(selectedIndex);
            sphere.translate(delta);
            spheresVAO.bufferInstanceMat(selectedIndex, sphere.modelMat());
            UniformGlobals.SphereChargesGlobals.set(selectedIndex, sphere.getLoc(), sphere.getCharge());
        }
    }

    private static class SphereIdentityListener implements IdentityListener {

        private int sphereIndex;

        public SphereIdentityListener(int sphereIndex) {
            this.sphereIndex = sphereIndex;
        }

        @Override
        public void gainedHover(int id) {

        }

        @Override
        public void lostHover(int id) {

        }

        @Override
        public boolean gainedSelect(int id) {
            selectedIndex = sphereIndex;

            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedIndex = Main.NO_IDENTITY;

            return true;
        }
    }

}
