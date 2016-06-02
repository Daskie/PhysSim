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

    public static boolean init() {
        program = new SensorProgram();
        program.init();
        sphereVAO = new VAO(MeshManager.sphereMesh, 0, null, null, null, GL_STATIC_DRAW);

        //sphereVAO.bufferColors(0, MeshManager.sphereMesh.nVertices(), new Vec4());
        int id = Main.registerIdentity(null, null, null);
        CardinalScene.registerListener(id, new SensorListener());
        program.setID(id);

        sensor = new Entity();

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

        UniformGlobals.ModelGlobals.setModelMat(sensor.modelMat());
        UniformGlobals.ModelGlobals.setNormMat(new Mat4());
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void move(Vec3 delta) {
        sensor.translate(delta);
    }

    private static class SensorListener implements CardinalListener {
        @Override
        public void move(int id, Vec3 delta) {
            SensorScene.move(delta);
        }
    }

}
