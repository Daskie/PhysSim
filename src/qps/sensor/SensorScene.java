package qps.sensor;

import qps.*;
import qps.cardinal.CardinalScene;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class SensorScene {

    private static SensorProgram program;
    private static VAO sphereVAO;
    private static Entity sensor;
    private static Mat4 sensorMat;
    private static Mat4 sensorNormMat;
    private static boolean selected;

    public static boolean init() {
        program = new SensorProgram();
        program.init();
        sphereVAO = new VAO(MeshManager.sphereMesh, 0, null, null, null, GL_STATIC_DRAW);

        //sphereVAO.bufferColors(0, MeshManager.sphereMesh.nVertices(), new Vec4());
        int id = Main.registerIdentity(new SensorIdentityListener(), null, null);
        CardinalScene.registerListener(id, new SensorListener());
        program.setID(id);

        sensor = new Entity();
        sensorMat = new Mat4();
        sensorNormMat = new Mat4(sensorMat.inv().trans());

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
        glBindVertexArray(sphereVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(sensorMat);
        UniformGlobals.ModelGlobals.setNormMat(sensorNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void move(Vec3 delta) {
        sensor.translate(delta);
        sensorMat = sensor.modelMat();
        sensorNormMat = new Mat4(sensorMat.inv().trans());
    }

    public static void rotate(Vec3 axis, float theta) {
        sensor.rotate(Quaternion.angleAxis(theta, axis));
        sensorMat = sensor.modelMat();
        sensorNormMat = new Mat4(sensorMat.inv().trans());
    }

    public static Vec3 getSensorLoc() {
        return sensor.getLoc();
    }

    public static Mat4 getSensorMat() {
        return sensorMat;
    }

    public static boolean isSelected() {
        return selected;
    }

    private static class SensorListener implements CardinalListener {
        @Override
        public void move(int id, Vec3 delta) {
            SensorScene.move(delta);
        }

        @Override
        public void round(int id) {
            Vec3 loc = sensor.getLoc();
            sensor.resetRotation();
            SensorScene.move(new Vec3(Math.round(loc.x) - loc.x, Math.round(loc.y) - loc.y, Math.round(loc.z) - loc.z));
        }

        @Override
        public void rotate(int id, Vec3 axis, float theta) {
            SensorScene.rotate(axis, theta);
        }
    }

    private static class SensorIdentityListener implements IdentityListener {

        @Override
        public void gainedHover(int id) {}

        @Override
        public void lostHover(int id) {}

        @Override
        public boolean gainedSelect(int id) {
            selected = true;
            return true;
        }

        @Override
        public boolean lostSelect(int id) {
            selected = false;
            return true;
        }
    }

}
