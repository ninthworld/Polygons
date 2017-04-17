package org.ninthworld.polygons.helper;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.loader.Loader;
import org.ninthworld.polygons.model.RawModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by NinthWorld on 4/15/2017.
 */
public class ModelHelper {

    private List<Float> verticesList;
    private List<Float> colorsList;
    private List<Float> normalsList;
    private List<Integer> indicesList;
    private int indicesPointer;

    public ModelHelper(){
        this.verticesList = new ArrayList<>();
        this.colorsList = new ArrayList<>();
        this.normalsList = new ArrayList<>();
        this.indicesList = new ArrayList<>();
        this.indicesPointer = 0;
    }

    public void addTriangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f color) {
        Vector3f normal = getNormal(v1, v2, v3);

        addVectorToList(verticesList, v1);
        addVectorToList(verticesList, v2);
        addVectorToList(verticesList, v3);

        for (int i = 0; i < 3; i++) {
            addVectorToList(colorsList, color);
            addVectorToList(normalsList, normal);

            indicesList.add(indicesPointer++);
        }
    }

    public void addTriangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f color1, Vector3f color2, Vector3f color3) {
        Vector3f normal = getNormal(v1, v2, v3);

        addVectorToList(verticesList, v1);
        addVectorToList(verticesList, v2);
        addVectorToList(verticesList, v3);

        addVectorToList(colorsList, color1);
        addVectorToList(colorsList, color2);
        addVectorToList(colorsList, color3);

        for (int i = 0; i < 3; i++) {
            addVectorToList(normalsList, normal);

            indicesList.add(indicesPointer++);
        }
    }

    public void addTriangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f color, Vector3f normal) {
        addVectorToList(verticesList, v1);
        addVectorToList(verticesList, v2);
        addVectorToList(verticesList, v3);

        for (int i = 0; i < 3; i++) {
            addVectorToList(colorsList, color);
            addVectorToList(normalsList, normal);

            indicesList.add(indicesPointer++);
        }
    }

    public Vector3f getNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        Vector3f normal = new Vector3f();
        Vector3f.cross(Vector3f.sub(v2, v1, null), Vector3f.sub(v3, v1, null), normal);
        normal.normalise();

        return normal;
    }

    private void addVectorToList(List<Float> list, Vector3f v){
        list.add(v.x);
        list.add(v.y);
        list.add(v.z);
    }

    public RawModel generateRawModel() {
        float[] vertices = getFloatArray(verticesList);
        float[] normals = getFloatArray(normalsList);
        float[] colors = getFloatArray(colorsList);
        int[] indices  = getIntArray(indicesList);

        return Loader.load(vertices, normals, colors, indices);
    }

    private static float[] getFloatArray(List<Float> list){
        float[] array = new float[list.size()];
        for(int i=0; i<array.length; i++){
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] getIntArray(List<Integer> list){
        int[] array = new int[list.size()];
        for(int i=0; i<array.length; i++){
            array[i] = list.get(i);
        }
        return array;
    }
}
