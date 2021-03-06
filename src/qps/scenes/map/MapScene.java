package qps.scenes.map;

import qps.*;
import qps.scenes.sensor.SensorScene;
import qps.utils.*;

import java.util.Arrays;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * @since 5/28/2016
 */
public abstract class MapScene {

    private static final float CRITICAL_ANGLE = (float)Math.toRadians(82.5);

    private static MapProgram program;
    private static VAO planeVAO;

    private static Vec3[] planeDirs;
    private static Mat4[] planeMats;
    private static float[] planeDistances;
    private static Integer[] indices;

    public static boolean init() {
        program = new MapProgram();
        program.init();

        planeVAO = new VAO(MeshManager.doubleSquareMesh, 0, null, null, null, null, GL_STREAM_DRAW);

        planeDirs = new Vec3[]{
            new Vec3(1.0f, 1.0f, 0.0f),
            new Vec3(-1.0f, 1.0f, 0.0f),
            new Vec3(-1.0f, -1.0f, 0.0f),
            new Vec3(1.0f, -1.0f, 0.0f),
            new Vec3(0.0f, 1.0f, 1.0f),
            new Vec3(0.0f, -1.0f, 1.0f),
            new Vec3(0.0f, -1.0f, -1.0f),
            new Vec3(0.0f, 1.0f, -1.0f),
            new Vec3(1.0f, 0.0f, 1.0f),
            new Vec3(1.0f, 0.0f, -1.0f),
            new Vec3(-1.0f, 0.0f, -1.0f),
            new Vec3(-1.0f, 0.0f, 1.0f),
        };

        Mat4 scaleMat = new Mat4(Mat3.scale(1000.0f));
        Mat4 rotXMat = new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSX));
        Mat4 rotYMat = new Mat4(Mat3.rotate((float)Math.PI / 2.0f, Vec3.POSY));

        planeMats = new Mat4[]{
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[0]))),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[1]))),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[2]))),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[3]))),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[4])).mult(rotYMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[5])).mult(rotYMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[6])).mult(rotYMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[7])).mult(rotYMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[8])).mult(rotXMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[9])).mult(rotXMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[10])).mult(rotXMat)),
            new Mat4(scaleMat.mult(Mat4.translate(planeDirs[11])).mult(rotXMat))
        };

        planeDistances = new float[12];
        indices = new Integer[12];

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
        Vec3 sensorLoc = SensorScene.getSensorLoc();
        Mat4 sensorMat = SensorScene.getSensorMat();
        for (int i = 0; i < 12; ++i) {
            planeDistances[i] = camLoc.sub(sensorLoc.add(new Vec3(sensorMat.mult(new Vec4(planeDirs[i]))))).mag2();
        }

        for (int i = 0; i < indices.length; ++i) {
            indices[i] = i;
        }

        Arrays.sort(indices, new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return -(int)Math.signum(planeDistances[o1] - planeDistances[o2]);
            }
        });

        Vec3 camForward = Main.getCamera().forward();

        boolean[] renderPlane = new boolean[]{
            Math.acos(Math.abs(camForward.dot((new Vec3(sensorMat.mult(new Vec4(Vec3.POSZ)))).norm()))) <= CRITICAL_ANGLE,
            Math.acos(Math.abs(camForward.dot((new Vec3(sensorMat.mult(new Vec4(Vec3.POSX)))).norm()))) <= CRITICAL_ANGLE,
            Math.acos(Math.abs(camForward.dot((new Vec3(sensorMat.mult(new Vec4(Vec3.POSY)))).norm()))) <= CRITICAL_ANGLE
        };

        for (Integer i : indices) {
            if (renderPlane[i / 4]) {
                UniformGlobals.ModelGlobals.setModelMat(new Mat4(sensorMat.mult(planeMats[i])));
                UniformGlobals.ModelGlobals.buffer();
                glDrawElements(GL_TRIANGLES, MeshManager.doubleSquareMesh.nIndices(), GL_UNSIGNED_INT, 0);
            }
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public static boolean cleanup() {
        return true;
    }

}
