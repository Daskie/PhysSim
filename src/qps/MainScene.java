package qps;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class MainScene {

    private static final int MAX_SPHERES = 100;

    private static MainProgram program;

    private static ArrayList<ChargedSphere> spheres;
    private static VAO spheresVAO;

    public static boolean init() {
        program = new MainProgram();
        program.init();
        spheres = new ArrayList<ChargedSphere>(MAX_SPHERES);
        Mat4[] mats = new Mat4[MAX_SPHERES];
        for (int i = 0; i < mats.length; ++i) {
            mats[i] = new Mat4();
        }
        spheresVAO = new VAO(MeshManager.sphereMesh, MAX_SPHERES, mats, GL_DYNAMIC_DRAW);

        return true;
    }

    public static void draw() {
        glUseProgram(program.id());

        glBindVertexArray(spheresVAO.vao());
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.sphereMesh.nIndices(), GL_UNSIGNED_INT, 0, spheres.size());

        glUseProgram(0);
    }

    public static void addSphere(ChargedSphere sphere) {
        spheres.add(sphere);
        spheresVAO.bufferInstanceMat(new Mat4(sphere.modelMat()), spheres.size() - 1);
    }

}
