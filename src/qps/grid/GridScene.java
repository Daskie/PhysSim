package qps.grid;

import qps.*;
import qps.map.MapProgram;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/31/2016
 */
public abstract class GridScene {

    private static final int SIZE = 100;
    private static final float SPACING = 1.0f;
    private static final float THICKNESS = 0.005f;
    private static final Vec4 GRID_COLOR = new Vec4(0.1f, 0.1f, 0.1f, 1.0f);
    private static final float CRITICAL_ANGLE = (float)Math.toRadians(75);

    private static GridProgram program;
    private static VAO cubeVAO;


    public static boolean init() {
        program = new GridProgram();
        program.init();

        cubeVAO = new VAO(MeshManager.cubeMesh, 0, null, null, null, GL_STATIC_DRAW);

        program.setSize(SIZE);
        program.setSpacing(SPACING);
        program.setThickness(THICKNESS);
        program.setColor(GRID_COLOR);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize grid scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(cubeVAO.vao());

        Vec3 camForward = Main.getCamera().forward();

        if (Math.acos(Math.abs(camForward.dot(Vec3.POSZ))) <= CRITICAL_ANGLE) {
            program.setDir(Vec3.POSX);
            program.setLong(Vec3.POSY);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
            program.setDir(Vec3.POSY);
            program.setLong(Vec3.POSX);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
        }

        if (Math.acos(Math.abs(camForward.dot(Vec3.POSX))) <= CRITICAL_ANGLE) {
            program.setDir(Vec3.POSY);
            program.setLong(Vec3.POSZ);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
            program.setDir(Vec3.POSZ);
            program.setLong(Vec3.POSY);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
        }

        if (Math.acos(Math.abs(camForward.dot(Vec3.POSY))) <= CRITICAL_ANGLE) {
            program.setDir(Vec3.POSZ);
            program.setLong(Vec3.POSX);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
            program.setDir(Vec3.POSX);
            program.setLong(Vec3.POSZ);
            glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, SIZE + 1);
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
