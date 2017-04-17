package org.ninthworld.polygons.renderer;

import org.lwjgl.opengl.GL11;
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

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class ChunkRenderer implements IRenderer {

    private ChunkShader chunkShader;
    private ModelShader modelShader;

    public ChunkRenderer(Matrix4f projectionMatrix, ChunkShader chunkShader, ModelShader modelShader){
        this.chunkShader = chunkShader;
        this.modelShader = modelShader;

        chunkShader.start();
        chunkShader.loadProjectionMatrix(projectionMatrix);
        chunkShader.stop();

        modelShader.start();
        modelShader.loadProjectionMatrix(projectionMatrix);
        modelShader.stop();
    }

    public void render(ChunkManager chunkManager, ModelManager modelManager, Camera camera){
        chunkShader.start();
        chunkShader.loadViewMatrix(camera.getViewMatrix());

        for(Chunk chunk : chunkManager.loadedChunks.values()){
            if(chunk.getRawModel() != null) {
                chunkShader.loadTransformationMatrix(MatrixHelper.createTransformationMatrix(chunk.getChunkPos().getTransformationVector(), new Vector3f(0, 0, 0), 1));

                prepareRawModel(chunk.getRawModel());
                GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                unbindRawModel();
            }
        }

        chunkShader.stop();

        modelShader.start();
        modelShader.loadViewMatrix(camera.getViewMatrix());

        for(RawModel rawModel : modelManager.models.values()){
            prepareRawModel(rawModel);

            for(Chunk chunk : chunkManager.loadedChunks.values()) {
                for(ChunkEntity chunkEntity : chunk.entities){
                    if(modelManager.models.get(chunkEntity.getRawModelName()) == rawModel){
                        modelShader.loadTransformationMatrix(
                                MatrixHelper.createTransformationMatrix(
                                        Vector3f.add(chunkEntity.getPosition(), new Vector3f(chunk.getChunkPos().x * Chunk.CHUNK_SIZE, 0f, chunk.getChunkPos().y * Chunk.CHUNK_SIZE), null),
                                        new Vector3f(0, 0, 0),
                                        0.1f
                                )
                        );
                        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                    }
                }
            }

            unbindRawModel();
        }

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
