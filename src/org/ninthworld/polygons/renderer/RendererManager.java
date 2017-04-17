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

    public boolean isWireframe, isntWireframe;

    public RendererManager(){
        shaderManager = new ShaderManager();
        projectionMatrix = MatrixHelper.createProjectionMatrix(Display.getWidth(), Display.getHeight(), 70f, 0.1f, 1000f);
        chunkRenderer = new ChunkRenderer(projectionMatrix, shaderManager.chunkShader, shaderManager.modelShader);
        modelRenderer = new ModelRenderer(projectionMatrix, shaderManager.modelShader);

        isWireframe = false;
        isntWireframe = true;
    }

    @Override
    public void initialize(){
        shaderManager.initialize();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glClearColor(0.7f, 0.9f, 1f, 1);
    }

    public void clearBuffers(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void debugKeyboard(){
        if(Keyboard.isKeyDown(Keyboard.KEY_1)){
            if(isWireframe){
                isntWireframe = true;
            }else{
                isntWireframe = false;
            }
        }else{
            if(isWireframe == isntWireframe){
                isWireframe = !isntWireframe;
            }
        }

        if(isWireframe) GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        else GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    @Override
    public void cleanUp(){
        shaderManager.cleanUp();
        chunkRenderer.cleanUp();
    }
}
