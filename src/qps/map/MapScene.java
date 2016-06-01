package qps.map;

import qps.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class MapScene {

    private static MapProgram program;
    private static VAO planeVAO;

    private static Vec4 plane;
    private static Mat4 modelMat;

    public static boolean init() {
        program = new MapProgram();
        program.init();

        planeVAO = new VAO(MeshManager.squareMesh, 0, null, null, null, GL_STREAM_DRAW);

        plane = new Vec4();
        modelMat = new Mat4(Mat3.scale(1000.0f));

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize map scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(planeVAO.vao());

        glDisable(GL_CULL_FACE);

        UniformGlobals.ModelGlobals.setModelMat(modelMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glEnable(GL_CULL_FACE);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
