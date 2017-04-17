package org.ninthworld.polygons.model;

import org.ninthworld.polygons.engine.IManager;

import java.util.HashMap;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ModelManager implements IManager {

    public static final String PINE_TREE = "pineTree";

    public HashMap<String, RawModel> models;

    public ModelManager(){
        models = new HashMap<>();
    }

    @Override
    public void initialize(){
        models.put(PINE_TREE, PineModel.generatePineRawModel());
    }

    @Override
    public void cleanUp(){
        for(RawModel rawModel : models.values()){
            rawModel.cleanUp();
        }
    }
}
