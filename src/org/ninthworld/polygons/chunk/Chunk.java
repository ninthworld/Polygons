package org.ninthworld.polygons.chunk;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.model.ModelManager;
import org.ninthworld.polygons.terrain.TerrainGenerator;
import org.ninthworld.polygons.helper.MathHelper;
import org.ninthworld.polygons.helper.ModelHelper;
import org.ninthworld.polygons.helper.Vector2i;
import org.ninthworld.polygons.model.RawModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class Chunk {

    public static final int SEED = 12345;
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_HEIGHT = 128;
    public static final float WATER_LEVEL = 0f;

    private Vector2i chunkPos;
    private double[][] heightData;
    private RawModel rawModel;

    private RawModel waterRawModel;

    public List<ChunkEntity> entities;

    public Chunk(Vector2i chunkPos){
        this.chunkPos = chunkPos;
        this.heightData = new double[CHUNK_SIZE][CHUNK_SIZE];
        this.rawModel = null;
        this.waterRawModel = null;
        this.entities = new ArrayList<>();
    }

    public RawModel getRawModel(){
        return rawModel;
    }

    public RawModel getWaterRawModel(){
        return waterRawModel;
    }

    public Vector2i getChunkPos(){
        return chunkPos;
    }

    public double getHeightAt(int x, int y){
        if(isInBounds(x, y)) {
            return heightData[x][y];
        }else{
            return 0;
        }
    }

    public void setHeightAt(int x, int y, double height){
        if(isInBounds(x, y)) heightData[x][y] = height;
    }

    public boolean isInBounds(int x, int y){
        return (x >= 0 && x < heightData.length && y >= 0 && y < heightData[0].length);
    }

    public void cleanUp(){
        if(rawModel != null) rawModel.cleanUp();
        if(waterRawModel != null) waterRawModel.cleanUp();
    }

    public void generateChunkData(TerrainGenerator terrainGenerator){
        for(int x=0; x<heightData.length; x++){
            for(int y=0; y<heightData[x].length; y++){
                heightData[x][y] = terrainGenerator.getHeightAt(x + chunkPos.x * CHUNK_SIZE, y + chunkPos.y * CHUNK_SIZE);

                String entity = terrainGenerator.getEntityAt(x + chunkPos.x * CHUNK_SIZE, y + chunkPos.y * CHUNK_SIZE);
                if(entity != null && !entity.equals("")){
                    entities.add(new ChunkEntity(entity, new Vector3f(x, (float) heightData[x][y], y)));
                }
            }
        }
    }

    public void generateChunkModel(ChunkManager chunkManager){
        ModelHelper modelHelper = new ModelHelper();

        for(int x=0; x<heightData.length; x++){
            for(int y=0; y<heightData[x].length; y++){

                // 0 ---- 1
                // | A /  |
                // |  / B |
                // 2 ---- 3


                float[] deltas = new float[4];
                Vector3f[] vertices = new Vector3f[4];
                for(int yy=0; yy<2; yy++){
                    for(int xx=0; xx<2; xx++){
                        int posX = x + xx + chunkPos.x * CHUNK_SIZE;
                        int posY = y + yy + chunkPos.y * CHUNK_SIZE;
                        double height = chunkManager.getHeightAt(posX, posY);

                        double nHeight0 = chunkManager.getHeightAt(posX-1, posY);
                        double nHeight1 = chunkManager.getHeightAt(posX+1, posY);
                        double nHeight2 = chunkManager.getHeightAt(posX, posY-1);
                        double nHeight3 = chunkManager.getHeightAt(posX, posY+1);

                        double maxHeight = MathHelper.max(nHeight0, nHeight1, nHeight2, nHeight3);
                        double minHeight = MathHelper.min(nHeight0, nHeight1, nHeight2, nHeight3);

                        deltas[yy*2 + xx] = (float) MathHelper.clamp((height - minHeight) / (maxHeight - minHeight) - 0.5, -0.4, 0.4);
                        vertices[yy*2 + xx] = new Vector3f(x + xx, (float) height, y + yy);
                    }
                }

                Vector3f normal1 = modelHelper.getNormal(vertices[2], vertices[1], vertices[0]);
                Vector3f normal2 = modelHelper.getNormal(vertices[1], vertices[2], vertices[3]);
                Vector3f stone = new Vector3f(184/255f, 193/255f, 163/255f);
                Vector3f col = chunkManager.terrainGenerator.getColorAt(x + chunkPos.x * CHUNK_SIZE, y + chunkPos.y * CHUNK_SIZE);

                float angle = 0.1f;
                if(Vector3f.dot(normal1, new Vector3f(0, 1, 0)) > angle){
                    modelHelper.addTriangle(vertices[2], vertices[1], vertices[0], col);
                }else{
                    modelHelper.addTriangle(vertices[2], vertices[1], vertices[0], stone);
                }

                if(Vector3f.dot(normal2, new Vector3f(0, 1, 0)) > angle) {
                    modelHelper.addTriangle(vertices[1], vertices[2], vertices[3], col);
                }else{
                    modelHelper.addTriangle(vertices[1], vertices[2], vertices[3], stone);
                }

            }
        }

        rawModel = modelHelper.generateRawModel();

        generateWaterModel(chunkManager);
    }

    public void generateWaterModel(ChunkManager chunkManager){
        ModelHelper modelHelper = new ModelHelper();

        for(int x=0; x<heightData.length; x++) {
            for (int y = 0; y < heightData[x].length; y++) {
                Vector3f color0 = new Vector3f(0/255f, 0/255f, 0/255f);
                Vector3f color1 = new Vector3f(255/255f, 0/255f, 0/255f);

                Vector3f[] vertices = new Vector3f[4];
                Vector3f[] colors = new Vector3f[4];
                double minHeight = Float.MAX_VALUE;
                for(int yy=0; yy<2; yy++){
                    for(int xx=0; xx<2; xx++){
                        int posX = x + xx + chunkPos.x * CHUNK_SIZE;
                        int posY = y + yy + chunkPos.y * CHUNK_SIZE;

                        vertices[yy*2 + xx] = new Vector3f(x + xx, WATER_LEVEL, y + yy);

                        double height = chunkManager.getHeightAt(posX, posY);
                        minHeight = Math.min(height, minHeight);
                        colors[yy*2 + xx] = MathHelper.mix(color0, color1, (float) MathHelper.clamp(1f - height/(-16.0f), 0f, 1f));
                    }
                }

                if(minHeight <= WATER_LEVEL + 1) {
                    modelHelper.addTriangle(vertices[2], vertices[1], vertices[0], colors[2], colors[1], colors[0]);
                    modelHelper.addTriangle(vertices[1], vertices[2], vertices[3], colors[1], colors[2], colors[3]);
                }
            }
        }

        waterRawModel = modelHelper.generateRawModel();
    }

}