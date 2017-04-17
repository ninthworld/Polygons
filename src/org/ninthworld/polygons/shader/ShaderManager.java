package org.ninthworld.polygons.shader;

import org.ninthworld.polygons.engine.IManager;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ShaderManager implements IManager {

    public ChunkShader chunkShader;
    public ModelShader modelShader;
    public SkyboxShader skyboxShader;

    public ShaderManager(){
        chunkShader = new ChunkShader("/shader/chunk/chunk.vert", "/shader/chunk/chunk.frag");
        modelShader = new ModelShader("/shader/model/model.vert", "/shader/model/model.frag");
        skyboxShader = new SkyboxShader("/shader/skybox/skybox.vert", "/shader/skybox/skybox.frag");
    }

    @Override
    public void initialize(){
    }

    @Override
    public void cleanUp(){
    }
}
