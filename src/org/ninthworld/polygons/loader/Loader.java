package org.ninthworld.polygons.loader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.ninthworld.polygons.model.RawModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class Loader {

    public static RawModel load(float[] vertices, int[] indices){
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        unbindVAO();
        return new RawModel(vaoID, vboID, indices.length);
    }

    public static RawModel load(float[] vertices, float[] normals, float[] colors, int[] indices){
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 3, normals);
        storeDataInAttributeList(2, 3, colors);
        unbindVAO();
        return new RawModel(vaoID, vboID, indices.length);
    }

    public static int loadTexture(String fileType, InputStream textureFile) throws IOException {
        Texture texture = TextureLoader.getTexture(fileType, textureFile);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);

        return texture.getTextureID();
    }

    private static int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private static void storeDataInAttributeList(int attrNum, int dimensions, float[] data){
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNum, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    }

    private static void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private static int bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vboID;
    }

    private static IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }
}
