package org.ninthworld.polygons.display;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.ninthworld.polygons.engine.IManager;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class DisplayManager implements IManager {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;
    public static final boolean VSYNC = true;
    public static final String TITLE = "Polygons";

    public int fps;
    public long time;

    public DisplayManager(){
        fps = 0;
        time = System.nanoTime();
    }

    @Override
    public void initialize() {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setVSyncEnabled(VSYNC);
            Display.setTitle(TITLE);
            Display.create(
                    new PixelFormat(),
                    new ContextAttribs(3, 3)
                            .withForwardCompatible(true)
                            .withProfileCore(true)
            );

            GL11.glEnable(GL13.GL_MULTISAMPLE);
            GL11.glViewport(0, 0, WIDTH, HEIGHT);
        } catch(LWJGLException e){
            e.printStackTrace();
        }
    }
    public void update(){
        if(System.nanoTime() - time < 1000000000L){
            fps++;
        }else{
            updateTitle(TITLE + " - FPS: " + fps);
            fps = 0;
            time = System.nanoTime();
        }

        Display.sync(FPS);
        Display.update();
    }

    public void updateTitle(String str){
        Display.setTitle(str);
    }

    @Override
    public void cleanUp(){
        Display.destroy();
    }
}
