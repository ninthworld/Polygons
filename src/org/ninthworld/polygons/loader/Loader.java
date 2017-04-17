package org.ninthworld.polygons.loader;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.ninthworld.polygons.helper.TextureData;
import org.ninthworld.polygons.model.RawModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

    public static RawModel load(float[] positions, int dimesions){
        int vaoID = createVAO();
        storeDataInAttributeList(0, dimesions, positions);
        unbindVAO();
        return new RawModel(vaoID, 0, positions.length/dimesions);
    }

    public static int loadTexture(String fileType, InputStream textureFile) throws IOException {
        Texture texture = TextureLoader.getTexture(fileType, textureFile);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);

        return texture.getTextureID();
    }

    public static int loadCubeMap(TextureData[] textureData){
        int textureID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);

        for(int i=0; i<textureData.length; i++){
            TextureData data = textureData[i];
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        return textureID;
    }

    public static TextureData decodeTextureFile(InputStream file) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            PNGDecoder decoder = new PNGDecoder(file);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
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
