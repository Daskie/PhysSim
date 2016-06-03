package qps.scenes.grid;

import qps.*;
import qps.charges.ChargedLine;
import qps.charges.ChargedObject;
import qps.charges.ChargedPlane;
import qps.scenes.main.MainScene;
import qps.scenes.sensor.SensorScene;
import qps.utils.*;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @since 6/1/2016
 */
public class GridReticleScene {

    private static final Vec4 RETICLE_COLOR = new Vec4(0.0f, 1.0f, 0.0f, 1.0f);

    private static GridReticleProgram program;
    private static VAO cubeVAO;
    private static Mat4[] modelMats;

    public static boolean init() {
        program = new GridReticleProgram();
        program.init();

        cubeVAO = new VAO(MeshManager.cubeMesh, 9, null, null, null, null, GL_STREAM_DRAW);
        modelMats = new Mat4[9];

        program.setColor(RETICLE_COLOR);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize grid reticle scene!");
            return false;
        }

        return true;
    }

    public static void update(int t, int dt) {

    }

    public static void draw() {
        glUseProgram(program.id());
        glBindVertexArray(cubeVAO.vao());


        Vec3 camLoc = Main.getCamera().loc();
        Vec3 target;

        ChargedObject charge = MainScene.getSelected();
        if (charge != null && !(charge instanceof ChargedPlane || charge instanceof ChargedLine)) {
            target = charge.getLoc();
        }
        else if (SensorScene.isSelected()) {
            target = SensorScene.getSensorLoc();
        }
        else {
            return;
        }

        float d1 = camLoc.sub(new Vec3(target.x, 0.0f, 0.0f)).mag();
        float d2 = camLoc.sub(new Vec3(0.0f, target.y, 0.0f)).mag();
        float d3 = camLoc.sub(new Vec3(0.0f, 0.0f, target.z)).mag();

        float thickness = Utils.max(0.00275f * Utils.min(d1, d2, d3), 0.006f);

        Mat4 xScale = new Mat4(Mat3.scale(new Vec3(Math.abs(target.x) / 2.0f, thickness, thickness)));
        Mat4 yScale = new Mat4(Mat3.scale(new Vec3(thickness, Math.abs(target.y) / 2.0f, thickness)));
        Mat4 zScale = new Mat4(Mat3.scale(new Vec3(thickness, thickness, Math.abs(target.z) / 2.0f)));

        modelMats[0] = new Mat4(Mat4.translate(new Vec3(target.x / 2.0f, target.y, target.z)).mult(xScale));
        modelMats[1] = new Mat4(Mat4.translate(new Vec3(target.x, target.y / 2.0f, target.z)).mult(yScale));
        modelMats[2] = new Mat4(Mat4.translate(new Vec3(target.x, target.y, target.z / 2.0f)).mult(zScale));

        modelMats[3] = new Mat4(Mat4.translate(new Vec3(target.x / 2.0f, target.y, 0)).mult(xScale));
        modelMats[4] = new Mat4(Mat4.translate(new Vec3(target.x, target.y / 2.0f, 0)).mult(yScale));

        modelMats[5] = new Mat4(Mat4.translate(new Vec3(0, target.y / 2.0f, target.z)).mult(yScale));
        modelMats[6] = new Mat4(Mat4.translate(new Vec3(0, target.y, target.z / 2.0f)).mult(zScale));

        modelMats[7] = new Mat4(Mat4.translate(new Vec3(target.x, 0, target.z / 2.0f)).mult(zScale));
        modelMats[8] = new Mat4(Mat4.translate(new Vec3(target.x / 2.0f, 0, target.z)).mult(xScale));

        cubeVAO.bufferInstanceModelMats(0, 9, modelMats);

        glDrawElementsInstanced(GL_TRIANGLES, MeshManager.cubeMesh.nIndices(), GL_UNSIGNED_INT, 0, 9);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        return true;
    }
}
