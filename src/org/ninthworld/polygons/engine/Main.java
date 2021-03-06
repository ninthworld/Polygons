package org.ninthworld.polygons.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.camera.Camera;
import org.ninthworld.polygons.chunk.ChunkManager;
import org.ninthworld.polygons.display.DisplayManager;
import org.ninthworld.polygons.fbo.FboManager;
import org.ninthworld.polygons.model.ModelManager;
import org.ninthworld.polygons.renderer.RendererManager;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class Main implements IManager {

    private DisplayManager displayManager;
    private RendererManager rendererManager;
    private ChunkManager chunkManager;
    private ModelManager modelManager;
    private FboManager fboManager;

    private Camera camera;

    public Main(){
        (displayManager = new DisplayManager()).initialize();

        rendererManager = new RendererManager();
        chunkManager = new ChunkManager();
        modelManager = new ModelManager();
        fboManager = new FboManager();

        camera = new Camera(
                    new Vector3f(0, 20, 0),
                    new Vector3f(0, 0, 0),
                    new Camera(
                            new Vector3f(0, 20, 0),
                            new Vector3f(0, 0, 0),
                            null
                    )
        );

        initialize();
    }

    @Override
    public void initialize() {
        rendererManager.initialize();
        chunkManager.initialize();
        modelManager.initialize();
        fboManager.initialize();

        update();
    }

    public void update(){
        while(!Display.isCloseRequested()){
            rendererManager.clearBuffers();

            if(camera.isEnabled) {
                camera.updateKeyboard();
                camera.updateMouse();
            }else{
                camera.debugCamera.updateKeyboard();
                camera.debugCamera.updateMouse();
            }
            camera.debugKeyboard(chunkManager);

            chunkManager.updateChunks(camera);

            fboManager.render(rendererManager, chunkManager, modelManager, camera);

            displayManager.update();
        }

        cleanUp();
    }

    @Override
    public void cleanUp(){
        fboManager.cleanUp();
        modelManager.cleanUp();
        chunkManager.cleanUp();
        rendererManager.cleanUp();
        displayManager.cleanUp();
    }

    public static void main(String[] args){
        new Main();
    }
}
