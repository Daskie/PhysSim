package qps;

import qps.main.MainScene;

import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferRange;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;

/**
 * @since 5/28/2016
 */
public abstract class UniformGlobals {

    private static int bindings = 0;
    private static int nextBinding() {
        return bindings++;
    }

    public static class CameraGlobals {
        private static Mat4 viewMat;     //64
        private static Mat4 projMat;     //128

        public static final int SIZE = 128;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            cameraGroup.data.clear();
            viewMat.buffer(cameraGroup.data);
            projMat.buffer(cameraGroup.data);
            cameraGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, cameraGroup.offset, SIZE, cameraGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setViewMat(Mat4 viewMat) { CameraGlobals.viewMat = viewMat; needsBuffered = true; }
        public static void setProjMat(Mat4 projMat) { CameraGlobals.projMat = projMat; needsBuffered = true; }
    }

    public static class ModelGlobals {
        private static Mat4 modelMat;    //64
        private static Mat4 normMat;     //128

        public static final int SIZE = 128;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            modelGroup.data.clear();
            modelMat.buffer(modelGroup.data);
            normMat.buffer(modelGroup.data);
            modelGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, modelGroup.offset, SIZE, modelGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setModelMat(Mat4 modelMat) { ModelGlobals.modelMat = modelMat; needsBuffered = true; }
        public static void setNormMat(Mat4 normMat) { ModelGlobals.normMat = normMat; needsBuffered = true; }
    }

    public static class ViewGlobals {
        private static Vec3 camLoc;      //12
        private static float nearFrust;  //16
        private static Vec3 camForward;  //28
        private static float farFrust;   //32
        private static Vec3 camUp;       //44
        private static float fov;        //48

        public static final int SIZE = 48;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            viewGroup.data.clear();
            camLoc.buffer(viewGroup.data);
            viewGroup.data.putFloat(nearFrust);
            camForward.buffer(viewGroup.data);
            viewGroup.data.putFloat(farFrust);
            camUp.buffer(viewGroup.data);
            viewGroup.data.putFloat(fov);
            viewGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, viewGroup.offset, SIZE, viewGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setCamLoc(Vec3 camLoc) { ViewGlobals.camLoc = camLoc; needsBuffered = true; }
        public static void setNearFrust(float nearFrust) { ViewGlobals.nearFrust = nearFrust; needsBuffered = true; }
        public static void setCamForward(Vec3 camForward) { ViewGlobals.camForward = camForward; needsBuffered = true; }
        public static void setFarFrust(float farFrust) { ViewGlobals.farFrust = farFrust; needsBuffered = true; }
        public static void setCamUp(Vec3 camUp) { ViewGlobals.camUp = camUp; needsBuffered = true; }
        public static void setFov(float fov) { ViewGlobals.fov = fov; needsBuffered = true; }
    }

    public static class LightGlobals {
        private static Vec3 dir;                //12
        private static float strength;          //16
        private static Vec3 color;              //28
        private static float ambience;          //32
        private static float specularIntensity; //36
        private static float shininess;         //40

        public static final int SIZE = 40;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            lightGroup.data.clear();
            dir.buffer(lightGroup.data);
            lightGroup.data.putFloat(strength);
            color.buffer(lightGroup.data);
            lightGroup.data.putFloat(ambience);
            lightGroup.data.putFloat(specularIntensity);
            lightGroup.data.putFloat(shininess);
            lightGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, lightGroup.offset, SIZE, lightGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setDir(Vec3 dir) { LightGlobals.dir = dir; needsBuffered = true; }
        public static void setStrength(float strength) { LightGlobals.strength = strength; needsBuffered = true; }
        public static void setColor(Vec3 color) { LightGlobals.color = color; needsBuffered = true; }
        public static void setAmbience(float ambience) { LightGlobals.ambience = ambience; needsBuffered = true; }
        public static void setSpecularIntensity(float specularIntensity) { LightGlobals.specularIntensity = specularIntensity; needsBuffered = true; }
        public static void setShininess(float shininess) { LightGlobals.shininess = shininess; needsBuffered = true; }
    }

    public static class ChargeCountsGlobals {
        private static int sphereCount;     //4
        private static int planeCount;      //8
        private static int lineCount;       //12

