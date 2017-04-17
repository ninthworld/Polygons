package org.ninthworld.polygons.fbo;

import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class Fbo {

    public static final int NONE = 0;
    public static final int DEPTH_TEXTURE = 1;
    public static final int DEPTH_RENDER_BUFFER = 2;

    private final int width;
    private final int height;

    private int frameBuffer;

    private boolean multisample = false;

    private int colorTexture;
    private int depthTexture;

    private int depthBuffer;
    private int colorBuffer;

    public Fbo(int width, int height, int depthBufferType) {
        this.width = width;
        this.height = height;
        initialiseFrameBuffer(depthBufferType);
    }

    public Fbo(int width, int height) {
        this.width = width;
        this.height = height;
        this.multisample = true;
        initialiseFrameBuffer(DEPTH_RENDER_BUFFER);
    }

    public void cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer);
        GL11.glDeleteTextures(colorTexture);
        GL11.glDeleteTextures(depthTexture);
        GL30.glDeleteRenderbuffers(depthBuffer);
        GL30.glDeleteRenderbuffers(colorBuffer);
    }

    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, width, height);
    }

    public void unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }

    public void bindToRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    public int getColorTexture() {
        return colorTexture;
    }

    public int getDepthTexture() {
        return depthTexture;
    }

    public void resolveToFbo(Fbo outputFbo){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        this.unbindFrameBuffer();
    }

    public void resolveToScreen(){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        this.unbindFrameBuffer();
    }

    private void initialiseFrameBuffer(int type) {
        createFrameBuffer();
        if(multisample){
            createMultisampleColorAttachment();
        }else {
            createTextureAttachment();
        }

        if (type == DEPTH_RENDER_BUFFER) {
            createDepthBufferAttachment();
        } else if (type == DEPTH_TEXTURE) {
            createDepthTextureAttachment();
        }
        unbindFrameBuffer();
    }

    private void createFrameBuffer() {
        frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    private void createTextureAttachment() {
        colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
    }
    private void createDepthTextureAttachment() {
        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
    }

    private void createMultisampleColorAttachment() {
        colorBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colorBuffer);
    }

    private void createDepthBufferAttachment() {
        depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        if(!multisample) {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
        }else{
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, width, height);
        }
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
    }
}
