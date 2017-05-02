package org.ninthworld.polygons.terrain;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.biome.Biome;
import org.ninthworld.polygons.biome.BiomeManager;
import org.ninthworld.polygons.helper.MathHelper;
import org.ninthworld.polygons.helper.SimplexNoiseOctave;

import java.util.*;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class TerrainGenerator {

    private static final int SEED = 11235813;
    public static final int LOCAL_GRID_SIZE = 64;

    private SimplexNoiseOctave gridNoiseX;
    private SimplexNoiseOctave gridNoiseZ;

    private BiomeManager biomeManager;

    public TerrainGenerator(BiomeManager biomeManager){
        this.biomeManager = biomeManager;

        int i = 0;
        gridNoiseX = new SimplexNoiseOctave(SEED + (i++));
        gridNoiseZ = new SimplexNoiseOctave(SEED + (i++));
    }

    public Seed getLocalSeed(int gx, int gz){
        double gridX = gridNoiseX.noise(gx, gz) * (LOCAL_GRID_SIZE/2f);
        double gridZ = gridNoiseZ.noise(gx, gz) * (LOCAL_GRID_SIZE/2f);

        return new Seed(new Vector3f((float) gridX, 0f, (float) gridZ), biomeManager.getBiomeAt(gridX + gx * LOCAL_GRID_SIZE, gridZ + gz * LOCAL_GRID_SIZE));
    }

    public HashMap<Biome, Double> getBiomesAt(double x, double z){
        Vector3f pos = new Vector3f((float) x, 0, (float) z);
        Vector3f localGridPos = new Vector3f((float) Math.floor(x/((double) LOCAL_GRID_SIZE)), 0f, (float) Math.floor(z/((double) LOCAL_GRID_SIZE)));

        int r = 2;
        List<Seed> seeds = new ArrayList<>();
        for(int i=-r; i<=r; i++){
            for(int j=-r; j<=r; j++){
                Vector3f localPos = new Vector3f(localGridPos.x + i, 0, localGridPos.z + j);
                Seed localSeed = getLocalSeed((int) localPos.x, (int) localPos.z);
                Vector3f edge = biomeManager.getBiomeEdge(x, z);
                Vector3f globalSeedPos = new Vector3f(
                        localSeed.pos.x + localPos.x * LOCAL_GRID_SIZE + edge.x,
                        0f,
                        localSeed.pos.z + localPos.z * LOCAL_GRID_SIZE + edge.z
                );

                if(Vector3f.sub(globalSeedPos, pos, null).length() <= LOCAL_GRID_SIZE * Math.sqrt(3)){
                    seeds.add(new Seed(globalSeedPos, localSeed.biome));
                }
            }
        }

        HashMap<Biome, Double> biomes = new HashMap<>();
        for(Seed seedA : seeds){
            Vector3f subAPos = Vector3f.sub(seedA.pos, pos, null);
            List<Double> clamps = new ArrayList<>();
            for(Seed seedB : seeds){
                if(seedA != seedB){
                    Vector3f ab = Vector3f.sub(seedA.pos, seedB.pos, null);
                    double dotAB = Vector3f.dot(subAPos, ab)/Math.pow(ab.length(), 2);
                    clamps.add(MathHelper.clamp(dotAB, 0, 1));
                }
            }

            double min = 1;
            for(Double clamp : clamps){
                min = Math.min(min, 1 - clamp);
            }

            Biome biome = seedA.biome;
            if(biomes.containsKey(biome)){
                double val = biomes.get(biome);
                biomes.put(biome, MathHelper.clamp(val + min, 0, 1));
            }else{
                biomes.put(biome, min);
            }
        }

        return biomes;
    }

    public double getHeightAt(double x, double z){
        HashMap<Biome, Double> biomes = getBiomesAt(x, z);

        double outHeight = 0;
        for(Map.Entry<Biome, Double> set : biomes.entrySet()){
            outHeight += biomeManager.getBiomeHeight(x, z, set.getKey()) * set.getValue();
        }

        return outHeight;
    }

    public Vector3f getColorAt(double x, double z){
        HashMap<Biome, Double> biomes = getBiomesAt(x, z);

        Vector3f color = new Vector3f();
        for(Map.Entry<Biome, Double> set : biomes.entrySet()){
            Color col = Biome.getColor(set.getKey());
            Vector3f.add((Vector3f)(new Vector3f(col.getRed()/255f, col.getGreen()/255f, col.getBlue()/255f).scale(set.getValue().floatValue())), color, color);
        }

        return color;
    }

    public String getEntityAt(double x, double z){
        return biomeManager.getEntityAt(x, z);
    }
}

class Seed {
    public Vector3f pos;
    public Biome biome;
    public Seed(Vector3f pos, Biome biome){
        this.pos = pos;
        this.biome = biome;
    }
}