        public static final int SIZE = 12;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            chargeCountsGroup.data.clear();
            chargeCountsGroup.data.putInt(sphereCount);
            chargeCountsGroup.data.putInt(planeCount);
            chargeCountsGroup.data.putInt(lineCount);
            chargeCountsGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, chargeCountsGroup.offset, SIZE, chargeCountsGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setSphereCount(int sphereCount) { ChargeCountsGlobals.sphereCount = sphereCount; needsBuffered = true; }
        public static void setPlaneCount(int planeCount) { ChargeCountsGlobals.planeCount = planeCount; needsBuffered = true; }
        public static void setLineCount(int lineCount) { ChargeCountsGlobals.lineCount = lineCount; needsBuffered = true; }
    }

    public static class SphereChargesGlobals {
        //loc       12
        //charge    16
        public static final int SIZE = MainScene.MAX_OBJECTS * 16;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, sphereChargesGroup.offset, SIZE, sphereChargesGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void set(int i, Vec3 loc, float charge) {
            loc.buffer(sphereChargesGroup.data, i * 16);
            sphereChargesGroup.data.putFloat(i * 16 + 12, charge);

            needsBuffered = true;
        }
    }

    public static class PlaneChargesGlobals {
        //norm      12
        //charge    16
        public static final int SIZE = MainScene.MAX_OBJECTS * 16;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, planeChargesGroup.offset, SIZE, planeChargesGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void set(int i, Vec3 norm, float charge) {
            norm.buffer(planeChargesGroup.data, i * 16);
            planeChargesGroup.data.putFloat(i * 16 + 12, charge);

            needsBuffered = true;
        }
    }

    public static class LineChargesGlobals {
        //loc       12
        //charge    16
        //dir       28
        //filler0   32
        public static final int SIZE = MainScene.MAX_OBJECTS * 32;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, lineChargesGroup.offset, SIZE, lineChargesGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void set(int i, Vec3 loc, float charge, Vec3 dir) {
            loc.buffer(lineChargesGroup.data, i * 16);
            lineChargesGroup.data.putFloat(i * 16 + 12, charge);
            dir.buffer(lineChargesGroup.data, i * 16 + 16);
            lineChargesGroup.data.putFloat(i * 16 + 28, 0.0f);

            needsBuffered = true;
        }
    }

    public static class EThresholdGlobals {
        private static float minMagE;      //4
        private static float maxMagE;      //4

        public static final int SIZE = 8;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            eThresholdGroup.data.clear();
            eThresholdGroup.data.putFloat(minMagE);
            eThresholdGroup.data.putFloat(maxMagE);
            eThresholdGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, eThresholdGroup.offset, SIZE, eThresholdGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setMinMagE(float minMagE) { EThresholdGlobals.minMagE = minMagE; needsBuffered = true; }
        public static void setMaxMagE(float maxMagE) { EThresholdGlobals.maxMagE = maxMagE; needsBuffered = true; }
    }

    public static class IDGlobals {
        private static int hovored;         //4
        private static int selected;        //8

        public static final int SIZE = 8;
        public static final int BINDING = nextBinding();

        static boolean needsBuffered;

        public static void buffer() {
            idGroup.data.clear();
            idGroup.data.putInt(hovored);
            idGroup.data.putInt(selected);
            idGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, idGroup.offset, SIZE, idGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setHoveredID(int hoveredID) { IDGlobals.hovored = hoveredID; needsBuffered = true; }
        public static void setSelectedID(int selectedID) { IDGlobals.selected = selectedID; needsBuffered = true; }
    }

    private static class UniformGroup {
        public int offset;
        public ByteBuffer data;

        public UniformGroup(int offset, int size) {
            this.offset = offset;
            data = createByteBuffer(size);
        }
    }

    private static UniformGroup cameraGroup, modelGroup, viewGroup, lightGroup, chargeCountsGroup, sphereChargesGroup, planeChargesGroup, lineChargesGroup, eThresholdGroup, idGroup;
    private static int ubo;

    public static boolean init()  {
        int alignSize = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);

