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

    private static GridProgram program;
    //private static VAO planeVAO;
    private static VAO cubeVAO;

    //private static Mat4 modelMat;
    //private static int divisions;
    private static int size;
    private static float spacing;
    private static float thickness;

    public static boolean init() {
        program = new GridProgram();
        program.init();

        //planeVAO = new VAO(MeshManager.squareMesh, 0, null, null, null, GL_STREAM_DRAW);
        cubeVAO = new VAO(MeshManager.cubeMesh, 0, null, null, null, GL_STATIC_DRAW);

        //modelMat = new Mat4(Mat3.scale(500.0f));
        //divisions = 1000;
        size = 100;
        spacing = 1.0f;
        thickness = 0.005f;

        //program.setDivisions(divisions);
        program.setSize(size);
        program.setSpacing(spacing);
        program.setThickness(thickness);

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
        //glBindVertexArray(planeVAO.vao());
        glBindVertexArray(cubeVAO.vao());

        //glDisable(GL_CULL_FACE);

        //UniformGlobals.ModelGlobals.setModelMat(modelMat);
        //UniformGlobals.ModelGlobals.buffer();
        //glDrawElements(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0);
        program.setDir(Vec3.POSX);
        program.setLong(Vec3.POSY);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);
        program.setDir(Vec3.POSY);
        program.setLong(Vec3.POSX);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);

        program.setDir(Vec3.POSY);
        program.setLong(Vec3.POSZ);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);
        program.setDir(Vec3.POSZ);
        program.setLong(Vec3.POSY);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);

        program.setDir(Vec3.POSZ);
        program.setLong(Vec3.POSX);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);
        program.setDir(Vec3.POSX);
        program.setLong(Vec3.POSZ);
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, size + 1);

        //glEnable(GL_CULL_FACE);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
