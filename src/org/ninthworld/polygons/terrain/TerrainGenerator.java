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

    private SimplexNoiseOctave[] noise;

    private HashMap<String, List<Vector3f>> biomeChunkPoints;

    public TerrainGenerator(){
        biomeChunkPoints = new HashMap<>();
        noise = new SimplexNoiseOctave[16];
        for(int i=0; i<noise.length; i++){
            noise[i] = new SimplexNoiseOctave(SEED + i);
        }
    }

    public double getBiomeHeight(double x, double y){
        return sumOctave(noise[0], 16, x, y, 0.1, 0.001, 0.0, 1.0);
    }

    public double getTemperature(double x, double y){
        return sumOctave(noise[1], 8, x, y, 0.01, 0.001, 0.0, 1.0);
    }

    private static float bcpSD = 8.0f;
    private static int bcpMin = 8;
    public List<Vector3f> getBiomePoints(double x, double y){
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

    public Vector3f getClosestBiomePoint(double x, double y){
        List<Vector3f> biomePoints = getBiomePoints(x, y);

        double pointNoise = sumOctave(noise[1], 8, x, y, 0.3, 0.05, 0.0, 20.0) - 10.0;
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

    public Vector3f getColorAt(double x, double y){
        Vector3f closest = getClosestBiomePoint(x, y);
        double temperature = getTemperature(closest.x, closest.y);

        if(closest.y < 0.4){
            return new Vector3f(0, 0, 1);
        }else{
            if(closest.y > 0.8){
                return new Vector3f(1, 0, 0);
            }else{
                if(temperature > 0.5){
                    return new Vector3f(0, 1, 0);
                }else{
                    return new Vector3f(1, 1, 0);
                }
            }
//            return new Vector3f(closest.y * 0.1f, closest.y, closest.y * 0.2f);
        }
    }

    public double[] getBiomeAmounts(double x, double y){

        double parentBiome = sumOctave(noise[0], 16, x, y, 0.10, 0.003, 0.0, 1.0);
        double childBiome1 = sumOctave(noise[1], 16, x, y, 0.10, 0.003, 0.0, 1.0);
        double childBiome2 = sumOctave(noise[2], 16, x, y, 0.10, 0.003, 0.0, 1.0);

        double childHeight1 = ((MathHelper.clamp(parentBiome, 0.2f, 1.0f) - 0.2)/0.8);
        double childHeight2 = (1.0 - MathHelper.clamp(childBiome1, 0.0f, 0.7f)/0.7);

        double plainsHeight = (1.0 - MathHelper.clamp(childBiome2, 0.0f, 0.6f)/0.6) * childHeight2 * childHeight1;
        double forestsHeight = ((MathHelper.clamp(childBiome2, 0.4f, 1.0f) - 0.4)/0.6) * childHeight2 * childHeight1;

        double mountainsHeight = ((MathHelper.clamp(childBiome1, 0.5f, 1.0f) - 0.5)/0.5) * childHeight1;

        double oceanHeight = (1.0 - MathHelper.clamp(parentBiome, 0.0f, 0.3f)/0.3);

        return new double[]{
                oceanHeight,
                mountainsHeight,
                plainsHeight,
                forestsHeight
        };
    }

    public double[] getBiomeHeights(double x, double y){
//        double[] biomeAmounts = getBiomeAmounts(x, y);
//
//        double oceanBiome = sumOctave(noise[8], 16, x, y, 0.55, 0.01, 0.0, 64.0);
//        double plainsBiome = sumOctave(noise[9], 16, x, y, 0.40, 0.03, 0.0, 16.0);
//        double forestBiome = sumOctave(noise[10], 16, x, y, 0.60, 0.02, 0.0, 64.0);
//        double mountainBiome = sumOctave(noise[11], 16, x, y, 0.55, 0.012, 0.0, 96.0);
//
//        return new double[]{
//                oceanBiome * biomeAmounts[0],
//                mountainBiome * biomeAmounts[1],
//                plainsBiome * biomeAmounts[2],
//                forestBiome * biomeAmounts[3]
//        };

        return new double[4];
    }

    public double getHeightAt(double x, double y){
//        double[] biomeHeights = getBiomeHeights(x, y);
//
//        return -biomeHeights[0] + biomeHeights[1] + biomeHeights[2] + biomeHeights[3] + 16.0;

        Vector3f closest = getClosestBiomePoint(x, y);
        double height = getBiomeHeight(x, y);

//        return (height * 0.9 + closest.y * 0.1) * 40.0;

        double oceanBiome = sumOctave(noise[8], 16, x, y, 0.55, 0.01, 0.0, 1.0);

        if(closest.y < 0.4){
            return height * 40.0 * -oceanBiome;
        }else{
            return height * 40.0;
        }
    }

    public String getEntityAt(double x, double y){
        double[] biomeHeights = getBiomeHeights(x, y);

        int maxHeightIndex = 0;
        for(int i=0; i<biomeHeights.length; i++){
            if(biomeHeights[i] > biomeHeights[maxHeightIndex]) maxHeightIndex = i;
        }

        double entityChance = sumOctave(noise[15], 16, x, y, 0.9, 1, 0.0, 1.0);

        if(maxHeightIndex == 2){
            if(entityChance > 0.62){
                return ModelManager.PINE_TREE;
            }
        }else if(maxHeightIndex == 3){
            if(entityChance > 0.63){
                return ModelManager.REDWOOD_TREE;
            }
        }

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
