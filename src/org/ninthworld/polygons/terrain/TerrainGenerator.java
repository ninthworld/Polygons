package org.ninthworld.polygons.terrain;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.helper.MathHelper;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;
import org.ninthworld.polygons.helper.Vector2i;
import org.ninthworld.polygons.model.ModelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TerrainGenerator {

    public static final int BIOME_CHUNK_SIZE = Chunk.CHUNK_SIZE * 16;
    private static final int SEED = 11235813;

    private SimplexNoiseOctave biomeNoise;
    private SimplexNoiseOctave biomeEdgeNoise;
    private SimplexNoiseOctave temperatureNoise;
    private SimplexNoiseOctave heightNoise;

    private HashMap<String, List<Vector3f>> biomeChunkPoints;

    public TerrainGenerator(){
        biomeChunkPoints = new HashMap<>();

        biomeNoise = new SimplexNoiseOctave(SEED + 1);
        biomeEdgeNoise = new SimplexNoiseOctave(SEED + 2);
        temperatureNoise = new SimplexNoiseOctave(SEED + 3);
        heightNoise = new SimplexNoiseOctave(SEED + 4);
    }

    public double getBiomeHeight(double x, double y){
        return sumOctave(biomeNoise, 16, x, y, 0.1, 0.001, 0.0, 1.0);
    }

    public double getTemperature(double x, double y){
        return sumOctave(temperatureNoise, 8, x, y, 0.01, 0.001, 0.0, 1.0);
    }

    private static float bcpSD = 8.0f;
    private static int bcpMin = 8;
    private List<Vector3f> getBiomePoints(double x, double y){
        List<Vector3f> biomePoints = new ArrayList<>();
        Vector2i biomeChunkPosOrigin = new Vector2i((int)Math.floor(x/(double) BIOME_CHUNK_SIZE), (int)Math.floor(y/(double)BIOME_CHUNK_SIZE));
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                Vector2i biomeChunkPos = new Vector2i(biomeChunkPosOrigin.x + i, biomeChunkPosOrigin.y + j);

                if(biomeChunkPoints.containsKey(biomeChunkPos.toHashString())){
                    biomePoints.addAll(biomeChunkPoints.get(biomeChunkPos.toHashString()));
                }else {
                    List<Vector3f> localBiomePoints = new ArrayList<>();
                    Random randX = new Random(810031 + biomeChunkPos.x * 541343);
                    Random randY = new Random(130018 + biomeChunkPos.y * 391247);

                    int numPoints = (int) Math.floor(bcpSD * (randX.nextFloat() + randY.nextFloat()) / 2.0f) + bcpMin;
                    for (int p = 0; p < numPoints; p++) {
                        Vector3f pointPos = new Vector3f((biomeChunkPos.x + randX.nextFloat()) * BIOME_CHUNK_SIZE, 0f, (biomeChunkPos.y + randY.nextFloat()) * BIOME_CHUNK_SIZE);
                        pointPos.y = (float) getBiomeHeight(pointPos.x, pointPos.z);
                        localBiomePoints.add(pointPos);
                    }

                    biomeChunkPoints.put(biomeChunkPos.toHashString(), localBiomePoints);
                    biomePoints.addAll(localBiomePoints);
                }
            }
        }

        return biomePoints;
    }

    private Vector3f getClosestBiomePoint(double x, double y){
        List<Vector3f> biomePoints = getBiomePoints(x, y);

        double pointNoise = sumOctave(biomeEdgeNoise, 8, x, y, 0.3, 0.05, 0.0, 20.0) - 10.0;
        double closestDist = -1;
        Vector3f closest = null;
        for(Vector3f point : biomePoints){
            double dist = Math.sqrt(Math.pow(x - (point.x + pointNoise), 2) + Math.pow(y - (point.z + pointNoise), 2));
            if(closest == null || closestDist < 0 || dist < closestDist){
                closestDist = dist;
                closest = point;
            }
        }

        return closest;
    }

    public static final int OCEAN = 0;
    public static final int MOUNTAIN = 1;
    public static final int PLAINS = 2;
    public static final int DESERT = 3;

    public int getBiomeAt(double x, double y){
        Vector3f closest = getClosestBiomePoint(x, y);
        double temperature = getTemperature(closest.x, closest.y);

        if(closest.y < 0.4){
            return OCEAN;
        }else{
            if(closest.y > 0.8){
                return MOUNTAIN;
            }else{
                if(temperature > 0.5){
                    return PLAINS;
                }else{
                    return DESERT;
                }
            }
        }
    }

    public double getHeightAt(double x, double y){
        int biome = getBiomeAt(x, y);

        double height = 0;
        switch(biome){
            case OCEAN:
                height = -sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 16.0); break;
            case MOUNTAIN:
                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 128.0); break;
            case PLAINS:
                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 24.0); break;
            case DESERT:
                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 32.0); break;
        }

        return height * getBiomeHeight(x, y) + 16.0;
    }

    public double getSmoothHeightAt(double x, double y){
//        double[] kernel = new double[]{
//                0.077847, 0.123317, 0.077847,
//                0.123317, 0.195346, 0.123317,
//                0.077847, 0.123317, 0.077847
//        };
//        double height = 0;
//        int r = 3;
//        for(int i=-r; i<=r; i++){
//            for(int j=-r; j<=r; j++){
//                height += getHeightAt(x + i, y + j);// * kernel[(i+1) + (j+1)*(int)Math.floor(r+0.5*2)];
//            }
//        }
//
//        return height/((r+r+1)*(r+r+1));
        return getHeightAt(x, y);
    }

    public String getEntityAt(double x, double y){
//        double[] biomeHeights = getBiomeHeights(x, y);
//
//        int maxHeightIndex = 0;
//        for(int i=0; i<biomeHeights.length; i++){
//            if(biomeHeights[i] > biomeHeights[maxHeightIndex]) maxHeightIndex = i;
//        }
//
//        double entityChance = sumOctave(noise[15], 16, x, y, 0.9, 1, 0.0, 1.0);
//
//        if(maxHeightIndex == 2){
//            if(entityChance > 0.62){
//                return ModelManager.PINE_TREE;
//            }
//        }else if(maxHeightIndex == 3){
//            if(entityChance > 0.63){
//                return ModelManager.REDWOOD_TREE;
//            }
//        }

        return null;
    }

    private double sumOctave(SimplexNoiseOctave noise, int numIterations, double x, double y, double persistence, double scale, double low, double high){
        double maxAmp = 0,
                amp = 1,
                freq = scale,
                noiseSum = 0;

        for(int i=0; i<numIterations; ++i){
            noiseSum += noise.noise(x * freq, y * freq) * amp;
            maxAmp += amp;
            amp *= persistence;
            freq *= 2;
        }

        noiseSum /= maxAmp;

        return noiseSum * (high - low)/2.0 + (high + low)/2.0;
    }
}
