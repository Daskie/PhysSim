package qps;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_COPY;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 5/28/2016
 */
public abstract class CardinalScene {

    private static CardinalProgram program;
    private static VAO axesVAO;
    private static VAO coneVAO;

    private static Mat4 axesMat;
    private static Mat4 axesNormMat;

    private static Mat4 pxStepMat;
    private static Mat4 nxStepMat;
    private static Mat4 pyStepMat;
    private static Mat4 nyStepMat;
    private static Mat4 pzStepMat;
    private static Mat4 nzStepMat;
    private static Mat4 pxStepNormMat;
    private static Mat4 nxStepNormMat;
    private static Mat4 pyStepNormMat;
    private static Mat4 nyStepNormMat;
    private static Mat4 pzStepNormMat;
    private static Mat4 nzStepNormMat;

    private static Mat4 pxSlideMat;
    private static Mat4 nxSlideMat;
    private static Mat4 pySlideMat;
    private static Mat4 nySlideMat;
    private static Mat4 pzSlideMat;
    private static Mat4 nzSlideMat;
    private static Mat4 pxSlideNormMat;
    private static Mat4 nxSlideNormMat;
    private static Mat4 pySlideNormMat;
    private static Mat4 nySlideNormMat;
    private static Mat4 pzSlideNormMat;
    private static Mat4 nzSlideNormMat;

    public static boolean init() {
        program = new CardinalProgram();
        program.init();

        axesVAO = new VAO(MeshManager.axesMesh, 0, null, null, null, GL_STATIC_DRAW);
        coneVAO = new VAO(MeshManager.coneMesh, 0, null, null, null, GL_STATIC_DRAW);

        float innerR = 0.5f;

        axesMat = new Mat4();
        axesNormMat = new Mat4();

        Mat4 stepMat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.4f)).mult(new Mat4(Mat3.scale(0.3f))));
        pxStepMat = new Mat4((new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(stepMat)));
        nxStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(stepMat));
        pyStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(stepMat));
        nyStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(stepMat));
        pzStepMat = stepMat;
        nzStepMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(stepMat));
        pxStepNormMat = new Mat4(pxStepMat.inv().trans());
        nxStepNormMat = new Mat4(nxStepMat.inv().trans());
        pyStepNormMat = new Mat4(pyStepMat.inv().trans());
        nyStepNormMat = new Mat4(nyStepMat.inv().trans());
        pzStepNormMat = new Mat4(pzStepMat.inv().trans());
        nzStepNormMat = new Mat4(nzStepMat.inv().trans());

        Mat4 slideMat = new Mat4(Mat4.translate(new Vec3(0.0f, 0.0f, 0.65f)).mult(new Mat4(Mat3.scale(0.2f))));
        pxSlideMat = new Mat4((new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY)).mult(slideMat)));
        nxSlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGY)).mult(slideMat));
        pySlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.NEGX)).mult(slideMat));
        nySlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX)).mult(slideMat));
        pzSlideMat = slideMat;
        nzSlideMat = new Mat4(new Mat4(Mat3.rotate((float)Math.PI, Vec3.POSX)).mult(slideMat));
        pxSlideNormMat = new Mat4(pxSlideMat.inv().trans());
        nxSlideNormMat = new Mat4(nxSlideMat.inv().trans());
        pySlideNormMat = new Mat4(pySlideMat.inv().trans());
        nySlideNormMat = new Mat4(nySlideMat.inv().trans());
        pzSlideNormMat = new Mat4(pzSlideMat.inv().trans());
        nzSlideNormMat = new Mat4(nzSlideMat.inv().trans());

        program.setCamLoc(new Vec3(0.0f, 0.0f, 4.0f));
        program.setLightDir(new Vec3(1.0f, -1.0f, -1.0f));
        program.setScreenPos(new Vec2(0.75f, -0.75f));

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());

        glBindVertexArray(axesVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(axesMat);
        UniformGlobals.ModelGlobals.setNormMat(axesNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.axesMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(coneVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(pxStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pxStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nxStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pyStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pyStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nyStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nyStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzStepMat);
        UniformGlobals.ModelGlobals.setNormMat(pzStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzStepMat);
        UniformGlobals.ModelGlobals.setNormMat(nzStepNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(coneVAO.vao());

        UniformGlobals.ModelGlobals.setModelMat(pxSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pxSlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nxSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nxSlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pySlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pySlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nySlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nySlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(pzSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(pzSlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        UniformGlobals.ModelGlobals.setModelMat(nzSlideMat);
        UniformGlobals.ModelGlobals.setNormMat(nzSlideNormMat);
        UniformGlobals.ModelGlobals.buffer();
        glDrawElements(GL_TRIANGLES, MeshManager.arrowMesh.nIndices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
