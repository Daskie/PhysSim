package qps;

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

    public static class TransformGlobals {
        private static Mat4 modelMat;    //64
        private static Mat4 normMat;     //128
        private static Mat4 viewMat;     //192
        private static Mat4 projMat;     //256

        public static final int SIZE = 256;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            transformGroup.data.clear();
            modelMat.buffer(transformGroup.data);
            normMat.buffer(transformGroup.data);
            viewMat.buffer(transformGroup.data);
            projMat.buffer(transformGroup.data);
            transformGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, transformGroup.offset, SIZE, transformGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setModelMat(Mat4 modelMat) { TransformGlobals.modelMat = modelMat; needsBuffered = true; }
        public static void setNormMat(Mat4 normMat) { TransformGlobals.normMat = normMat; needsBuffered = true; }
        public static void setViewMat(Mat4 viewMat) { TransformGlobals.viewMat = viewMat; needsBuffered = true; }
        public static void setProjMat(Mat4 projMat) { TransformGlobals.projMat = projMat; needsBuffered = true; }
    }

    public static class LightGlobals {
        private static Vec3 dir;         //12
        private static float strength;   //16
        private static Vec3 color;       //28
        private static float ambience;   //32

        public static final int SIZE = 32;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            lightGroup.data.clear();
            dir.buffer(lightGroup.data);
            lightGroup.data.putFloat(strength);
            color.buffer(lightGroup.data);
            lightGroup.data.putFloat(ambience);
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
    }

    public static class ChargeCountsGlobals {
        private static int sphereCount;     //4

        public static final int SIZE = 4;
        public static final int BINDING = nextBinding();

        private static boolean needsBuffered;

        public static void buffer() {
            chargeCountsGroup.data.clear();
            chargeCountsGroup.data.putInt(sphereCount);
            chargeCountsGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, chargeCountsGroup.offset, SIZE, chargeCountsGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setSphereCount(int sphereCount) { ChargeCountsGlobals.sphereCount = sphereCount; needsBuffered = true; }
    }

    public static class SphereChargesGlobals {
        public static final int SIZE = 128 * 16;
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

    public static class IDGlobals {
        private static int id;      //4

        public static final int SIZE = 4;
        public static final int BINDING = nextBinding();

        static boolean needsBuffered;

        public static void buffer() {
            idGroup.data.clear();
            idGroup.data.putInt(id);
            idGroup.data.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, idGroup.offset, SIZE, idGroup.data);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            needsBuffered = false;
        }

        public static void setID(int id) { IDGlobals.id = id; needsBuffered = true; }
    }

    private static class UniformGroup {
        public int offset;
        public ByteBuffer data;

        public UniformGroup(int offset, int size) {
            this.offset = offset;
            data = createByteBuffer(size);
        }
    }

    private static UniformGroup viewGroup, transformGroup, lightGroup, chargeCountsGroup, sphereChargesGroup, idGroup;
    private static int ubo;

    public static boolean init()  {
        int alignSize = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);

        int offset = 0;
        viewGroup = new UniformGroup(offset, ViewGlobals.SIZE);
        offset += (int)Math.ceil((float)ViewGlobals.SIZE / alignSize) * alignSize;
        transformGroup = new UniformGroup(offset, TransformGlobals.SIZE);
        offset += (int)Math.ceil((float)TransformGlobals.SIZE / alignSize) * alignSize;
        lightGroup = new UniformGroup(offset, LightGlobals.SIZE);
        offset += (int)Math.ceil((float)LightGlobals.SIZE / alignSize) * alignSize;
        chargeCountsGroup = new UniformGroup(offset, ChargeCountsGlobals.SIZE);
        offset += (int)Math.ceil((float) ChargeCountsGlobals.SIZE / alignSize) * alignSize;
        sphereChargesGroup = new UniformGroup(offset, SphereChargesGlobals.SIZE);
        offset += (int)Math.ceil((float)SphereChargesGlobals.SIZE / alignSize) * alignSize;
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

        glBindBufferRange(GL_UNIFORM_BUFFER, ViewGlobals.BINDING, ubo, viewGroup.offset, ViewGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, TransformGlobals.BINDING, ubo, transformGroup.offset, TransformGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, LightGlobals.BINDING, ubo, lightGroup.offset, LightGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, ChargeCountsGlobals.BINDING, ubo, chargeCountsGroup.offset, ChargeCountsGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, SphereChargesGlobals.BINDING, ubo, sphereChargesGroup.offset, SphereChargesGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, IDGlobals.BINDING, ubo, idGroup.offset, IDGlobals.SIZE);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize uniform buffer ranges!");
            return false;
        }

        return true;
    }

    static void buffer() {
        if (ViewGlobals.needsBuffered) ViewGlobals.buffer();
        if (TransformGlobals.needsBuffered) TransformGlobals.buffer();
        if (LightGlobals.needsBuffered) LightGlobals.buffer();
        if (ChargeCountsGlobals.needsBuffered) ChargeCountsGlobals.buffer();
        if (SphereChargesGlobals.needsBuffered) SphereChargesGlobals.buffer();
        if (IDGlobals.needsBuffered) IDGlobals.buffer();
    }

    static void bufferForce() {
        ViewGlobals.buffer();
        TransformGlobals.buffer();
        LightGlobals.buffer();
        ChargeCountsGlobals.buffer();
        SphereChargesGlobals.buffer();
        IDGlobals.buffer();
    }

}
