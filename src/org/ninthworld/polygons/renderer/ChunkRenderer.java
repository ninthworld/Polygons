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
import org.ninthworld.polygons.helper.Frustum;
import org.ninthworld.polygons.helper.MatrixHelper;
import org.ninthworld.polygons.model.ModelManager;
import org.ninthworld.polygons.model.RawModel;
import org.ninthworld.polygons.shader.ChunkShader;
import org.ninthworld.polygons.shader.ModelShader;
import org.ninthworld.polygons.shader.NormalShader;
import org.ninthworld.polygons.shader.WaterShader;

import java.nio.FloatBuffer;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class ChunkRenderer implements IRenderer {

    private ChunkShader chunkShader;
    private ModelShader modelShader;
    private NormalShader normalShader;
    private WaterShader waterShader;

    private Matrix4f projectionMatrix;

    public ChunkRenderer(Matrix4f projectionMatrix, ChunkShader chunkShader, ModelShader modelShader, NormalShader normalShader, WaterShader waterShader){
        this.projectionMatrix = projectionMatrix;
        this.chunkShader = chunkShader;
        this.modelShader = modelShader;
        this.normalShader = normalShader;
        this.waterShader = waterShader;

        chunkShader.start();
        chunkShader.loadProjectionMatrix(projectionMatrix);
        chunkShader.stop();

        modelShader.start();
        modelShader.loadProjectionMatrix(projectionMatrix);
        modelShader.stop();

        normalShader.start();
        normalShader.loadProjectionMatrix(projectionMatrix);
        normalShader.stop();

        waterShader.start();
        waterShader.loadProjectionMatrix(projectionMatrix);
        waterShader.stop();
    }

    private void drawChunks(ChunkManager chunkManager, Frustum frustum) {
        for(Chunk chunk : chunkManager.loadedChunks.values()){
            if(chunk.getRawModel() != null && isInViewFrustum(chunk, frustum)) {
                chunkShader.loadTransformationMatrix(MatrixHelper.createTransformationMatrix(chunk.getChunkPos().getTransformationVector(), new Vector3f(0, 0, 0), 1));

                prepareRawModel(chunk.getRawModel());
                GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                unbindRawModel();
            }
        }
    }

    private void drawModels(ChunkManager chunkManager, ModelManager modelManager, Frustum frustum) {
        for(RawModel rawModel : modelManager.models.values()){
            prepareRawModel(rawModel);

            for(Chunk chunk : chunkManager.loadedChunks.values()) {
                if(isInViewFrustum(chunk, frustum)) {
                    for (ChunkEntity chunkEntity : chunk.entities) {
                        if (modelManager.models.get(chunkEntity.getRawModelName()) == rawModel) {
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
            }
            unbindRawModel();
        }
    }

    private void drawWaters(ChunkManager chunkManager, Frustum frustum){
        for(Chunk chunk : chunkManager.loadedChunks.values()){
            if(chunk.getWaterRawModel() != null && isInViewFrustum(chunk, frustum)) {
                waterShader.loadTransformationMatrix(MatrixHelper.createTransformationMatrix(chunk.getChunkPos().getTransformationVector(), new Vector3f(0, 0, 0), 1));

                prepareRawModel(chunk.getWaterRawModel());
                GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getWaterRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                unbindRawModel();
            }
        }
    }

    public void render(ChunkManager chunkManager, ModelManager modelManager, Camera camera){
        Frustum frustum = Frustum.getFrustum(projectionMatrix, camera.getViewMatrix());

        Matrix4f viewMatrix = (camera.isEnabled ? camera.getViewMatrix() : camera.debugCamera.getViewMatrix());

        chunkShader.start();
        chunkShader.loadViewMatrix(viewMatrix);
        drawChunks(chunkManager, frustum);
        chunkShader.stop();

        modelShader.start();
        modelShader.loadViewMatrix(viewMatrix);
        drawModels(chunkManager, modelManager, frustum);
        modelShader.stop();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderWater(chunkManager, viewMatrix, frustum);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private float clock = 0f;
    public void renderWater(ChunkManager chunkManager, Matrix4f viewMatrix, Frustum frustum){
        waterShader.start();
        waterShader.loadViewMatrix(viewMatrix);
        waterShader.loadClock(clock);
        drawWaters(chunkManager, frustum);
        waterShader.stop();

        clock += 0.01f;
        if(clock >= 1f) clock = 0f;
    }

    public void renderNormal(ChunkManager chunkManager, ModelManager modelManager, Camera camera){
        Frustum frustum = Frustum.getFrustum(projectionMatrix, camera.getViewMatrix());

        Matrix4f viewMatrix = (camera.isEnabled ? camera.getViewMatrix() : camera.debugCamera.getViewMatrix());

        normalShader.start();
        normalShader.loadViewMatrix(viewMatrix);
        drawChunks(chunkManager, frustum);
        drawModels(chunkManager, modelManager, frustum);
        normalShader.stop();
    }

    private boolean isInViewFrustum(Chunk chunk, Frustum frustum){
        float x = chunk.getChunkPos().x * Chunk.CHUNK_SIZE;
        float y = 0;
        float z = chunk.getChunkPos().y * Chunk.CHUNK_SIZE;

        float size = Chunk.CHUNK_SIZE;
        float height = Chunk.CHUNK_HEIGHT;

        return frustum.cubeInFrustum(x, y, z, x + size, y + height, z + size);

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
