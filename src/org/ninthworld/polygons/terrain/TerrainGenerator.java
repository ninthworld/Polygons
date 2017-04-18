package org.ninthworld.polygons.terrain;

import org.ninthworld.polygons.chunk.Chunk;
import org.ninthworld.polygons.helper.MathHelper;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;
import org.ninthworld.polygons.model.ModelManager;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TerrainGenerator {

    private static final int SEED = 11235813;

    private SimplexNoiseOctave[] noise;

    public TerrainGenerator(){
        noise = new SimplexNoiseOctave[16];
        for(int i=0; i<noise.length; i++){
            noise[i] = new SimplexNoiseOctave(SEED + i);
        }
    }

    public double[] getBiomeHeights(double x, double y){

        double parentBiome = sumOctave(noise[0], 16, x, y, 0.10, 0.003, 0.0, 1.0);
        double childBiome1 = sumOctave(noise[1], 16, x, y, 0.10, 0.003, 0.0, 1.0);
        double childBiome2 = sumOctave(noise[2], 4, x, y, 0.01, 0.003, 0.0, 2.0) - 1.0;

        double oceanBiome = sumOctave(noise[8], 16, x, y, 0.55, 0.01, 0.0, 64.0);
        double plainsBiome = sumOctave(noise[9], 16, x, y, 0.40, 0.03, 0.0, 16.0);
        double forestBiome = sumOctave(noise[10], 16, x, y, 0.60, 0.02, 0.0, 64.0);
        double mountainBiome = sumOctave(noise[11], 16, x, y, 0.55, 0.012, 0.0, 96.0);

        double childHeight1 = ((MathHelper.clamp(parentBiome, 0.2f, 1.0f) - 0.2)/0.8);
        double childHeight2 = (1.0 - MathHelper.clamp(childBiome1, 0.0f, 0.7f)/0.7);

        double plainsHeight = (1.0 - MathHelper.clamp(childBiome2, 0.0f, 0.6f)/0.6) * plainsBiome * childHeight2 * childHeight1;
        double forestsHeight = ((MathHelper.clamp(childBiome2, 0.4f, 1.0f) - 0.4)/0.6) * forestBiome * childHeight2 * childHeight1;

        double mountainsHeight = ((MathHelper.clamp(childBiome1, 0.5f, 1.0f) - 0.5)/0.5) * mountainBiome * childHeight1;

        double oceanHeight = (1.0 - MathHelper.clamp(parentBiome, 0.0f, 0.3f)/0.3) * oceanBiome;

        return new double[]{
                oceanHeight,
                mountainsHeight,
                plainsHeight,
                forestsHeight
        };
    }

    public double getHeightAt(double x, double y){
        double[] biomeHeights = getBiomeHeights(x, y);

        return -biomeHeights[0] + biomeHeights[1] + biomeHeights[2] + biomeHeights[3] + 16.0;
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
