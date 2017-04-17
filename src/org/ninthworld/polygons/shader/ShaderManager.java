package org.ninthworld.polygons.shader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.polygons.engine.IManager;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ShaderManager implements IManager {

    public ChunkShader chunkShader;
    public ModelShader modelShader;
    public SkyboxShader skyboxShader;
    public FXShader fxShader;
    public NormalShader normalShader;
    public WaterShader waterShader;

    private Matrix4f projectionMatrix;

    public ShaderManager(Matrix4f projectionMatrix){
        this.projectionMatrix = projectionMatrix;
        chunkShader = new ChunkShader("/shader/chunk/chunk.vert", "/shader/chunk/chunk.frag");
        modelShader = new ModelShader("/shader/model/model.vert", "/shader/model/model.frag");
        skyboxShader = new SkyboxShader("/shader/skybox/skybox.vert", "/shader/skybox/skybox.frag");
        fxShader = new FXShader("/shader/postprocessing/fx.vert", "/shader/postprocessing/fx.frag");
        normalShader = new NormalShader("/shader/normal/normal.vert", "/shader/normal/normal.frag");
        waterShader = new WaterShader("/shader/water/water.vert", "/shader/water/water.frag");
    }

    @Override
    public void initialize(){
        fxShader.start();
        fxShader.loadSampleValues(sampleValues);
        fxShader.loadScreenSize(Display.getWidth(), Display.getHeight());
        fxShader.loadInvProjectionMatrix(Matrix4f.invert(projectionMatrix, null));
        fxShader.stop();
    }

    @Override
    public void cleanUp(){
    }

    private static final float[] sampleValues = new float[]{
            -0.94201624f, -0.39906216f,
            0.94558609f, -0.76890725f,
            -0.09418410f, -0.92938870f,
            0.34495938f, 0.29387760f,
            -0.91588581f, 0.45771432f,
            -0.81544232f, -0.87912464f,
            -0.38277543f, 0.27676845f,
            0.97484398f, 0.75648379f,
            0.44323325f, -0.97511554f,
            0.53742981f, -0.47373420f,
            -0.26496911f, -0.41893023f,
            0.79197514f, 0.19090188f,
            -0.24188840f, 0.99706507f,
            -0.81409955f, 0.91437590f,
            0.19984126f, 0.78641367f,
            0.14383161f, -0.14100790f
    };
}
