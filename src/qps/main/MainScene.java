package qps.main;

import qps.*;
import qps.cardinal.CardinalScene;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class MainScene {

    public static final int MAX_OBJECTS = 128;

    private static MainProgram program;
    private static VAO spheresVAO;
    private static VAO planesVAO;
    private static ArrayList<ChargedSphere> spheres;
    private static ArrayList<ChargedPlane> planes;
    private static int selectedIndex;
    private static ChargedObject selectedObject;
    private static Mat4 scaleMat;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheresVAO = new VAO(MeshManager.sphereMesh, MAX_OBJECTS, null, null, null, GL_STREAM_DRAW);
        planesVAO = new VAO(MeshManager.squareMesh, MAX_OBJECTS, null, null, null, GL_STREAM_DRAW);
        spheres = new ArrayList<ChargedSphere>();
        planes = new ArrayList<ChargedPlane>();
        selectedIndex = -1;

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

        glDisable(GL_CULL_FACE);
        glBindVertexArray(planesVAO.vao());
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0, planes.size());
        glEnable(GL_CULL_FACE);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void addSphere(ChargedSphere sphere) {
        spheres.add(sphere);
        int index = spheres.size() - 1;

        SphereListener listener = new SphereListener(index);
        int id = Main.registerIdentity(listener, null, null);
        CardinalScene.registerListener(id, listener);

        spheresVAO.bufferInstanceMat(index, sphere.modelMat());
        spheresVAO.bufferInstanceCharge(index, sphere.getCharge());
        spheresVAO.bufferInstanceID(index, id);

        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size());
        UniformGlobals.SphereChargesGlobals.set(index, sphere.getLoc(), sphere.getCharge());
    }

    public static void addPlane(ChargedPlane plane) {
        planes.add(plane);
        int index = planes.size() - 1;

        PlaneListener listener = new PlaneListener(index);
        int id = Main.registerIdentity(listener, null, null);
        CardinalScene.registerListener(id, listener);

        planesVAO.bufferInstanceMat(index, plane.modelMat());
        planesVAO.bufferInstanceCharge(index, plane.getCharge());
        planesVAO.bufferInstanceID(index, id);

        UniformGlobals.ChargeCountsGlobals.setPlaneCount(spheres.size());
        UniformGlobals.PlaneChargesGlobals.set(index, plane.getForward(), plane.getCharge());
    }

    public static void moveSphere(Vec3 delta) {
        if (selectedIndex >= 0) {
            ChargedSphere sphere = spheres.get(selectedIndex);
            sphere.translate(delta);
            spheresVAO.bufferInstanceMat(selectedIndex, sphere.modelMat());
            UniformGlobals.SphereChargesGlobals.set(selectedIndex, sphere.getLoc(), sphere.getCharge());
        }
    }

    public static void movePlane(Vec3 delta) {
        if (selectedIndex >= 0) {
            ChargedPlane plane = planes.get(selectedIndex);
            plane.translate(delta);
            planesVAO.bufferInstanceMat(selectedIndex, plane.modelMat());
            UniformGlobals.PlaneChargesGlobals.set(selectedIndex, plane.getForward(), plane.getCharge());
        }
    }

    public static void rotatePlane(Vec3 axis, float theta) {
        if (selectedIndex >= 0) {
            ChargedPlane plane = planes.get(selectedIndex);
            plane.rotate(Quaternion.angleAxis(theta, axis));
            planesVAO.bufferInstanceMat(selectedIndex, plane.modelMat());
            UniformGlobals.PlaneChargesGlobals.set(selectedIndex, plane.getForward(), plane.getCharge());
        }
    }

    public static ChargedObject getSelected() {
        return selectedObject;
    }

    private static class SphereListener implements IdentityListener, CardinalListener {

        private int sphereI;

        public SphereListener(int sphereI) {
            this.sphereI = sphereI;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedIndex = sphereI;
            selectedObject = spheres.get(sphereI);
            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedIndex = -1;
            selectedObject = null;
            return true;
        }

        @Override
        public void move(int id, Vec3 delta) {
            moveSphere(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = selectedObject.getLoc();
            moveSphere(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {}

    }

    private static class PlaneListener implements IdentityListener, CardinalListener {

        private int planeI;

        public PlaneListener(int planeI) {
            this.planeI = planeI;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedIndex = planeI;
            selectedObject = planes.get(planeI);
            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedIndex = -1;
            selectedObject = null;
            return true;
        }

        @Override
        public void move(int id, Vec3 delta) {
            movePlane(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = selectedObject.getLoc();
            movePlane(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {
            rotatePlane(axis, theta);
        }

    }

}
