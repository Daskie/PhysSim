package qps.scenes.main;

import qps.*;
import qps.scenes.cardinal.CardinalScene;
import qps.charges.ChargedLine;
import qps.charges.ChargedObject;
import qps.charges.ChargedPlane;
import qps.charges.ChargedSphere;
import qps.input_listeners.InputAdapter;
import qps.utils.Mat4;
import qps.utils.Quaternion;
import qps.utils.Utils;
import qps.utils.Vec3;

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

    public static final int MAX_OBJECTS = 128;

    private static MainProgram program;
    private static VAO spheresVAO;
    private static VAO planesVAO;
    private static VAO linesVAO;
    private static ArrayList<ChargedSphere> spheres;
    private static ArrayList<ChargedPlane> planes;
    private static ArrayList<ChargedLine> lines;
    private static ChargedSphere selectedSphere;
    private static ChargedPlane selectedPlane;
    private static ChargedLine selectedLine;
    private static HashMap<ChargedObject, Integer> objectIDs;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheresVAO = new VAO(MeshManager.sphereMesh, MAX_OBJECTS, null, null, null, null, GL_STREAM_DRAW);
        planesVAO = new VAO(MeshManager.doubleSquareMesh, MAX_OBJECTS, null, null, null, null, GL_STREAM_DRAW);
        linesVAO = new VAO(MeshManager.cylinderMesh, MAX_OBJECTS, null, null, null, null, GL_STREAM_DRAW);
        spheres = new ArrayList<ChargedSphere>();
        planes = new ArrayList<ChargedPlane>();
        lines = new ArrayList<ChargedLine>();

        objectIDs = new HashMap<ChargedObject, Integer>();

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

        glBindVertexArray(planesVAO.vao());
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.doubleSquareMesh.nIndices(), GL_UNSIGNED_INT, 0, planes.size());

        glBindVertexArray(linesVAO.vao());
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cylinderMesh.nIndices(), GL_UNSIGNED_INT, 0, lines.size());

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        return true;
    }

    public static void addSphere(ChargedSphere sphere) {
        spheres.add(sphere);
        int index = spheres.size() - 1;

        SphereListener listener = new SphereListener(sphere);
        int id = Main.registerIdentity(listener, null, new InputListener());
        CardinalScene.registerListener(id, listener);
        objectIDs.put(sphere, id);
        Main.select(id);

        Mat4 mat = sphere.modelMat();
        spheresVAO.bufferInstanceModelMat(index, mat);
        spheresVAO.bufferInstanceNormMat(index, new Mat4(mat.inv().trans()));
        spheresVAO.bufferInstanceCharge(index, sphere.getCharge());
        spheresVAO.bufferInstanceID(index, id);

        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size());
        UniformGlobals.SphereChargesGlobals.set(index, sphere.getLoc(), sphere.getCharge());
    }

    public static void removeSphere() {
        if (selectedSphere == null) {
            return;
        }

        int index = spheres.indexOf(selectedSphere);

        int id = objectIDs.get(selectedSphere);
        Main.unregisterIdentity(id);
        CardinalScene.unregisterListener(id);
        objectIDs.remove(selectedSphere);

        spheresVAO.removeInstance(index);

        UniformGlobals.ChargeCountsGlobals.setSphereCount(spheres.size() - 1);
        UniformGlobals.SphereChargesGlobals.remove(index);

        spheres.remove(selectedSphere);
        selectedSphere = null;
    }

    public static void addPlane(ChargedPlane plane) {
        planes.add(plane);
        int index = planes.size() - 1;

        PlaneListener listener = new PlaneListener(plane);
        int id = Main.registerIdentity(listener, null, new InputListener());
        CardinalScene.registerListener(id, listener);
        objectIDs.put(plane, id);
        Main.select(id);


        Mat4 mat = plane.modelMat();
        planesVAO.bufferInstanceModelMat(index, mat);
        planesVAO.bufferInstanceNormMat(index, new Mat4(mat.inv().trans()));
        planesVAO.bufferInstanceCharge(index, plane.getCharge());
        planesVAO.bufferInstanceID(index, id);

        UniformGlobals.ChargeCountsGlobals.setPlaneCount(planes.size());
        UniformGlobals.PlaneChargesGlobals.set(index, plane.getForward(), plane.getCharge(), plane.getVec());
    }

    public static void removePlane() {
        if (selectedPlane == null) {
            return;
        }

        int index = planes.indexOf(selectedPlane);

        int id = objectIDs.get(selectedPlane);
        Main.unregisterIdentity(id);
        CardinalScene.unregisterListener(id);
        objectIDs.remove(selectedPlane);

        planesVAO.removeInstance(index);

        UniformGlobals.ChargeCountsGlobals.setPlaneCount(planes.size() - 1);
        UniformGlobals.PlaneChargesGlobals.remove(index);

        planes.remove(selectedPlane);
        selectedPlane = null;
    }

    public static void addLine(ChargedLine line) {
        lines.add(line);
        int index = lines.size() - 1;

        LineListener listener = new LineListener(line);
        int id = Main.registerIdentity(listener, null, new InputListener());
        CardinalScene.registerListener(id, listener);
        objectIDs.put(line, id);
        Main.select(id);

        Mat4 mat = line.modelMat();
        linesVAO.bufferInstanceModelMat(index, mat);
        linesVAO.bufferInstanceNormMat(index, new Mat4(mat.inv().trans()));
        linesVAO.bufferInstanceCharge(index, line.getCharge());
        linesVAO.bufferInstanceID(index, id);

        UniformGlobals.ChargeCountsGlobals.setLineCount(lines.size());
        UniformGlobals.LineChargesGlobals.set(index, line.getLoc(), line.getCharge(), line.getForward());
    }

    public static void removeLine() {
        if (selectedLine == null) {
            return;
        }

        int index = lines.indexOf(selectedLine);

        int id = objectIDs.get(selectedLine);
        Main.unregisterIdentity(id);
        CardinalScene.unregisterListener(id);
        objectIDs.remove(selectedLine);

        linesVAO.removeInstance(index);

        UniformGlobals.ChargeCountsGlobals.setLineCount(lines.size() - 1);
        UniformGlobals.LineChargesGlobals.remove(index);

        lines.remove(selectedLine);
        selectedLine = null;
    }

    public static void remove() {
        if (selectedSphere != null) {
            removeSphere();
        }
        if (selectedPlane != null) {
            removePlane();
        }
        if (selectedLine != null) {
            removeLine();
        }
    }

    public static void clear() {
        int id;
        for (ChargedSphere sphere : spheres) {
            id = objectIDs.get(sphere);
            Main.unregisterIdentity(id);
            CardinalScene.unregisterListener(id);
        }
        for (ChargedPlane plane : planes) {
            id = objectIDs.get(plane);
            Main.unregisterIdentity(id);
            CardinalScene.unregisterListener(id);
        }
        for (ChargedLine line : lines) {
            id = objectIDs.get(line);
            Main.unregisterIdentity(id);
            CardinalScene.unregisterListener(id);
        }
        objectIDs.clear();

        UniformGlobals.ChargeCountsGlobals.setSphereCount(0);
        UniformGlobals.ChargeCountsGlobals.setLineCount(0);
        UniformGlobals.ChargeCountsGlobals.setPlaneCount(0);

        spheres.clear();
        planes.clear();
        lines.clear();
        selectedSphere = null;
        selectedPlane = null;
        selectedLine = null;
    }

    public static void moveSphere(Vec3 delta) {
        if (selectedSphere == null) {
            return;
        }

        selectedSphere.translate(delta);
        Mat4 mat = selectedSphere.modelMat();
        int i = spheres.indexOf(selectedSphere);
        spheresVAO.bufferInstanceModelMat(i, mat);
        spheresVAO.bufferInstanceNormMat(i, new Mat4(mat.inv().trans()));
        UniformGlobals.SphereChargesGlobals.set(i, selectedSphere.getLoc(), selectedSphere.getCharge());
    }

    public static void movePlane(Vec3 delta) {
        if (selectedPlane == null) {
            return;
        }

        selectedPlane.translate(delta);
        Mat4 mat = selectedPlane.modelMat();
        int i = planes.indexOf(selectedPlane);
        planesVAO.bufferInstanceModelMat(i, mat);
        planesVAO.bufferInstanceNormMat(i, new Mat4(mat.inv().trans()));
        UniformGlobals.PlaneChargesGlobals.set(i, selectedPlane.getForward(), selectedPlane.getCharge(), selectedPlane.getVec());
    }

    public static void moveLine(Vec3 delta) {
        if (selectedLine == null) {
            return;
        }

        selectedLine.translate(delta);
        Mat4 mat = selectedLine.modelMat();
        int i = lines.indexOf(selectedLine);
        linesVAO.bufferInstanceModelMat(i, mat);
        linesVAO.bufferInstanceNormMat(i, new Mat4(mat.inv().trans()));
        UniformGlobals.LineChargesGlobals.set(i, selectedLine.getLoc(), selectedLine.getCharge(), selectedLine.getForward());
    }

    public static void rotatePlane(Vec3 axis, float theta) {
        if (selectedPlane == null) {
            return;
        }

        selectedPlane.rotate(Quaternion.angleAxis(theta, axis));
        Mat4 mat = selectedPlane.modelMat();
        int i = planes.indexOf(selectedPlane);
        planesVAO.bufferInstanceModelMat(i, mat);
        planesVAO.bufferInstanceNormMat(i, new Mat4(mat.inv().trans()));
        UniformGlobals.PlaneChargesGlobals.set(i, selectedPlane.getForward(), selectedPlane.getCharge(), selectedPlane.getVec());
    }

    public static void rotateLine(Vec3 axis, float theta) {
        if (selectedLine == null) {
            return;
        }

        selectedLine.rotate(Quaternion.angleAxis(theta, axis));
        Mat4 mat = selectedLine.modelMat();
        int i = lines.indexOf(selectedLine);
        linesVAO.bufferInstanceModelMat(i, mat);
        linesVAO.bufferInstanceNormMat(i, new Mat4(mat.inv().trans()));
        UniformGlobals.LineChargesGlobals.set(i, selectedLine.getLoc(), selectedLine.getCharge(), selectedLine.getForward());
    }

    public static ChargedObject getSelected() {
        return selectedSphere != null ? selectedSphere : selectedPlane != null ? selectedPlane : selectedLine;
    }

    public static float calculateE(Vec3 p) {
        float e = 0.0f;

        for (ChargedSphere sphere : spheres) {
            e += Utils.calcSphereE(sphere, p);
        }
        for (ChargedPlane plane : planes) {
            e += Utils.calcPlaneE(plane, p);
        }
        for (ChargedLine line : lines) {
            e += Utils.calcLineE(line, p);
        }

        return e;
    }

    public static float calculateV(Vec3 p) {
        float v = 0.0f;

        for (ChargedSphere sphere : spheres) {
            v += Utils.calcSphereV(sphere, p);
        }
        for (ChargedPlane plane : planes) {
            v += Utils.calcPlaneV(plane, p);
        }
        for (ChargedLine line : lines) {
            v += Utils.calcLineV(line, p);
        }

        return v;
    }

    private static class SphereListener implements IdentityListener, CardinalListener {

        private ChargedSphere sphere;

        public SphereListener(ChargedSphere sphere) {
            this.sphere = sphere;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedSphere = sphere;
            selectedPlane = null;
            selectedLine = null;

            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedSphere = null;
            return true;
        }

        @Override
        public void move(int id, Vec3 delta) {
            moveSphere(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = selectedSphere.getLoc();
            moveSphere(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {}

    }

    private static class PlaneListener implements IdentityListener, CardinalListener {

        private ChargedPlane plane;

        public PlaneListener(ChargedPlane plane) {
            this.plane = plane;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedPlane = plane;
            selectedSphere = null;
            selectedLine = null;

            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedPlane = null;
            return true;
        }

        @Override
        public void move(int id, Vec3 delta) {
            movePlane(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = selectedPlane.getLoc();
            movePlane(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {
            rotatePlane(axis, theta);
        }

    }

    private static class LineListener implements IdentityListener, CardinalListener {

        private ChargedLine line;

        public LineListener(ChargedLine line) {
            this.line = line;
        }

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selectedLine = line;
            selectedSphere = null;
            selectedPlane = null;

            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selectedLine = null;
            return true;
        }

        @Override
        public void move(int id, Vec3 delta) {
            moveLine(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = selectedLine.getLoc();
            moveLine(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {
            rotateLine(axis, theta);
        }

    }

    private static class InputListener extends InputAdapter {
        @Override
        public void mouseReleased(int button, boolean shift, boolean ctrl, boolean alt, InputManager manager) {
            if (shift) {
                ChargedObject object = selectedSphere != null ? selectedSphere : selectedLine != null ? selectedLine : selectedPlane;
                if (object != null) {
                    Main.getCamera().lookAt(object.getLoc());
                }
            }
        }
    }

}
