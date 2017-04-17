package org.ninthworld.polygons.shader;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ChunkShader extends AShader {

    private int location_transformationMatrix,
                location_projectionMatrix,
                location_viewMatrix;

    public ChunkShader(String vertexFile, String fragmentFile){
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "normal");
        super.bindAttribute(2, "color");
    }

    @Override
    protected void getAllUniformLocations(){
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix){
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

}
