package org.ninthworld.polygons.terrain;

import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;
import org.ninthworld.polygons.model.ModelManager;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TerrainGenerator {

    private static final int SEED = 11235813;

    private SimplexNoiseOctave[] noise;

    public TerrainGenerator(){
        noise = new SimplexNoiseOctave[8];
        for(int i=0; i<noise.length; i++){
            noise[i] = new SimplexNoiseOctave(SEED + i);
        }
    }

    public double getHeightAt(double x, double y){

        double biome = sumOctave(noise[0], 16, x, y, 0.05, 0.003, 0.0, 1.0);

        double hBiome0 = sumOctave(noise[1], 16, x, y, 0.15, 0.002, 0.0, 8.0);
        double hBiome1 = sumOctave(noise[2], 16, x, y, 0.45, 0.01, 0.0, 96.0);


        return hBiome0 * biome + hBiome1 * (1 - biome);
    }

    public String getEntityAt(double x, double y){

        double biome = sumOctave(noise[0], 16, x, y, 0.05, 0.003, 0.0, 1.0);
        double entityChance = sumOctave(noise[4], 8, x, y, 0.9, 1, 0.0, 1.0);
        double height = getHeightAt(x, y);

        if(height > Chunk.WATER_LEVEL) {
            if (biome > 0.5 && entityChance > 0.65 && height < 40) {
                return ModelManager.PINE_TREE;
            } else if (biome > 0.3 && entityChance > 0.7) {
                return ModelManager.REDWOOD_TREE;
            } else {
                return null;
            }
        }else{
            return null;
        }
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
