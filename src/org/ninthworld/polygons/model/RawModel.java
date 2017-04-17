package org.ninthworld.polygons.model;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class RawModel {

    private int vaoID, vboID, vertexCount;

    public RawModel(int vaoID, int vboID, int vertexCount){
        this.vaoID = vaoID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID(){
        return vaoID;
    }

    public int getVboID(){
        return vboID;
    }

    public int getVertexCount(){
        return vertexCount;
    }

    public void cleanUp(){
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
    }
}
