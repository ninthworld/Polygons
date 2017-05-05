package org.ninthworld.polygons.terrain;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.helper.ModelHelper;
import org.ninthworld.polygons.model.RawModel;

import java.util.Random;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TreeGenerator {

    private static final Vector3f leaves1 = new Vector3f(36/255f, 142/255f, 79/255f); // Highlight (88, 172, 146), Darkest: (16, 88, 56)
    private static final Vector3f trunk1 = new Vector3f(95/255f, 89/255f, 59/255f); // Darkest: (57, 57, 48)

    private static final Vector3f leaves2 = new Vector3f(32/255f, 152/255f, 86/255f); // Highlight (88, 172, 146), Darkest: (16, 88, 56)
    private static final Vector3f trunk2 = new Vector3f(115/255f, 82/255f, 64/255f); // Darkest: (57, 57, 48)

    public static RawModel generatorRedwoodTree(){
        int trunkSections = 3;
        float[] trunkRadius = new float[]{3.5f, 3.0f, 2.0f, 1.8f};
        float[] trunkHeight = new float[]{5.5f, 22.0f, 16.0f};
        int trunkNGon = 6;

        int leafSections = 6;
        int leafSubSections = 2;
        int[] leafNGons = new int[]{8, 8, 6, 6, 4, 4};
        float[][] leafRadius = new float[][]{
                {12f, 8f, 6f},
                {11f, 7f, 5f},
                {10f, 6f, 4f},
                {9f, 5f, 3f},
                {8f, 4f, 2f},
                {7f, 3f, 0.0f}
        };
        float[][] leafHeight = new float[][]{
                {5.0f, 4.0f},
                {5.0f, 4.0f},
                {5.0f, 4.0f},
                {5.0f, 4.0f},
                {5.0f, 4.0f},
                {5.0f, 12.0f}
        };
        float[] leafInnerStartHeight = new float[]{42.0f, 50.0f, 58f, 66f, 72f, 80f};
        float[] leafOuterStartHeight = new float[]{40.5f, 49f, 57f, 65f, 71f, 79f};

        return pineGenerator(trunkSections, trunkRadius, trunkHeight, trunkNGon,
                leafSections, leafSubSections, leafNGons, leafRadius,
                leafHeight, leafInnerStartHeight, leafOuterStartHeight,
                trunk2, leaves2);
    }

    public static RawModel generatorPineTree(){
        int trunkSections = 2;
        float[] trunkRadius = new float[]{1.5f, 1.0f, 0.8f};
        float[] trunkHeight = new float[]{2.5f, 10.0f};
        int trunkNGon = 6;

        int leafSections = 4;
        int leafSubSections = 2;
        int[] leafNGons = new int[]{8, 6, 4, 4};
        float[][] leafRadius = new float[][]{
                {8.5f, 5.0f, 3.5f},
                {6.5f, 3.5f, 2.5f},
                {5.0f, 2.5f, 1.5f},
                {3.0f, 1.5f, 0.0f}
        };
        float[][] leafHeight = new float[][]{
                {4.0f, 2.5f},
                {4.0f, 2.5f},
                {4.0f, 2.5f},
                {2.5f, 5.5f}
        };
        float[] leafInnerStartHeight = new float[]{12.0f, 16.0f, 22.5f, 28f};
        float[] leafOuterStartHeight = new float[]{10.5f, 16.5f, 22.0f, 27.5f};

        return pineGenerator(trunkSections, trunkRadius, trunkHeight, trunkNGon,
                leafSections, leafSubSections, leafNGons, leafRadius,
                leafHeight, leafInnerStartHeight, leafOuterStartHeight,
                trunk1, leaves1);
    }

    private static RawModel pineGenerator(int trunkSections, float[] trunkRadius, float[] trunkHeight, int trunkNGon,
                                          int leafSections, int leafSubSections, int[] leafNGons, float[][] leafRadius,
                                          float[][] leafHeight, float[] leafInnerStartHeight, float[] leafOuterStartHeight,
                                          Vector3f trunkColor, Vector3f leafColor){

        ModelHelper modelHelper = new ModelHelper();

        // Trunk
        float ty = -0.5f;
        for(int s=0; s<trunkSections; s++){
            if(s > 0) ty += trunkHeight[s-1];
            for(int i=0; i<trunkNGon; i++){
                double a0 = (i/(double)trunkNGon) * Math.PI * 2;
                double a1 = ((i+1)%trunkNGon)/(double)trunkNGon * Math.PI * 2;

                Vector3f v0 = new Vector3f((float) Math.sin(a0) * trunkRadius[s], ty, (float) Math.cos(a0) * trunkRadius[s]);
                Vector3f v1 = new Vector3f((float) Math.sin(a1) * trunkRadius[s], ty, (float) Math.cos(a1) * trunkRadius[s]);
                Vector3f v2 = new Vector3f((float) Math.sin(a1) * trunkRadius[s+1], ty + trunkHeight[s], (float) Math.cos(a1) * trunkRadius[s+1]);
                Vector3f v3 = new Vector3f((float) Math.sin(a0) * trunkRadius[s+1], ty + trunkHeight[s], (float) Math.cos(a0) * trunkRadius[s+1]);

                modelHelper.addTriangle(v0, v1, v2, trunkColor);
                modelHelper.addTriangle(v2, v3, v0, trunkColor);
            }
        }

        // Leaves
        for(int s=0; s<leafSections; s++){
            for(int b=0; b<leafSubSections; b++){
                for(int i=0; i<leafNGons[s]; i++){
                    double a0 = (i/(double)leafNGons[s]) * Math.PI * 2;
                    double a1 = ((i+1)%leafNGons[s])/(double)leafNGons[s] * Math.PI * 2;

                    float y = leafOuterStartHeight[s];
                    if(b > 0) y += leafHeight[s][b-1];

                    Vector3f v0 = new Vector3f((float) Math.sin(a0) * leafRadius[s][b], y, (float) Math.cos(a0) * leafRadius[s][b]);
                    Vector3f v1 = new Vector3f((float) Math.sin(a1) * leafRadius[s][b], y, (float) Math.cos(a1) * leafRadius[s][b]);

                    if(!(s == leafSections-1 && b == leafSubSections-1)) {
                        Vector3f v2 = new Vector3f((float) Math.sin(a1) * leafRadius[s][b + 1], y + leafHeight[s][b], (float) Math.cos(a1) * leafRadius[s][b + 1]);
                        Vector3f v3 = new Vector3f((float) Math.sin(a0) * leafRadius[s][b + 1], y + leafHeight[s][b], (float) Math.cos(a0) * leafRadius[s][b + 1]);

                        modelHelper.addTriangle(v0, v1, v2, leafColor);
                        modelHelper.addTriangle(v2, v3, v0, leafColor);
                    }else{
                        Vector3f v4 = new Vector3f(0, y + leafHeight[s][b], 0);

                        modelHelper.addTriangle(v0, v1, v4, leafColor);
                    }

                    // Under-leaf
                    if(b == 0){
                        Vector3f v4 = new Vector3f(0, leafInnerStartHeight[s], 0);

                        Vector3f normal = Vector3f.cross(modelHelper.getNormal(v0, v1, v4), new Vector3f(0, 1, 1), null);
                        normal.normalise();

                        modelHelper.addTriangle(v4, v1, v0, leafColor, normal);
                    }

                }
            }
        }

        return modelHelper.generateRawModel();
    }
}
