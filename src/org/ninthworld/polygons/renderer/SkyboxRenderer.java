package org.ninthworld.polygons.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.camera.Camera;
import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.chunk.ChunkEntity;
import org.ninthworld.polygons.chunk.ChunkManager;
import org.ninthworld.polygons.helper.MatrixHelper;
import org.ninthworld.polygons.model.ModelManager;
import org.ninthworld.polygons.model.RawModel;
import org.ninthworld.polygons.shader.ChunkShader;
import org.ninthworld.polygons.shader.ModelShader;
import org.ninthworld.polygons.shader.SkyboxShader;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class SkyboxRenderer implements IRenderer {

    private SkyboxShader skyboxShader;

    public SkyboxRenderer(Matrix4f projectionMatrix, SkyboxShader skyboxShader){
        this.skyboxShader = skyboxShader;

        skyboxShader.start();
        skyboxShader.loadProjectionMatrix(projectionMatrix);
        skyboxShader.stop();
    }

    public void render(ModelManager modelManager, Camera camera){
        skyboxShader.start();
        skyboxShader.loadViewMatrix(camera.getViewMatrix());
        GL30.glBindVertexArray(modelManager.skyboxCube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, modelManager.skyboxTexture);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, modelManager.skyboxCube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        skyboxShader.stop();
    }

    @Override
    public void render() {}

    @Override
    public void prepareRawModel(RawModel rawModel){
    }

    @Override
    public void unbindRawModel(){
    }

    @Override
    public void cleanUp(){
    }
}
