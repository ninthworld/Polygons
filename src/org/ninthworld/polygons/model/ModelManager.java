package org.ninthworld.polygons.model;

import org.lwjgl.opengl.GL11;
import org.ninthworld.polygons.engine.IManager;
import org.ninthworld.polygons.helper.TextureData;
import org.ninthworld.polygons.loader.Loader;

import java.util.HashMap;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ModelManager implements IManager {

    private static String[] TEXTURE_FILES = {"/skybox/side.png", "/skybox/side.png", "/skybox/top.png", "/skybox/bottom.png", "/skybox/side.png", "/skybox/side.png"};

    public static final String PINE_TREE = "pineTree";

    public HashMap<String, RawModel> models;
    public RawModel skyboxCube;
    public int skyboxTexture;

    public ModelManager(){
        models = new HashMap<>();
    }

    @Override
    public void initialize(){
        models.put(PINE_TREE, PineModel.generatePineRawModel());
        skyboxCube = Loader.load(VERTICES, 3);

        TextureData[] textureData = new TextureData[TEXTURE_FILES.length];
        for(int i=0; i<TEXTURE_FILES.length; i++){
            textureData[i] = Loader.decodeTextureFile(getClass().getResourceAsStream(TEXTURE_FILES[i]));
        }
        skyboxTexture = Loader.loadCubeMap(textureData);
    }

    @Override
    public void cleanUp(){
        for(RawModel rawModel : models.values()){
            rawModel.cleanUp();
        }

        skyboxCube.cleanUp();
        GL11.glDeleteTextures(skyboxTexture);
    }

    private static final float SIZE = 500f;
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

}
