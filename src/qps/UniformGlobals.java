package qps;

import javafx.scene.effect.Light;

import javax.swing.text.View;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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

    public static class ViewGlobals {
        public static Vec3 camLoc;      //12
        public static float nearFrust;  //16
        public static Vec3 camForward;  //28
        public static float farFrust;   //32
        public static Vec3 camUp;       //44
        public static float fov;        //48

        public static final int SIZE = 48;
        public static final int BINDING = 1;

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
        }
    }

    public static class TransformGlobals {
        public static Mat4 modelMat;    //64
        public static Mat4 normMat;     //128
        public static Mat4 viewMat;     //192
        public static Mat4 projMat;     //256

        public static final int SIZE = 256;
        public static final int BINDING = 1;

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
        }
    }

    public static class LightGlobals {
        public static Vec3 dir;         //12
        public static float strength;   //16
        public static Vec3 color;       //28
        public static float ambience;   //32

        public static final int SIZE = 32;
        public static final int BINDING = 2;

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
        }
    }

    private static class UniformGroup {
        public int offset;
        public ByteBuffer data;

        public UniformGroup(int binding, int offset, int size) {
            this.offset = offset;
            data = createByteBuffer(size);
        }
    }

    private static UniformGroup viewGroup, transformGroup, lightGroup;
    private static int ubo;

    public static boolean init()  {
        int alignSize = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);

        int offset = 0;
        viewGroup = new UniformGroup(ViewGlobals.BINDING, offset, ViewGlobals.SIZE);
        offset += (int)Math.ceil((float)ViewGlobals.SIZE / alignSize) * alignSize;
        transformGroup = new UniformGroup(TransformGlobals.BINDING, offset, TransformGlobals.SIZE);
        offset += (int)Math.ceil((float)TransformGlobals.SIZE / alignSize) * alignSize;
        lightGroup = new UniformGroup(LightGlobals.BINDING, offset, LightGlobals.SIZE);
        offset += (int)Math.ceil((float)LightGlobals.SIZE / alignSize) * alignSize;

        ubo = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferData(GL_UNIFORM_BUFFER, offset, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize uniform buffer!");
            return false;
        }

        glBindBufferRange(GL_UNIFORM_BUFFER, ViewGlobals.BINDING, ubo, viewGroup.offset, ViewGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, TransformGlobals.BINDING, ubo, transformGroup.offset, TransformGlobals.SIZE);
        glBindBufferRange(GL_UNIFORM_BUFFER, LightGlobals.BINDING, ubo, lightGroup.offset, LightGlobals.SIZE);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize uniform buffer ranges!");
            return false;
        }

        return true;
    }

    static void buffer() {
        ViewGlobals.buffer();
        TransformGlobals.buffer();
        LightGlobals.buffer();
    }

}
