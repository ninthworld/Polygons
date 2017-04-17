package org.ninthworld.polygons.fbo;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.ninthworld.polygons.camera.Camera;
import org.ninthworld.polygons.chunk.ChunkManager;
import org.ninthworld.polygons.engine.IManager;
import org.ninthworld.polygons.loader.Loader;
import org.ninthworld.polygons.model.ModelManager;
import org.ninthworld.polygons.model.RawModel;
import org.ninthworld.polygons.renderer.RendererManager;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class FboManager implements IManager {

    private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private RawModel quad;

    public Fbo multisampleFbo;
    public Fbo terrainFbo;
    public Fbo terrainNormalFbo;
    public Fbo fxFbo;

    public FboManager(){
        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        terrainFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        terrainNormalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        fxFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

    }

    @Override
    public void initialize() {
        quad = Loader.load(POSITIONS, 2);
    }

    public void render(RendererManager rendererManager, ChunkManager chunkManager, ModelManager modelManager, Camera camera){

        multisampleFbo.bindFrameBuffer();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            rendererManager.skyboxRenderer.render(modelManager, camera);

            if(camera.isWireframe)
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            else
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            rendererManager.chunkRenderer.render(chunkManager, modelManager, camera);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        multisampleFbo.unbindFrameBuffer();
        multisampleFbo.resolveToFbo(terrainFbo);

        multisampleFbo.bindFrameBuffer();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(0, 0, 0, 1);
            rendererManager.chunkRenderer.renderNormal(chunkManager, modelManager, camera);
        multisampleFbo.unbindFrameBuffer();
        multisampleFbo.resolveToFbo(terrainNormalFbo);

        fxFbo.bindFrameBuffer();
            rendererManager.shaderManager.fxShader.start();
            rendererManager.shaderManager.fxShader.connectTextures();
            rendererManager.shaderManager.fxShader.loadCameraPos(camera.getPosition());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainFbo.getColorTexture());

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainFbo.getDepthTexture());

            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainNormalFbo.getColorTexture());

            drawQuad();
            rendererManager.shaderManager.fxShader.stop();
        fxFbo.unbindFrameBuffer();
        fxFbo.resolveToScreen();
    }

    private void drawQuad(){
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void cleanUp() {
        quad.cleanUp();
        multisampleFbo.cleanUp();
        terrainFbo.cleanUp();
        terrainNormalFbo.cleanUp();
        fxFbo.cleanUp();
    }
}