        int offset = 0;
        cameraGroup = new UniformGroup(offset, CameraGlobals.SIZE);
        offset += (int)Math.ceil((float)CameraGlobals.SIZE / alignSize) * alignSize;
        modelGroup = new UniformGroup(offset, ModelGlobals.SIZE);
        offset += (int)Math.ceil((float)ModelGlobals.SIZE / alignSize) * alignSize;
        viewGroup = new UniformGroup(offset, ViewGlobals.SIZE);
        offset += (int)Math.ceil((float)ViewGlobals.SIZE / alignSize) * alignSize;
        lightGroup = new UniformGroup(offset, LightGlobals.SIZE);
        offset += (int)Math.ceil((float)LightGlobals.SIZE / alignSize) * alignSize;
        chargeCountsGroup = new UniformGroup(offset, ChargeCountsGlobals.SIZE);
        offset += (int)Math.ceil((float) ChargeCountsGlobals.SIZE / alignSize) * alignSize;
        eThresholdGroup = new UniformGroup(offset, EThresholdGlobals.SIZE);
        offset += (int)Math.ceil((float) EThresholdGlobals.SIZE / alignSize) * alignSize;
        sphereChargesGroup = new UniformGroup(offset, SphereChargesGlobals.SIZE);
        offset += (int)Math.ceil((float)SphereChargesGlobals.SIZE / alignSize) * alignSize;
        planeChargesGroup = new UniformGroup(offset, PlaneChargesGlobals.SIZE);
        offset += (int)Math.ceil((float)PlaneChargesGlobals.SIZE / alignSize) * alignSize;
        lineChargesGroup = new UniformGroup(offset, LineChargesGlobals.SIZE);
        offset += (int)Math.ceil((float)LineChargesGlobals.SIZE / alignSize) * alignSize;
        idGroup = new UniformGroup(offset, IDGlobals.SIZE);
        offset += (int)Math.ceil((float)IDGlobals.SIZE / alignSize) * alignSize;

        ubo = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);

        glBufferData(GL_UNIFORM_BUFFER, offset, GL_STREAM_DRAW);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize uniform buffer!");
            return false;
        }

        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        glBindBufferRange(GL_UNIFORM_BUFFER, CameraGlobals.BINDING, ubo, cameraGroup.offset, CameraGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, ModelGlobals.BINDING, ubo, modelGroup.offset, ModelGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, ViewGlobals.BINDING, ubo, viewGroup.offset, ViewGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, LightGlobals.BINDING, ubo, lightGroup.offset, LightGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, ChargeCountsGlobals.BINDING, ubo, chargeCountsGroup.offset, ChargeCountsGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, SphereChargesGlobals.BINDING, ubo, sphereChargesGroup.offset, SphereChargesGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, PlaneChargesGlobals.BINDING, ubo, planeChargesGroup.offset, PlaneChargesGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, LineChargesGlobals.BINDING, ubo, lineChargesGroup.offset, LineChargesGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, EThresholdGlobals.BINDING, ubo, eThresholdGroup.offset, EThresholdGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, IDGlobals.BINDING, ubo, idGroup.offset, IDGlobals.SIZE);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize uniform buffer ranges!");
            return false;
        }

        return true;
    }

    static void buffer() {
        if (CameraGlobals.needsBuffered) CameraGlobals.buffer();
        if (ModelGlobals.needsBuffered) ModelGlobals.buffer();
        if (ViewGlobals.needsBuffered) ViewGlobals.buffer();
        if (LightGlobals.needsBuffered) LightGlobals.buffer();
        if (ChargeCountsGlobals.needsBuffered) ChargeCountsGlobals.buffer();
        if (SphereChargesGlobals.needsBuffered) SphereChargesGlobals.buffer();
        if (PlaneChargesGlobals.needsBuffered) PlaneChargesGlobals.buffer();
        if (LineChargesGlobals.needsBuffered) LineChargesGlobals.buffer();
        if (EThresholdGlobals.needsBuffered) EThresholdGlobals.buffer();
        if (IDGlobals.needsBuffered) IDGlobals.buffer();
    }

    static void bufferForce() {
        CameraGlobals.buffer();
        ModelGlobals.buffer();
        ViewGlobals.buffer();
        LightGlobals.buffer();
        ChargeCountsGlobals.buffer();
        SphereChargesGlobals.buffer();
        PlaneChargesGlobals.buffer();
        LineChargesGlobals.buffer();
        EThresholdGlobals.buffer();
        IDGlobals.buffer();
    }

}
