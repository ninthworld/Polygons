package org.ninthworld.polygons.renderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.polygons.engine.IManager;
import org.ninthworld.polygons.helper.MatrixHelper;
import org.ninthworld.polygons.shader.ShaderManager;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class RendererManager implements IManager {

    public Matrix4f projectionMatrix;
    public ShaderManager shaderManager;
    public ChunkRenderer chunkRenderer;
    public ModelRenderer modelRenderer;
    public SkyboxRenderer skyboxRenderer;

    public RendererManager(){
        projectionMatrix = MatrixHelper.createProjectionMatrix(Display.getWidth(), Display.getHeight(), 70f, 0.1f, 1000f);
        shaderManager = new ShaderManager(projectionMatrix);
        chunkRenderer = new ChunkRenderer(projectionMatrix, shaderManager.chunkShader, shaderManager.modelShader, shaderManager.normalShader, shaderManager.waterShader);
        modelRenderer = new ModelRenderer(projectionMatrix, shaderManager.modelShader);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, shaderManager.skyboxShader);

    }

    @Override
    public void initialize(){
        shaderManager.initialize();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glClearColor(0x00/255f, 0x96/255f, 0xff/255f, 1);
    }

    public void clearBuffers(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void cleanUp(){
        shaderManager.cleanUp();
        chunkRenderer.cleanUp();
    }
}
