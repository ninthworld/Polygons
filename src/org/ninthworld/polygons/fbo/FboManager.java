package org.ninthworld.polygons.fbo;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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


    public FboManager(){
        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
    }

    @Override
    public void initialize() {
        quad = Loader.load(POSITIONS, 2);
    }

    public void postProcessing(){
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void render(RendererManager rendererManager, ChunkManager chunkManager, ModelManager modelManager, Camera camera){

        multisampleFbo.bindFrameBuffer();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        rendererManager.skyboxRenderer.render(modelManager, camera);
        rendererManager.chunkRenderer.render(chunkManager, modelManager, camera);

        multisampleFbo.unbindFrameBuffer();
        multisampleFbo.resolveToScreen();
    }

    @Override
    public void cleanUp() {
        quad.cleanUp();
        multisampleFbo.cleanUp();
    }

    private static final float[] sampleValues = new float[]{
            -0.94201624f, -0.39906216f,
            0.94558609f, -0.76890725f,
            -0.09418410f, -0.92938870f,
            0.34495938f, 0.29387760f,
            -0.91588581f, 0.45771432f,
            -0.81544232f, -0.87912464f,
            -0.38277543f, 0.27676845f,
            0.97484398f, 0.75648379f,
            0.44323325f, -0.97511554f,
            0.53742981f, -0.47373420f,
            -0.26496911f, -0.41893023f,
            0.79197514f, 0.19090188f,
            -0.24188840f, 0.99706507f,
            -0.81409955f, 0.91437590f,
            0.19984126f, 0.78641367f,
            0.14383161f, -0.14100790f
    };
}
