package qps.sensor;

import qps.*;
import qps.cardinal.CardinalScene;
import qps.main.MainScene;

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
    private static VAO sensorVAO;
    private static Entity sensor;
    private static Mat4 modelMat;
    private static Mat4 sensorMat;
    private static Mat4 sensorNormMat;
    private static boolean selected;

    private static Meter meter;

    public static boolean init() {
        program = new SensorProgram();
        program.init();
        sensorVAO = new VAO(MeshManager.tetraMesh, 0, null, null, null, null, GL_STATIC_DRAW);
        sensorVAO.bufferColors(0, MeshManager.tetraMesh.nVertices(), new Vec4(0.0f, 1.0f, 0.0f, 1.0f));

        int id = Main.registerIdentity(new SensorIdentityListener(), null, null);
        CardinalScene.registerListener(id, new SensorListener());
        program.setID(id);

        sensor = new Entity();
        modelMat = new Mat4(Mat3.scale(0.25f));
        sensorMat = modelMat;
        sensorNormMat = new Mat4(sensorMat.inv().trans());

        meter = new Meter();

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize main scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {
        meter.setE(MainScene.calculateE(sensor.getLoc()));
        meter.setV(MainScene.calculateV(sensor.getLoc()));
    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(sensorVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(sensorMat);
        UniformGlobals.ModelGlobals.setNormMat(sensorNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.tetraMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        meter.cleanup();

        return true;
    }

    public static void move(Vec3 delta) {
        sensor.translate(delta);
        sensorMat = new Mat4(sensor.modelMat().mult(modelMat));
        sensorNormMat = new Mat4(sensorMat.inv().trans());
    }

    public static void rotate(Vec3 axis, float theta) {
        sensor.rotate(Quaternion.angleAxis(theta, axis));
        sensorMat = new Mat4(sensor.modelMat().mult(modelMat));
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
