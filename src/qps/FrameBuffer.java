package qps;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.BufferUtils.createIntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

/**
 * @since 5/29/2016
 */
public class FrameBuffer {

    private int fboID;
    private int[] colorBufferIDs;
    private int depthStencilBufferID;

    private int width, height;
    private int nColBuffers;
    private boolean hasDepthStencil;
    private boolean readableColor;
    private boolean readableDepthStencil;
    private boolean floatFormat;
    private boolean linearFiltering;
    private boolean mipmapping;
    private int nLevels;

    public FrameBuffer(int width, int height, int nColBuffers, boolean hasDepthStencil, boolean readableColor, boolean readableDepthStencil, boolean floatFormat, boolean linearFiltering, boolean mipmapping) {
        this.width = width;
        this.height = height;
        this.nColBuffers = nColBuffers;
        this.hasDepthStencil = hasDepthStencil;
        this.readableColor = readableColor;
        this.readableDepthStencil = readableDepthStencil;
        this.floatFormat = floatFormat;
        this.linearFiltering = linearFiltering;
        this.mipmapping = mipmapping;
        this.nLevels = mipmapping ? Utils.log2(width > height ? width : height) + 1 : 1;

        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        colorBufferIDs = new int[nColBuffers];

        if (readableColor) {
            IntBuffer colBufferIDsBuffer = createIntBuffer(nColBuffers);
            glGenTextures(colBufferIDsBuffer);
            colBufferIDsBuffer.get(colorBufferIDs);

            for (int i = 0; i < nColBuffers; ++i) {
                glBindTexture(GL_TEXTURE_2D, colorBufferIDs[i]);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearFiltering ? (mipmapping ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR) : (mipmapping ? GL_NEAREST_MIPMAP_NEAREST : GL_NEAREST));
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexImage2D(GL_TEXTURE_2D, 0, floatFormat ? GL_RGBA16F : GL_RGBA8, width, height, 0, GL_RGBA, floatFormat ? GL_FLOAT : GL_UNSIGNED_BYTE, (ByteBuffer)null);
                if (mipmapping) {
                    glGenerateMipmap(GL_TEXTURE_2D);
                }

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, colorBufferIDs[i], 0);
            }

            glBindTexture(GL_TEXTURE_2D, 0);
        }
        else {
            IntBuffer colBufferIDsBuffer = createIntBuffer(nColBuffers);
            glGenRenderbuffers(colBufferIDsBuffer);
            colBufferIDsBuffer.get(colorBufferIDs);

            for (int i = 0; i < nColBuffers; ++i) {
                glBindRenderbuffer(GL_RENDERBUFFER, colorBufferIDs[i]);
                glRenderbufferStorage(GL_RENDERBUFFER, floatFormat ? GL_RGBA16F : GL_RGBA8, width, height);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);

                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_RENDERBUFFER, colorBufferIDs[i]);
            }

            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }
        if (hasDepthStencil) {
            if (readableDepthStencil) {
                depthStencilBufferID = glGenTextures();

                glBindTexture(GL_TEXTURE_2D, depthStencilBufferID);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearFiltering ? GL_LINEAR : GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0,	GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer)null);

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilBufferID, 0);

                glBindTexture(GL_TEXTURE_2D, 0);
            }
            else {
                depthStencilBufferID = glGenRenderbuffers();

                glBindRenderbuffer(GL_RENDERBUFFER, depthStencilBufferID);
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);

                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthStencilBufferID);

                glBindRenderbuffer(GL_RENDERBUFFER, 0);
            }
        }

        int fbStatus;
        if ((fbStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Failed to create framebuffer! Error: " + fbStatus);
        }
        Utils.checkGLErr();

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

    int nLevels() {
        return nLevels;
    }

    int nColBuffers() {
        return nColBuffers;
    }
}
