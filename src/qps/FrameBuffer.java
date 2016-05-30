package qps;

import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

/**
 * @since 5/29/2016
 */
public class FrameBuffer {

    public static FrameBuffer creatMain(int width, int height, Vec4 clearColor) {
        AttachmentFormat af = new AttachmentFormat(
                1,
                new EssentialDataType[]{ EssentialDataType.UINT },
                new boolean[]{ true },
                new int[]{ GL_RGBA8 },
                new int[]{ GL_RGBA },
                new int[]{ GL_UNSIGNED_BYTE },
                true,
                false
        );

        TextureFormat[] ctfs = new TextureFormat[]{
                new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE)
        };

        TextureFormat dstf = new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE);

        ByteBuffer cb = createByteBuffer(16);
        cb.putInt(Math.round(clearColor.x * 255)).putInt(Math.round(clearColor.y * 255)).putInt(Math.round(clearColor.z * 255)).putInt(Math.round(clearColor.w * 255));
        cb.flip();
        ClearFormat cf = new ClearFormat(new ByteBuffer[]{ cb }, 1.0f, 0);

        return new FrameBuffer(width, height, af, ctfs, dstf, cf);
    }

    public static FrameBuffer createMainIdentity(int width, int height, Vec4 clearColor, int clearIdentity) {
        AttachmentFormat af = new AttachmentFormat(
                2,
                new EssentialDataType[]{ EssentialDataType.UINT, EssentialDataType.UINT },
                new boolean[]{ true, true },
                new int[]{ GL_RGBA8, GL_R32UI },
                new int[]{ GL_RGBA, GL_RED_INTEGER },
                new int[]{ GL_UNSIGNED_BYTE, GL_UNSIGNED_INT },
                true,
                false
        );

        TextureFormat[] ctfs = new TextureFormat[]{
                new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE),
                new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE),
        };

        TextureFormat dstf = new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE);

        ByteBuffer cb0 = createByteBuffer(16);
        cb0.putFloat(clearColor.x).putFloat(clearColor.y).putFloat(clearColor.z).putFloat(clearColor.w);
        cb0.flip();
        ByteBuffer cb1 = createByteBuffer(16);
        cb1.putInt(clearIdentity).putInt(clearIdentity).putInt(clearIdentity).putInt(clearIdentity);
        cb1.flip();
        ClearFormat cf = new ClearFormat(new ByteBuffer[]{ cb0, cb1 }, 1.0f, 0);

        return new FrameBuffer(width, height, af, ctfs, dstf, cf);
    }

    public static class AttachmentFormat {
        public int nColorAttachments;
        public EssentialDataType[] colorEssentialDataTypes;
        public boolean[] colorBuffersReadable;
        public int[] colorBufferInternalFormats;
        public int[] colorBufferFormats;
        public int[] colorBufferTypes;

        public boolean hasDepthStencilAttachment;
        public boolean depthStencilReadable;

        public AttachmentFormat(
                int nColorAttachments,
                EssentialDataType[] colorEssentialDataTypes,
                boolean[] colorBuffersReadable,
                int[] colorBufferInternalFormats,
                int[] colorBufferFormats,
                int[] colorBufferTypes,

                boolean hasDepthStencilAttachment,
                boolean depthStencilReadable
        ) {
            this.nColorAttachments = nColorAttachments;
            this.colorEssentialDataTypes = new EssentialDataType[nColorAttachments];
            System.arraycopy(colorEssentialDataTypes, 0, this.colorEssentialDataTypes, 0, nColorAttachments);
            this.colorBuffersReadable = new boolean[nColorAttachments];
            System.arraycopy(colorBuffersReadable, 0, this.colorBuffersReadable, 0, nColorAttachments);
            this.colorBufferInternalFormats = new int[nColorAttachments];
            System.arraycopy(colorBufferInternalFormats, 0, this.colorBufferInternalFormats, 0, nColorAttachments);
            this.colorBufferFormats = new int[nColorAttachments];
            System.arraycopy(colorBufferFormats, 0, this.colorBufferFormats, 0, nColorAttachments);
            this.colorBufferTypes = new int[nColorAttachments];
            System.arraycopy(colorBufferTypes, 0, this.colorBufferTypes, 0, nColorAttachments);

            this.hasDepthStencilAttachment = hasDepthStencilAttachment;
            this.depthStencilReadable = depthStencilReadable;
        }
    }

    public static class TextureFormat {
        public boolean mipmapped;
        public int minFilter;
        public int magFilter;
        public int wrapMode;

        public TextureFormat(boolean mipmapped, int minFilter, int magFilter, int wrapMode) {
            this.mipmapped = mipmapped;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.wrapMode = wrapMode;
        }
    }

    public static class ClearFormat {
        public ByteBuffer[] colorValues;
        public float depthValue;
        public int stencilValue;

        public ClearFormat(ByteBuffer[] colorValues, float depthValue, int stencilValue) {
            this.colorValues = new ByteBuffer[colorValues.length];
            System.arraycopy(colorValues, 0, this.colorValues, 0, colorValues.length);
            this.depthValue = depthValue;
            this.stencilValue = stencilValue;
        }
    }

    private enum EssentialDataType { FLOAT, INT, UINT }

    private int fboID;
    private int[] colorBufferIDs;
    private int depthStencilBufferID;

    private int width, height;
    private AttachmentFormat attachmentFormat;
    private TextureFormat[] colorTextureFormats;
    private TextureFormat depthStencilTextureFormat;
    private ClearFormat clearFormat;

    public FrameBuffer(int width, int height, AttachmentFormat attachmentFormat, TextureFormat[] colorTextureFormats, TextureFormat depthStencilTextureFormat, ClearFormat clearFormat) {
        this.width = width;
        this.height = height;
        this.attachmentFormat = attachmentFormat;
        this.colorTextureFormats = new TextureFormat[attachmentFormat.nColorAttachments];
        System.arraycopy(colorTextureFormats, 0, this.colorTextureFormats, 0, attachmentFormat.nColorAttachments);
        this.depthStencilTextureFormat = depthStencilTextureFormat;
        this.clearFormat = clearFormat;

        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        colorBufferIDs = new int[attachmentFormat.nColorAttachments];

        for (int i = 0; i < attachmentFormat.nColorAttachments; ++i) {
            if (attachmentFormat.colorBuffersReadable[i]) {
                colorBufferIDs[i] = glGenTextures();

                glBindTexture(GL_TEXTURE_2D, colorBufferIDs[i]);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, colorTextureFormats[i].minFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, colorTextureFormats[i].magFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, colorTextureFormats[i].wrapMode);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, colorTextureFormats[i].wrapMode);
                glTexImage2D(GL_TEXTURE_2D, 0, attachmentFormat.colorBufferInternalFormats[i], width, height, 0, attachmentFormat.colorBufferFormats[i], attachmentFormat.colorBufferTypes[i], (ByteBuffer) null);
                if (colorTextureFormats[i].mipmapped) glGenerateMipmap(colorBufferIDs[i]);
                glBindTexture(GL_TEXTURE_2D, 0);

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, colorBufferIDs[i], 0);

            }
            else {
                colorBufferIDs[i] = glGenRenderbuffers();

                glBindRenderbuffer(GL_RENDERBUFFER, colorBufferIDs[i]);
                glRenderbufferStorage(GL_RENDERBUFFER, attachmentFormat.colorBufferInternalFormats[i], width, height);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);

                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_RENDERBUFFER, colorBufferIDs[i]);
            }
        }
        if (attachmentFormat.hasDepthStencilAttachment) {
            if (attachmentFormat.depthStencilReadable) {
                depthStencilBufferID = glGenTextures();

                glBindTexture(GL_TEXTURE_2D, depthStencilBufferID);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, depthStencilTextureFormat.minFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, depthStencilTextureFormat.magFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, depthStencilTextureFormat.wrapMode);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, depthStencilTextureFormat.wrapMode);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer)null);
                if (depthStencilTextureFormat.mipmapped) glGenerateMipmap(depthStencilBufferID);
                glBindTexture(GL_TEXTURE_2D, 0);

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilBufferID, 0);

            }
            else {
                depthStencilBufferID = glGenRenderbuffers();

                glBindRenderbuffer(GL_RENDERBUFFER, depthStencilBufferID);
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);

                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthStencilBufferID);
            }
        }

        int fbStatus;
        if ((fbStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Failed to create framebuffer! Error: " + fbStatus);
        }
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to create framebuffer!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    //assumes the framebuffer is currently bound
    public void clear() {
        for (int i = 0; i < attachmentFormat.nColorAttachments; ++i) {
            glDrawBuffer(GL_COLOR_ATTACHMENT0 + i);
            switch (attachmentFormat.colorEssentialDataTypes[i]) {
                case FLOAT: glClearBufferfv(GL_COLOR, 0, clearFormat.colorValues[i]); break;
                case INT: glClearBufferiv(GL_COLOR, 0, clearFormat.colorValues[i]); break;
                case UINT: glClearBufferuiv(GL_COLOR, 0, clearFormat.colorValues[i]); break;
            }
        }
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glClearBufferfi(GL_DEPTH_STENCIL, 0, clearFormat.depthValue, clearFormat.stencilValue);
    }

    int id() {
        return fboID;
    }

    int colorBuffer(int i) {
        return colorBufferIDs[i];
    }

    int depthStencilBuffer() {
        return depthStencilBufferID;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    AttachmentFormat attachmentFormat() {
        return attachmentFormat;
    }

}
