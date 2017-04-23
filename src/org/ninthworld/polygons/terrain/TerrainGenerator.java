package org.ninthworld.polygons.terrain;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.helper.MathHelper;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;
import org.ninthworld.polygons.helper.Vector2i;
import org.ninthworld.polygons.model.ModelManager;

import java.util.*;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TerrainGenerator {

    private static final int SEED = 11235813;

    public static final int LOCAL_GRID_SIZE = 64;

    public static final int numBiomes = 10;
    public static final int BIOME_NULL = 0;
    public static final int BIOME_TAIGA = 1;
    public static final int BIOME_JUNGLE = 2;
    public static final int BIOME_SWAMP = 3;
    public static final int BIOME_TUNDRA = 4;
    public static final int BIOME_FOREST = 5;
    public static final int BIOME_PLAINS = 6;
    public static final int BIOME_DESERT = 7;
    public static final int BIOME_OCEAN = 8;
    public static final int BIOME_ICE_OCEAN = 9;

    public static final int[][] biomeMap = new int[][]{
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {4, 4, 4, 6, 6, 6, 7, 7, 7},
                {4, 4, 4, 6, 6, 6, 7, 7, 7},
                {4, 4, 4, 6, 6, 6, 7, 7, 7}
    };

    public static Vector3f getBiomeColor(int biome){
        switch(biome){
            case BIOME_TAIGA:
                return new Vector3f(0x00/255f, 0x96/255f, 0x88/255f);
            case BIOME_JUNGLE:
                return new Vector3f(0xAE/255f, 0xEA/255f, 0x00/255f);
            case BIOME_SWAMP:
                return new Vector3f(0x33/255f, 0x69/255f, 0x1E/255f);
            case BIOME_TUNDRA:
                return new Vector3f(0xEC/255f, 0xEF/255f, 0xF1/255f);
            case BIOME_FOREST:
                return new Vector3f(0x4C/255f, 0xAF/255f, 0x50/255f);
            case BIOME_PLAINS:
                return new Vector3f(0x8B/255f, 0xC3/255f, 0x4A/255f);
            case BIOME_DESERT:
                return new Vector3f(0xFF/255f, 0xEB/255f, 0x3B/255f);
            case BIOME_OCEAN:
                return new Vector3f(0x21/255f, 0x96/255f, 0xF3/255f);
            case BIOME_ICE_OCEAN:
                return new Vector3f(0x64/255f, 0xB5/255f, 0xF6/255f);
            default:
                return new Vector3f(0, 0, 0);
        }
    }

    public static Vector3f getBiomeColorMulti(double[] biomes){
        Vector3f outColor = new Vector3f(0, 0, 0);
        for(int i=0; i<biomes.length; i++){
            Vector3f color = getBiomeColor(i);
            outColor.x += Math.min(1f, color.x * biomes[i]);
            outColor.y += Math.min(1f, color.y * biomes[i]);
            outColor.z += Math.min(1f, color.z * biomes[i]);
        }
        return outColor;
    }

    private SimplexNoiseOctave jitterNoiseX;
    private SimplexNoiseOctave jitterNoiseZ;
    private SimplexNoiseOctave elevationNoise;
    private SimplexNoiseOctave temperatureNoise;
    private SimplexNoiseOctave humidityNoise;
    private SimplexNoiseOctave distortionNoiseX;
    private SimplexNoiseOctave distortionNoiseZ;
    private SimplexNoiseOctave biomeHeightNoise;

    public TerrainGenerator(){
        int i = 0;
        jitterNoiseX = new SimplexNoiseOctave(SEED + (i++));
        jitterNoiseZ = new SimplexNoiseOctave(SEED + (i++));
        elevationNoise = new SimplexNoiseOctave(SEED + (i++));
        temperatureNoise = new SimplexNoiseOctave(SEED + (i++));
        humidityNoise = new SimplexNoiseOctave(SEED + (i++));
        distortionNoiseX = new SimplexNoiseOctave(SEED + (i++));
        distortionNoiseZ = new SimplexNoiseOctave(SEED + (i++));
        biomeHeightNoise = new SimplexNoiseOctave(SEED + (i++));
    }

    public double getElevation(double x, double z){
        return sumOctave(elevationNoise, 2, x, z, 0.0, 0.0005, 0, 1);
    }

    public int getTemperature(double x, double z){
        return (int) Math.floor(sumOctave(temperatureNoise, 2, x, z, 0.0, 0.0004, 0, biomeMap[0].length));
    }

    public int getHumidity(double x, double z){
        return (int) Math.floor(sumOctave(humidityNoise, 2, x, z, 0.0, 0.0004, 0, biomeMap.length));
    }

    public Vector3f getDistortion(double x, double z){
        return new Vector3f(
                (float) sumOctave(distortionNoiseX, 1, x, z, 0, 0.02, 0, 16),
                0,
                (float) sumOctave(distortionNoiseZ, 1, x, z, 0, 0.02, 0, 16)
        );
    }

    public double getBiomeHeight(double x, double z, int biome){
        int iterations = 16;
        double persistence, scale, low, high;
        switch(biome){
            case BIOME_TAIGA:
                persistence = 0.4; scale = 0.008; low = 0.0; high = 32.0; break;
            case BIOME_JUNGLE:
                persistence = 0.3; scale = 0.01; low = 0.0; high = 20.0; break;
            case BIOME_SWAMP:
            case BIOME_TUNDRA:
                persistence = 0.01; scale = 0.001; low = 0.0; high = 4.0; break;
            case BIOME_FOREST:
            case BIOME_PLAINS:
            case BIOME_DESERT:
                persistence = 0.05; scale = 0.005; low = 0.0; high = 8.0; break;
            case BIOME_OCEAN:
            case BIOME_ICE_OCEAN:
                persistence = 0.2; scale = 0.01; low = -16.0; high = 16.0; break;
            default:
                persistence = 0.0; scale = 0.01; low = 0.0; high = 16.0; break;
        }

        return sumOctave(biomeHeightNoise, iterations, x, z, persistence, scale, 0, high) + low;
    }

    private int getBiome(double x, double z){
        double elevation = getElevation(x, z);
        int temperature = getTemperature(x, z);
        int humidity = getHumidity(x, z);

        if(elevation > 0.4){
            return biomeMap[humidity][temperature];
        }else{
            if(temperature > 2){
                return BIOME_OCEAN;
            }else{
                return BIOME_ICE_OCEAN;
            }
        }
    }

    public GridSeed getLocalSeed(int x, int z){
        Vector3f relPos = new Vector3f(
                (float) jitterNoiseX.noise(x, z) * (LOCAL_GRID_SIZE/2f),
                0f,
                (float) jitterNoiseZ.noise(x, z) * (LOCAL_GRID_SIZE/2f)
        );

        int biome = getBiome(relPos.x + x * LOCAL_GRID_SIZE, relPos.z + z * LOCAL_GRID_SIZE);

        return new GridSeed(relPos, biome);
    }

    public int getBiomeAt(double x, double z){
        Vector3f localGridPos = new Vector3f((float) Math.floor(x/((double) LOCAL_GRID_SIZE)), 0f, (float) Math.floor(z/((double) LOCAL_GRID_SIZE)));

        GridSeed[] seeds = new GridSeed[16];
        for(int i=-2; i<2; i++){
            for(int j=-2; j<2; j++){
                Vector3f localPos = new Vector3f(localGridPos.x + i, 0, localGridPos.z + j);
                GridSeed localSeed = getLocalSeed((int) localPos.x, (int) localPos.z);
                Vector3f distortion = getDistortion(x, z);
                Vector3f globalSeedPos = new Vector3f(
                        localSeed.pos.x + localPos.x * LOCAL_GRID_SIZE + distortion.x,
                        0,
                        localSeed.pos.z + localPos.z * LOCAL_GRID_SIZE + distortion.z
                );

                seeds[(i+2)*4+(j+2)] = new GridSeed(globalSeedPos, localSeed.biome);
            }
        }

        double minDist = LOCAL_GRID_SIZE * 2.0;
        int minBiome = BIOME_NULL;
        for(int i=0; i<seeds.length; i++){
            double dist = Math.sqrt(Math.pow(x - seeds[i].pos.x, 2) + Math.pow(z - seeds[i].pos.z, 2));
            if(dist < minDist){
                minDist = dist;
                minBiome = seeds[i].biome;
            }
        }

        return minBiome;
    }

    public double[] getBiomeAtSmooth(double x, double z){
        double[] biomes = new double[numBiomes];
        double mod = 1/81.0;
        for(int s=4, i=-s; i<s+1; i++){
            for(int j=-s; j<s+1; j++){
                int biome = getBiomeAt(x + i, z + j);
                biomes[biome] += mod;
            }
        }

        return biomes;
    }

    public double getHeightAt(double x, double z){
//        int biome = getBiomeAt(x, z);
//        double height = getBiomeHeight(x, z, biome);

        double[] biomes = getBiomeAtSmooth(x, z);

        double outHeight = 0;
        for(int i=0; i<biomes.length; i++){
            outHeight += getBiomeHeight(x, z, i) * biomes[i];
        }

        return outHeight;
    }

//    private static float bcpSD = 8.0f;
//    private static int bcpMin = 8;
//    private List<Vector3f> getBiomePoints(double x, double y){
//        List<Vector3f> biomePoints = new ArrayList<>();
//        Vector2i biomeChunkPosOrigin = new Vector2i((int)Math.floor(x/(double) BIOME_CHUNK_SIZE), (int)Math.floor(y/(double)BIOME_CHUNK_SIZE));
//        for(int i=-1; i<2; i++){
//            for(int j=-1; j<2; j++){
//                Vector2i biomeChunkPos = new Vector2i(biomeChunkPosOrigin.x + i, biomeChunkPosOrigin.y + j);
//
//                if(biomeChunkPoints.containsKey(biomeChunkPos.toHashString())){
//                    biomePoints.addAll(biomeChunkPoints.get(biomeChunkPos.toHashString()));
//                }else {
//                    List<Vector3f> localBiomePoints = new ArrayList<>();
//                    Random randX = new Random(810031 + biomeChunkPos.x * 541343);
//                    Random randY = new Random(130018 + biomeChunkPos.y * 391247);
//
//                    int numPoints = (int) Math.floor(bcpSD * (randX.nextFloat() + randY.nextFloat()) / 2.0f) + bcpMin;
//                    for (int p = 0; p < numPoints; p++) {
//                        Vector3f pointPos = new Vector3f((biomeChunkPos.x + randX.nextFloat()) * BIOME_CHUNK_SIZE, 0f, (biomeChunkPos.y + randY.nextFloat()) * BIOME_CHUNK_SIZE);
//                        pointPos.y = (float) getBiomeHeight(pointPos.x, pointPos.z);
//                        localBiomePoints.add(pointPos);
//                    }
//
//                    biomeChunkPoints.put(biomeChunkPos.toHashString(), localBiomePoints);
//                    biomePoints.addAll(localBiomePoints);
//                }
//            }
//        }
//
//        return biomePoints;
//    }
//
//    private Vector3f getClosestBiomePoint(double x, double y){
//        List<Vector3f> biomePoints = getBiomePoints(x, y);
//
//        double pointNoise = sumOctave(biomeEdgeNoise, 8, x, y, 0.3, 0.05, 0.0, 20.0) - 10.0;
//        double closestDist = -1;
//        Vector3f closest = null;
//        for(Vector3f point : biomePoints){
//            double dist = Math.sqrt(Math.pow(x - (point.x + pointNoise), 2) + Math.pow(y - (point.z + pointNoise), 2));
//            if(closest == null || closestDist < 0 || dist < closestDist){
//                closestDist = dist;
//                closest = point;
//            }
//        }
//
//        return closest;
//    }
//
//    public static final int OCEAN = 0;
//    public static final int MOUNTAIN = 1;
//    public static final int PLAINS = 2;
//    public static final int DESERT = 3;
//
//    public int getBiomeAt(double x, double y){
//        Vector3f closest = getClosestBiomePoint(x, y);
//        double temperature = getTemperature(closest.x, closest.y);
//
//        if(closest.y < 0.4){
//            return OCEAN;
//        }else{
//            if(closest.y > 0.8){
//                return MOUNTAIN;
//            }else{
//                if(temperature > 0.5){
//                    return PLAINS;
//                }else{
//                    return DESERT;
//                }
//            }
//        }
//    }
//
//    public double getHeightAt(double x, double y){
//        int biome = getBiomeAt(x, y);
//
//        double height = 0;
//        switch(biome){
//            case OCEAN:
//                height = -sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 16.0); break;
//            case MOUNTAIN:
//                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 128.0); break;
//            case PLAINS:
//                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 24.0); break;
//            case DESERT:
//                height = sumOctave(heightNoise, 16, x, y, 0.4, 0.01, 0.0, 32.0); break;
//        }
//
//        return height * getBiomeHeight(x, y) + 16.0;
//    }
//
//    public double getSmoothHeightAt(double x, double y){
////        double[] kernel = new double[]{
////                0.077847, 0.123317, 0.077847,
////                0.123317, 0.195346, 0.123317,
////                0.077847, 0.123317, 0.077847
////        };
////        double height = 0;
////        int r = 3;
////        for(int i=-r; i<=r; i++){
////            for(int j=-r; j<=r; j++){
////                height += getHeightAt(x + i, y + j);// * kernel[(i+1) + (j+1)*(int)Math.floor(r+0.5*2)];
////            }
////        }
////
////        return height/((r+r+1)*(r+r+1));
//        return getHeightAt(x, y);
//    }
//
//    public String getEntityAt(double x, double y){
////        double[] biomeHeights = getBiomeHeights(x, y);
////
////        int maxHeightIndex = 0;
////        for(int i=0; i<biomeHeights.length; i++){
////            if(biomeHeights[i] > biomeHeights[maxHeightIndex]) maxHeightIndex = i;
////        }
////
////        double entityChance = sumOctave(noise[15], 16, x, y, 0.9, 1, 0.0, 1.0);
////
////        if(maxHeightIndex == 2){
////            if(entityChance > 0.62){
////                return ModelManager.PINE_TREE;
////            }
////        }else if(maxHeightIndex == 3){
////            if(entityChance > 0.63){
////                return ModelManager.REDWOOD_TREE;
////            }
////        }
//
//        return null;
//    }

    private static double sumOctave(SimplexNoiseOctave noise, int numIterations, double x, double y, double persistence, double scale, double low, double high){
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

    class GridSeed {
        public Vector3f pos;
        public int biome;

        public GridSeed(Vector3f pos, int biome){
            this.pos = pos;
            this.biome = biome;
        }
    }
}
