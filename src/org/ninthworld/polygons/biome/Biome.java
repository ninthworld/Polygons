package org.ninthworld.polygons.biome;

import org.lwjgl.util.Color;

/**
 * Created by NinthWorld on 5/1/2017.
 */
public enum Biome {

    TAIGA, TAIGA_HILLS, JUNGLE, JUNGLE_HILLS, SWAMP, TUNDRA, TUNDRA_HILLS, FOREST, FOREST_HILLS, PLAINS, DESERT, DESERT_HILLS, EXTREME_HILLS, OCEAN, ICE_OCEAN;

    public static Color getColor(Biome b){
        switch(b){
            case TAIGA:
            case TAIGA_HILLS:
                return new Color(0x30, 0xCC, 0x68);
            case JUNGLE:
            case JUNGLE_HILLS:
                return new Color(0xAE, 0xEA, 0x00);
            case SWAMP:
                return new Color(0x33, 0x69, 0x1E);
            case TUNDRA:
            case TUNDRA_HILLS:
                return new Color(0xEC, 0xEF, 0xF1);
            case FOREST:
            case FOREST_HILLS:
                return new Color(0x4C, 0xAF, 0x50);
            case PLAINS:
                return new Color(0x8B, 0xC3, 0x4A);
            case DESERT:
            case DESERT_HILLS:
                return new Color(0xFF, 0xEB, 0x3B);
            case EXTREME_HILLS:
                return new Color(0x80, 0xA6, 0x78);
            case OCEAN:
                return new Color(0xFF, 0xEB, 0x3B);
            case ICE_OCEAN:
                return new Color(0x64, 0x95, 0xF6);
            default:
                return new Color(0, 0, 0);
        }
    }

    public static final Biome[][] biomeMap = new Biome[][]{
            {TAIGA,     TAIGA,  TAIGA,         TAIGA,          JUNGLE,         JUNGLE,         JUNGLE, JUNGLE, JUNGLE,         JUNGLE,         SWAMP,          SWAMP,          SWAMP,  SWAMP},
            {TAIGA,     TAIGA,  TAIGA,         TAIGA,          JUNGLE,         JUNGLE,         JUNGLE, JUNGLE, JUNGLE,         JUNGLE,         SWAMP,          SWAMP,          SWAMP,  SWAMP},
            {TAIGA,     TAIGA,  TAIGA,         TAIGA,          JUNGLE,         JUNGLE,         JUNGLE, JUNGLE, JUNGLE,         JUNGLE,         SWAMP,          SWAMP,          SWAMP,  SWAMP},
            {TAIGA,     TAIGA,  TAIGA_HILLS,   TAIGA_HILLS,    JUNGLE_HILLS,   JUNGLE_HILLS,   JUNGLE, JUNGLE, JUNGLE,         JUNGLE,         SWAMP,          SWAMP,          SWAMP,  SWAMP},
            {TAIGA,     TAIGA,  TAIGA_HILLS,   EXTREME_HILLS,  EXTREME_HILLS,  JUNGLE_HILLS,   JUNGLE, JUNGLE, JUNGLE,         JUNGLE,         SWAMP,          SWAMP,          SWAMP,  SWAMP},
            {TUNDRA,    TUNDRA, TUNDRA,        EXTREME_HILLS,  EXTREME_HILLS,  FOREST_HILLS,   FOREST, FOREST, FOREST,         FOREST,         PLAINS,         PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         FOREST_HILLS,   FOREST_HILLS,   FOREST, FOREST, FOREST,         FOREST,         PLAINS,         PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         FOREST,         FOREST,         FOREST, FOREST, FOREST,         FOREST,         PLAINS,         PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         FOREST,         FOREST,         FOREST, FOREST, FOREST,         FOREST,         PLAINS,         PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA_HILLS,  TUNDRA_HILLS,   FOREST_HILLS,   FOREST_HILLS,   FOREST, FOREST, FOREST_HILLS,   FOREST_HILLS,   PLAINS,         PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA_HILLS,  EXTREME_HILLS,  EXTREME_HILLS,  FOREST_HILLS,   FOREST, FOREST, FOREST_HILLS,   EXTREME_HILLS,  EXTREME_HILLS,  PLAINS,         PLAINS, PLAINS},
            {TUNDRA,    TUNDRA, TUNDRA_HILLS,  EXTREME_HILLS,  EXTREME_HILLS,  PLAINS,         PLAINS, PLAINS, PLAINS,         EXTREME_HILLS,  EXTREME_HILLS,  DESERT_HILLS,   DESERT, DESERT},
            {TUNDRA,    TUNDRA, TUNDRA_HILLS,  TUNDRA_HILLS,   PLAINS,         PLAINS,         PLAINS, PLAINS, PLAINS,         PLAINS,         DESERT_HILLS,   DESERT_HILLS,   DESERT, DESERT},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         PLAINS,         PLAINS,         PLAINS, PLAINS, PLAINS,         PLAINS,         DESERT,         DESERT,         DESERT, DESERT},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         PLAINS,         PLAINS,         PLAINS, PLAINS, PLAINS,         PLAINS,         DESERT,         DESERT,         DESERT, DESERT},
            {TUNDRA,    TUNDRA, TUNDRA,        TUNDRA,         PLAINS,         PLAINS,         PLAINS, PLAINS, PLAINS,         PLAINS,         DESERT,         DESERT,         DESERT, DESERT}
    };

    public static final int waterTemperature = 5;

    public static Biome getMapBiome(int humidity, int temperature){
        if(humidity >= 0 && humidity < biomeMap.length && temperature >= 0 && temperature < biomeMap[0].length){
            return biomeMap[humidity][temperature];
        }else{
            return OCEAN;
        }
    }
}
