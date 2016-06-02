package qps.map;

import qps.*;
import qps.sensor.SensorScene;

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

    private static final float CRITICAL_ANGLE = (float)Math.toRadians(75);

    private static MapProgram program;
    private static VAO planeVAO;

    private static Vec3[] planeDirs;
    private static Mat4[] planeMats;
    private static float[] planeDistances;
    private static Integer[] indices;

    public static boolean init() {
        program = new MapProgram();
        program.init();

        planeVAO = new VAO(MeshManager.squareMesh, 0, null, null, null, GL_STREAM_DRAW);

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
        for (int i = 0; i < 12; ++i) {
            planeDistances[i] = camLoc.sub(sensorLoc.add(planeDirs[i])).mag2();
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
            Math.acos(Math.abs(camForward.dot(Vec3.POSZ))) <= CRITICAL_ANGLE,
            Math.acos(Math.abs(camForward.dot(Vec3.POSX))) <= CRITICAL_ANGLE,
            Math.acos(Math.abs(camForward.dot(Vec3.POSY))) <= CRITICAL_ANGLE
        };

        glDisable(GL_CULL_FACE);

        Mat4 sensorMat = SensorScene.getSensorMat();
        for (Integer i : indices) {
            if (renderPlane[i / 4]) {
                UniformGlobals.ModelGlobals.setModelMat(new Mat4(sensorMat.mult(planeMats[i])));
                UniformGlobals.ModelGlobals.buffer();
                glDrawElements(GL_TRIANGLES, MeshManager.squareMesh.nIndices(), GL_UNSIGNED_INT, 0);
            }
        }

        glEnable(GL_CULL_FACE);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}
