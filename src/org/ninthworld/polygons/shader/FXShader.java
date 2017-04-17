package org.ninthworld.polygons.shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class FXShader extends AShader {

    private int[] location_sampleValues;
    private int location_terrainColorTexture,
                location_terrainDepthTexture,
                location_terrainNormalTexture,
                location_screenSize,
                location_invProjectionMatrix,
                location_cameraPos;

    public FXShader(String vertexFile, String fragmentFile){
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_sampleValues = new int[32];
        for(int i=0; i<location_sampleValues.length; i++){
            location_sampleValues[i] = super.getUniformLocation("samples[" + i + "]");
        }

        location_terrainColorTexture = super.getUniformLocation("terrainColorTexture");
        location_terrainDepthTexture = super.getUniformLocation("terrainDepthTexture");
        location_terrainNormalTexture = super.getUniformLocation("terrainNormalTexture");

        location_screenSize = super.getUniformLocation("screenSize");
        location_invProjectionMatrix = super.getUniformLocation("invProjectionMatrix");
        location_cameraPos = super.getUniformLocation("cameraPos");
    }

    public void loadCameraPos(Vector3f pos){
        super.loadVector3f(location_cameraPos, pos);
    }

    public void loadSampleValues(float[] sampleValues){
        for(int i=0; i<location_sampleValues.length; i++){
            super.loadFloat(location_sampleValues[i], sampleValues[i]);
        }
    }

    public void connectTextures(){
        super.loadInteger(location_terrainColorTexture, 0);
        super.loadInteger(location_terrainDepthTexture, 1);
        super.loadInteger(location_terrainNormalTexture, 2);
    }

    public void loadScreenSize(int width, int height){
        super.loadVector2f(location_screenSize, new Vector2f(width, height));
    }

    public void loadInvProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_invProjectionMatrix, matrix);
    }
}
