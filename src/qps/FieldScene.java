package qps;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public class FieldScene {

    private static FieldProgram program;
    private static VAO arrowVAO;

    private static Vec3 fieldLoc;
    private static Vec3 fieldSize;
    private static float fieldDensity;
    private static int xCount, yCount, zCount;

    public static boolean init() {
        program = new FieldProgram();
        program.init();

        fieldLoc = new Vec3();
        fieldSize = new Vec3(11.0f, 11.0f, 11.0f);
        fieldDensity = 1.0f;

        program.setFieldLoc(fieldLoc);
        program.setFieldSize(fieldSize);
        xCount = Math.round(fieldSize.x * fieldDensity);
        yCount = Math.round(fieldSize.y * fieldDensity);
        zCount = Math.round(fieldSize.z * fieldDensity);
        program.setFieldCount(xCount, yCount, zCount);

        arrowVAO = new VAO(MeshManager.arrowMesh, 0, null, GL_STATIC_DRAW);

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(arrowVAO.vao());

        UniformGlobals.TransformGlobals.setModelMat(new Mat4());
        UniformGlobals.TransformGlobals.setNormMat(new Mat4());
        UniformGlobals.buffer();
        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0, xCount * yCount * zCount);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
