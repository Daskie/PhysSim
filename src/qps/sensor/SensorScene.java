package qps.sensor;

import qps.*;

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

    /*private static SensorProgram program;
    private static VAO sphereVAO;

    public static boolean init() {
        program = new SensorProgram();
        program.init();
        sphereVAO = new VAO(MeshManager.sphereMesh, 0, null, null, null, GL_STATIC_DRAW);

        //sphereVAO.bufferColors(0, MeshManager.sphereMesh.nVertices(), new Vec4());
        int id = Main.registerIdentity(new CardinalListener(), null, null);
        program.setID(id);

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

        glDrawElements(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static void moveSphere(Vec3 delta) {
        if (selectedIndex != Main.NO_IDENTITY) {
            ChargedSphere sphere = spheres.get(selectedIndex);
            sphere.translate(delta);
            sphereVAO.bufferInstanceMat(selectedIndex, sphere.modelMat());
            UniformGlobals.SphereChargesGlobals.set(selectedIndex, sphere.getLoc(), sphere.getCharge());
        }
    }

    public static ChargedObject getSelected() {
        if (selectedIndex != Main.NO_IDENTITY) {
            return spheres.get(selectedIndex);
        }

        return null;
    }

    private static class CardinalListener implements IdentityListener {

        @Override
        public void gainedHover(int id) {

        }

        @Override
        public void lostHover(int id) {

        }

        @Override
        public boolean gainedSelect(int id) {

            return true;
        }

        @Override
        public boolean lostSelect(int id) {

            return true;
        }
    }*/

}
