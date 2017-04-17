package org.ninthworld.polygons.helper;

import java.nio.ByteBuffer;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TextureData {

    private int width;
    private int height;
    private ByteBuffer buffer;

    public TextureData(ByteBuffer buffer, int width, int height){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }

}
