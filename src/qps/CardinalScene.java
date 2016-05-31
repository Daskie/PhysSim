package qps;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_COPY;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class CardinalScene {

    private static CardinalProgram program;
    private static VAO arrowVAO;
    private static VAO coneVAO;

    private static Mat4 pxArrowModalMat;
    private static Mat4 nxArrowModalMat;
    private static Mat4 pyArrowModalMat;
    private static Mat4 nyArrowModalMat;
    private static Mat4 pzArrowModalMat;
    private static Mat4 nzArrowModalMat;

    private static Mat4 pxArrowNormMat;
    private static Mat4 nxArrowNormMat;
    private static Mat4 pyArrowNormMat;
    private static Mat4 nyArrowNormMat;
    private static Mat4 pzArrowNormMat;
    private static Mat4 nzArrowNormMat;

    public static boolean init() {
        program = new CardinalProgram();
        program.init();

        arrowVAO = new VAO(MeshManager.arrowMesh, 0, null, null, null, GL_STATIC_COPY);
        coneVAO = new VAO(MeshManager.coneMesh, 0, null, null, null, GL_STATIC_COPY);

        float innerR = 0.5f;
        float sizeOnScreen = 10.0f;
        float onScreenRatio = 0.8f;

        pxArrowModalMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(Mat4.translate(Vec3.POSZ.mult(innerR))));
        nxArrowModalMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(Mat4.translate(Vec3.POSZ.mult(innerR))));
        pyArrowModalMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(Mat4.translate(Vec3.POSZ.mult(innerR))));
        nyArrowModalMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(Mat4.translate(Vec3.POSZ.mult(innerR))));
        pzArrowModalMat = Mat4.translate(Vec3.POSZ.mult(innerR));
        nzArrowModalMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(Mat4.translate(Vec3.POSZ.mult(innerR))));

        pxArrowNormMat = new Mat4(pxArrowModalMat.inv().trans());
        nxArrowNormMat = new Mat4(nxArrowModalMat.inv().trans());
        pyArrowNormMat = new Mat4(pyArrowModalMat.inv().trans());
        nyArrowNormMat = new Mat4(nyArrowModalMat.inv().trans());
        pzArrowNormMat = new Mat4(pzArrowModalMat.inv().trans());
        nzArrowNormMat = new Mat4(nzArrowModalMat.inv().trans());

        program.setCamLoc(new Vec3(0.0f, 0.0f, 10.0f));
        program.setLightDir(new Vec3(1.0f, -1.0f, -1.0f));
        program.setScreenPos(new Vec2(0.75f, -0.75f));

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(arrowVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(pxArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(pxArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(nxArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pyArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(pyArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nyArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(nyArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(pzArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzArrowModalMat);
        UniformGlobals.ModelGlobals.setNormMat(nzArrowNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
