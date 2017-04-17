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
        Display.sync(FPS);
        Display.update();
    }

    @Override
    public void cleanUp(){
        Display.destroy();
    }
}
