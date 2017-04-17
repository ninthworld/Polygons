package org.ninthworld.polygons.helper;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.chunk.Chunk;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class Vector2i {

    public int x, y;

    public Vector2i(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public String toHashString(){
        return ("x" + x + "y" + y);
    }

    public Vector3f getTransformationVector(){
        return new Vector3f(x * Chunk.CHUNK_SIZE, 0, y * Chunk.CHUNK_SIZE);
    }
}
