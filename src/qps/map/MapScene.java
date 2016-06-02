package qps.map;

import qps.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

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

    private static Vec3[] planeDirs;
    private static Mat4[] planeMats;
    private static float[] planeDistances;

    public static boolean init() {
        program = new MapProgram();
        program.init();

        planeVAO = new VAO(MeshManager.squareMesh, 0, null, null, null, GL_STREAM_DRAW);

        planeDirs = new Vec3[12];
        planeDirs[0] = new Vec3(1.0f, 1.0f, 0.0f);
        planeDirs[1] = new Vec3(-1.0f, 1.0f, 0.0f);
        planeDirs[2] = new Vec3(-1.0f, -1.0f, 0.0f);
        planeDirs[3] = new Vec3(1.0f, -1.0f, 0.0f);
        planeDirs[4] = new Vec3(0.0f, 1.0f, 1.0f);
        planeDirs[5] = new Vec3(0.0f, -1.0f, 1.0f);
        planeDirs[6] = new Vec3(0.0f, -1.0f, -1.0f);
        planeDirs[7] = new Vec3(0.0f, 1.0f, -1.0f);
        planeDirs[8] = new Vec3(1.0f, 0.0f, 1.0f);
        planeDirs[9] = new Vec3(1.0f, 0.0f, -1.0f);
        planeDirs[10] = new Vec3(-1.0f, 0.0f, -1.0f);
        planeDirs[11] = new Vec3(-1.0f, 0.0f, 1.0f);

        Mat4 scaleMat = new Mat4(Mat3.scale(1000.0f));
        Mat4 rotXMat = new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX));
        Mat4 rotYMat = new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY));

        planeMats = new Mat4[12];
        planeMats[0] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[0])));
        planeMats[1] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[1])));
        planeMats[2] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[2])));
        planeMats[3] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[3])));
        planeMats[4] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[4])).mult(rotYMat));
        planeMats[5] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[5])).mult(rotYMat));
        planeMats[6] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[6])).mult(rotYMat));
        planeMats[7] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[7])).mult(rotYMat));
        planeMats[8] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[8])).mult(rotXMat));
        planeMats[9] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[9])).mult(rotXMat));
        planeMats[10] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[10])).mult(rotXMat));
        planeMats[11] = new Mat4(scaleMat.mult(Mat4.translate(planeDirs[11])).mult(rotXMat));

        planeDistances = new float[12];

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

        Vec3 camLoc = Main.getCamera().loc();
        for (int i = 0; i < 12; ++i) {
            planeDistances[i] = camLoc.sub(planeDirs[i]).mag2();
        }

        TreeMap<Float, Integer> tree = new TreeMap<Float, Integer>();
        for (int i = 0; i < 12; ++i) {
            tree.put(planeDistances[i], i);
        }

        glDisable(GL_CULL_FACE);

        for (Integer i : tree.descendingMap().values()) {
            UniformGlobals.ModelGlobals.setModelMat(planeMats[i]);
            UniformGlobals.ModelGlobals.buffer();
            glDrawElements(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0);
        }

        glEnable(GL_CULL_FACE);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
