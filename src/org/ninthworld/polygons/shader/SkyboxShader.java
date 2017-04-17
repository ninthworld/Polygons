package org.ninthworld.polygons.shader;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class SkyboxShader extends AShader {

    private int location_projectionMatrix,
                location_viewMatrix;

    public SkyboxShader(String vertexFile, String fragmentFile){
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadViewMatrix(Matrix4f matrix){
        matrix.m30 = matrix.m31 = matrix.m32 = 0;
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

}
