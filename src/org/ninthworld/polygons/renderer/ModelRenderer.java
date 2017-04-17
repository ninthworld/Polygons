package org.ninthworld.polygons.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.camera.Camera;
import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.chunk.ChunkManager;
import org.ninthworld.polygons.helper.MatrixHelper;
import org.ninthworld.polygons.model.RawModel;
import org.ninthworld.polygons.shader.ChunkShader;
import org.ninthworld.polygons.shader.ModelShader;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class ModelRenderer implements IRenderer {

    private ModelShader modelShader;

    public ModelRenderer(Matrix4f projectionMatrix, ModelShader modelShader){
        this.modelShader = modelShader;

        modelShader.start();
        modelShader.loadProjectionMatrix(projectionMatrix);
        modelShader.stop();
    }

    public void render(RawModel rawModel, Camera camera){
        modelShader.start();
        modelShader.loadViewMatrix(camera.getViewMatrix());

        modelShader.loadTransformationMatrix(MatrixHelper.createTransformationMatrix(new Vector3f(0, 0,0), new Vector3f(0, 0, 0), 1));

        prepareRawModel(rawModel);
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbindRawModel();

        modelShader.stop();
    }

    @Override
    public void render() {}

    @Override
    public void prepareRawModel(RawModel rawModel){
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    @Override
    public void unbindRawModel(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void cleanUp(){
    }
}
