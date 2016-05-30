package qps;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

/**
 * @since 5/29/2016
 */
public class FrameBuffer {

    public enum Profile {Tne_R}

    public static FrameBuffer createFrameBuffer(int width, int height, Profile profile) {
        AttachmentFormat af = null;
        TextureFormat[] ctfs = null;
        TextureFormat dstf = null;

        switch (profile) {
            case Tne_R: {
                af = new AttachmentFormat(
                        1,
                        new boolean[]{ true },
                        new int[]{ GL_RGBA8 },
                        new int[]{ GL_RGBA },
                        new int[]{ GL_UNSIGNED_BYTE },
                        true,
                        false
                );
                ctfs = new TextureFormat[]{
                        new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE)
                };
                dstf = new TextureFormat(false, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE);
            }
        }

        return new FrameBuffer(width, height, af, ctfs, dstf);
    }

    public static class AttachmentFormat {
        public int nColAttachments;
        public boolean[] colBuffersReadable;
        public int[] colBufferInternalFormats;
        public int[] colBufferFormats;
        public int[] colBufferTypes;

        public boolean hasDepthStencilAttachment;
        public boolean depthStencilReadable;

        public AttachmentFormat(
                int nColAttachments,
                boolean[] colBuffersReadable,
                int[] colBufferInternalFormats,
                int[] colBufferFormats,
                int[] colBufferTypes,

                boolean hasDepthStencilAttachment,
                boolean depthStencilReadable
        ) {
            this.nColAttachments = nColAttachments;
            this.colBuffersReadable = new boolean[nColAttachments];
            System.arraycopy(colBuffersReadable, 0, this.colBuffersReadable, 0, nColAttachments );
            this.colBufferInternalFormats = new int[nColAttachments];
            System.arraycopy(colBufferInternalFormats, 0, this.colBufferInternalFormats, 0, nColAttachments);
            this.colBufferFormats = new int[nColAttachments];
            System.arraycopy(colBufferFormats, 0, this.colBufferFormats, 0, nColAttachments);
            this.colBufferTypes = new int[nColAttachments];
            System.arraycopy(colBufferTypes, 0, this.colBufferTypes, 0, nColAttachments);

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

    private int fboID;
    private int[] colorBufferIDs;
    private int depthStencilBufferID;

    private int width, height;
    private AttachmentFormat attachmentFormat;
    private TextureFormat[] colorTextureFormats;
    private TextureFormat depthStencilTextureFormat;

    public FrameBuffer(int width, int height, AttachmentFormat attachmentFormat, TextureFormat[] colorTextureFormats, TextureFormat depthStencilTextureFormat) {
        this.width = width;
        this.height = height;
        this.attachmentFormat = attachmentFormat;
        this.colorTextureFormats = new TextureFormat[attachmentFormat.nColAttachments];
        System.arraycopy(colorTextureFormats, 0, this.colorTextureFormats, 0, attachmentFormat.nColAttachments);
        this.depthStencilTextureFormat = depthStencilTextureFormat;

        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        colorBufferIDs = new int[attachmentFormat.nColAttachments];

        for (int i = 0; i < attachmentFormat.nColAttachments; ++i) {
            if (attachmentFormat.colBuffersReadable[i]) {
                colorBufferIDs[i] = glGenTextures();

                glBindTexture(GL_TEXTURE_2D, colorBufferIDs[i]);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, colorTextureFormats[i].minFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, colorTextureFormats[i].magFilter);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, colorTextureFormats[i].wrapMode);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, colorTextureFormats[i].wrapMode);
                glTexImage2D(GL_TEXTURE_2D, 0, attachmentFormat.colBufferInternalFormats[i], width, height, 0, attachmentFormat.colBufferFormats[i], attachmentFormat.colBufferTypes[i], (ByteBuffer) null);
                if (colorTextureFormats[i].mipmapped) glGenerateMipmap(colorBufferIDs[i]);
                glBindTexture(GL_TEXTURE_2D, 0);

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, colorBufferIDs[i], 0);

            }
            else {
                colorBufferIDs[i] = glGenRenderbuffers();

                glBindRenderbuffer(GL_RENDERBUFFER, colorBufferIDs[i]);
                glRenderbufferStorage(GL_RENDERBUFFER, attachmentFormat.colBufferInternalFormats[i], width, height);
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
