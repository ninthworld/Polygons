package org.ninthworld.polygons.renderer;

import org.ninthworld.polygons.model.RawModel;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public interface IRenderer {

    void render();

    void prepareRawModel(RawModel rawModel);

    void unbindRawModel();

    void cleanUp();
}
