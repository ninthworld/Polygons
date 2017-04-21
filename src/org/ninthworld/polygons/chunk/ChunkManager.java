package org.ninthworld.polygons.chunk;

import org.lwjgl.input.Keyboard;
import org.ninthworld.polygons.camera.Camera;
import org.ninthworld.polygons.engine.IManager;
import org.ninthworld.polygons.terrain.TerrainGenerator;
import org.ninthworld.polygons.helper.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ChunkManager implements IManager {

    public int loadRadius;
    public HashMap<String, Chunk> loadedChunks;

    public TerrainGenerator terrainGenerator;

    public ChunkManager(){
        loadRadius = 8;
        loadedChunks = new HashMap<>();
        terrainGenerator = new TerrainGenerator();
    }

    @Override
    public void initialize(){
//        for(int x=-loadRadius; x<loadRadius; x++){
//            for(int y=-loadRadius; y<loadRadius; y++){
//                Vector2i chunkPos = new Vector2i(x, y);
//                Chunk chunk = new Chunk(chunkPos);
//                chunk.generateChunkData(terrainGenerator);
//
//                loadedChunks.put(chunkPos.toHashString(), chunk);
//            }
//        }
//
//        for(Chunk chunk : loadedChunks.values()){
//            chunk.generateChunkModel(this);
//        }
    }

    public double getHeightAt(int x, int y){
        Vector2i chunkPos = new Vector2i((int)Math.floor(x/(double)Chunk.CHUNK_SIZE), (int)Math.floor(y/(double)Chunk.CHUNK_SIZE));
        if(loadedChunks.containsKey(chunkPos.toHashString())){
            return loadedChunks.get(chunkPos.toHashString()).getHeightAt(Math.floorMod(x, Chunk.CHUNK_SIZE), Math.floorMod(y, Chunk.CHUNK_SIZE));
        }else{
            return terrainGenerator.getSmoothHeightAt(x, y);
        }
    }

    public void updateChunks(Camera camera){
        Vector2i cameraChunkPos = new Vector2i((int)Math.floor(camera.getPosition().x/(double)Chunk.CHUNK_SIZE), (int)Math.floor(camera.getPosition().z/(double)Chunk.CHUNK_SIZE));

        for(int x=-loadRadius; x<loadRadius; x++){
            for(int y=-loadRadius; y<loadRadius; y++){
                Vector2i chunkPos = new Vector2i(x + cameraChunkPos.x, y + cameraChunkPos.y);
                if(!loadedChunks.containsKey(chunkPos.toHashString())){
                    Chunk chunk = new Chunk(chunkPos);
                    chunk.generateChunkData(terrainGenerator);

                    loadedChunks.put(chunkPos.toHashString(), chunk);
                }
            }
        }

        List<Chunk> unload = new ArrayList<>();
        for(Chunk chunk : loadedChunks.values()){
            if(chunk.getChunkPos().x >= cameraChunkPos.x + loadRadius ||
               chunk.getChunkPos().x < cameraChunkPos.x - loadRadius ||
               chunk.getChunkPos().y >= cameraChunkPos.y + loadRadius ||
               chunk.getChunkPos().y < cameraChunkPos.y - loadRadius){
                unload.add(chunk);
            }else{
                if(chunk.getRawModel() == null){
                    chunk.generateChunkModel(this);
                }
            }
        }

        for(Chunk chunk : unload){
            loadedChunks.remove(chunk.getChunkPos().toHashString());
            chunk.cleanUp();
        }

        if(!camera.isFreemode) {
            float height = (float) terrainGenerator.getSmoothHeightAt(camera.getPosition().x, camera.getPosition().z) + 0.8f;
            if (camera.getPosition().y < height) {
                camera.getPosition().setY(height);
                camera.onGround = true;
            }
        }
    }

    @Override
    public void cleanUp(){
        for(Chunk chunk : loadedChunks.values()){
            chunk.cleanUp();
        }
    }
}
