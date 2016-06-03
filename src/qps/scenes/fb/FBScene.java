package qps.scenes.fb;

import qps.MeshManager;
import qps.utils.Utils;
import qps.VAO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * @since 5/29/2016
 */
public abstract class FBScene {

    private static FBProgram program;
    private static VAO squareVAO;

    private static int texID;

    public static boolean init() {
        program = new FBProgram();
        program.init();

        texID = 0;

        squareVAO = new VAO(MeshManager.squareMesh, 0, null, null, null, null, GL_STATIC_DRAW);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize fb scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(squareVAO.vao());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);

        glDrawElements(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindTexture(GL_TEXTURE_2D, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        return true;
    }

    public static void setTex(int texID) {
        FBScene.texID = texID;
    }

}
