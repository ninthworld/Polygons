package org.ninthworld.polygons.biome;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.engine.IManager;
import org.ninthworld.polygons.helper.NoiseHelper;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;
import org.ninthworld.polygons.model.ModelManager;

/**
 * Created by NinthWorld on 5/1/2017.
 */
public class BiomeManager implements IManager {

//    private static final int SEED = 92234211;

    private SimplexNoiseOctave elevationNoise;
    private SimplexNoiseOctave temperatureNoise;
    private SimplexNoiseOctave humidityNoise;
    private SimplexNoiseOctave biomeEdgeNoiseX;
    private SimplexNoiseOctave biomeEdgeNoiseZ;
    private SimplexNoiseOctave biomeHeightNoise;
    private SimplexNoiseOctave biomeEntityNoise;

    public BiomeManager(int SEED){
        int i = 0;
        elevationNoise = new SimplexNoiseOctave(SEED + (i++));
        temperatureNoise = new SimplexNoiseOctave(SEED + (i++));
        humidityNoise = new SimplexNoiseOctave(SEED + (i++));
        biomeEdgeNoiseX = new SimplexNoiseOctave(SEED + (i++));
        biomeEdgeNoiseZ = new SimplexNoiseOctave(SEED + (i++));
        biomeHeightNoise = new SimplexNoiseOctave(SEED + (i++));
        biomeEntityNoise = new SimplexNoiseOctave(SEED + (i++));
    }

    @Override
    public void initialize() {

    }

    @Override
    public void cleanUp() {

    }

    public String getEntityAt(double x, double z){
        Biome biome = getBiomeAt(x, z);

        switch (biome){
            case TAIGA:
                if(NoiseHelper.sumOctave(biomeEntityNoise, 1, x, z, 0.0, 1.0, 0.0, 1.0) > 0.95) return ModelManager.PINE_TREE;
                break;
            case TAIGA_HILLS:
                if(NoiseHelper.sumOctave(biomeEntityNoise, 1, x, z, 0.0, 1.0, 0.0, 1.0) > 0.98) return ModelManager.REDWOOD_TREE;
                break;
        }
        return "";
    }

    public Biome getBiomeAt(double x, double z){
        double elevation = getElevation(x, z);
        int temperature = getTemperature(x, z);
        int humidity = getHumidity(x, z);

        if(elevation > 0.4){
            return Biome.getMapBiome(humidity, temperature);
        }else{
            if(temperature >= Biome.waterTemperature){
                return Biome.OCEAN;
            }else{
                return Biome.ICE_OCEAN;
            }
        }
    }

    public double getBiomeHeight(double x, double z, Biome biome){
        int iterations = 16;
        double persistence, scale, offset, amp;

        switch(biome){
            case TAIGA:
                persistence = 0.4; scale = 0.008; offset = 0.0; amp = 16.0; break;
            case JUNGLE:
                persistence = 0.4; scale = 0.01; offset = 0.0; amp = 16.0; break;
            case SWAMP:
                persistence = 0.4; scale = 0.01; offset = 0.0; amp = 6.0; break;
            case TUNDRA:
                persistence = 0.3; scale = 0.008; offset = 0.0; amp = 4.0; break;
            case FOREST:
                persistence = 0.4; scale = 0.01; offset = 0.0; amp = 12.0; break;
            case PLAINS:
                persistence = 0.4; scale = 0.01; offset = 0.0; amp = 4.0; break;
            case DESERT:
                persistence = 0.4; scale = 0.01; offset = 0.0; amp = 8.0; break;
            case OCEAN:
                persistence = 0.4; scale = 0.01; offset = -16.0; amp = 16.0; break;
            case ICE_OCEAN:
                persistence = 0.1; scale = 0.005; offset = 0.0; amp = 1.0; break;
            case TAIGA_HILLS:
            case JUNGLE_HILLS:
            case TUNDRA_HILLS:
            case FOREST_HILLS:
            case DESERT_HILLS:
                persistence = 0.45; scale = 0.008; offset = 0.0; amp = 32.0; break;
            case EXTREME_HILLS:
                persistence = 0.45; scale = 0.005; offset = 0.0; amp = 64.0; break;
            default:
                persistence = 0.0; scale = 0.01; offset = 0.0; amp = 16.0; break;
        }

        return NoiseHelper.sumOctave(biomeHeightNoise, iterations, x, z, persistence, scale, 0, amp) + offset;
    }

    public Vector3f getBiomeEdge(double x, double z){
        double edgeX = NoiseHelper.sumOctave(biomeEdgeNoiseX, 1, x, z, 0, 0.02, 0, 16);
        double edgeZ = NoiseHelper.sumOctave(biomeEdgeNoiseZ, 1, x, z, 0, 0.02, 0, 16);
        return new Vector3f((float) edgeX, 0f, (float) edgeZ);
    }

    private double getElevation(double x, double z){
        return NoiseHelper.sumOctave(elevationNoise, 2, x, z, 0.0, 0.0008, 0, 1);
    }

    private int getTemperature(double x, double z){
        return (int) Math.floor(NoiseHelper.sumOctave(temperatureNoise, 2, x, z, 0.0, 0.0008, 0, Biome.biomeMap[0].length));
    }

    private int getHumidity(double x, double z){
        return (int) Math.floor(NoiseHelper.sumOctave(humidityNoise, 2, x, z, 0.0, 0.0008, 0, Biome.biomeMap.length));
    }
}
