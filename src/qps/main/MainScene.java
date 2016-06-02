package qps.main;

import qps.*;
import qps.cardinal.CardinalScene;

import java.util.ArrayList;
import java.util.HashMap;

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
    private static HashMap<Integer, Integer> sphereIDs;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheres = new ArrayList<ChargedSphere>(MAX_SPHERES);
        sphereIDs = new HashMap<Integer, Integer>(MAX_SPHERES);
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
        int id = Main.registerIdentity(null, null, null);
        sphereIDs.put(id, spheres.size() - 1);
        CardinalScene.registerListener(id, new SphereListener());
        spheresVAO.bufferInstanceID(spheres.size() - 1, id);
        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size());
        UniformGlobals.SphereChargesGlobals.set(spheres.size() - 1, sphere.getLoc(), sphere.getCharge());
    }

    public static void moveSphere(int id, Vec3 delta) {
        if (sphereIDs.containsKey(id)) {
            int sphereI = sphereIDs.get(id);
            ChargedSphere sphere = spheres.get(sphereI);
            sphere.translate(delta);
            spheresVAO.bufferInstanceMat(sphereI, sphere.modelMat());
            UniformGlobals.SphereChargesGlobals.set(sphereI, sphere.getLoc(), sphere.getCharge());
        }
    }

    public static ChargedSphere getSelected() {
        if (sphereIDs.containsKey(Main.getSelected())) {
            return spheres.get(sphereIDs.get(Main.getSelected()));
        }

        return null;
    }

    private static class SphereListener implements CardinalListener {
        @Override
        public void move(int id, Vec3 delta) {
            moveSphere(id, delta);
        }
    }

}
