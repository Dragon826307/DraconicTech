package dragon826307.dt.project.microtick;

import dragon826307.dt.DraconicTech;

import java.util.BitSet;

public class WorldTickManager {
    private static final BitSet TICK_FLAGS = new BitSet(WorldTickingFlags.values().length);
    // shouldTickCommandFunction = bit 0
    // shouldTickWorldBorder = bit 1
    // shouldTickWeather = bit 2
    // shouldCountAmbientDarkness = bit 3
    // shouldTickPendingBlocks = bit 4
    // shouldTickPendingFluid = bit 5
    // shouldTickRaid = bit 6
    // shouldTickChunk = bit 7
    // shouldTickBlockEvents = bit 8
    // shouldTickDragonFight = bit 9
    // shouldTickEntity = bit 10
    // shouldTickBlockEntity = bit 11
    // entityLoadChunk = bit 12
    // entityLoadChunk = bit 13
    // shouldTickPlayer = bit 14
    private static int tickFrozenLevel = 0;
    // level:
    // 0 -> normal
    // 1 -> global
    // 2 -> phase
    // 3 -> event
    // 4 -> update
    // 5 -> ???
    private static int stepTimes = 0;
    public static WorldTickingFlags flag$frozenLevel0;
    public static void init(){
        TICK_FLAGS.set(0, WorldTickingFlags.values().length);
    }
    public static void setWorldTickFlag(WorldTickingFlags flag,boolean bl){
        switch (flag){
            case COMMAND_FUNCTION -> TICK_FLAGS.set(0,bl);
            case WORLD_BORDER -> TICK_FLAGS.set(1,bl);
            case WEATHER -> TICK_FLAGS.set(2,bl);
            case AMBIENT_DARKNESS -> TICK_FLAGS.set(3,bl);
            case PENDING_BLOCK -> TICK_FLAGS.set(4,bl);
            case PENDING_FLUID -> TICK_FLAGS.set(5,bl);
            case RAID -> TICK_FLAGS.set(6,bl);
            case CHUNK -> TICK_FLAGS.set(7,bl);
            case BLOCK_EVENT -> TICK_FLAGS.set(8,bl);
            case DRAGON_FIGHT -> TICK_FLAGS.set(9,bl);
            case ENTITIES -> TICK_FLAGS.set(10,bl);
            case BLOCK_ENTITIES -> TICK_FLAGS.set(11,bl);
            case ENTITY_LOAD_CHUNK -> TICK_FLAGS.set(12,bl);
            case ENTITY_UNLOAD_CHUNK -> TICK_FLAGS.set(13,bl);
            case PLAYER -> TICK_FLAGS.set(14,bl);
            default -> {
                DraconicTech.LOGGER.error("[WorldTickManager] set world tick flag to ILLEGAL flag");
                throw new IllegalStateException("Illegal world tick flag");
            }
        }
    }
    public static boolean getWorldTickFlag(WorldTickingFlags flag){
        switch (flag){
            case COMMAND_FUNCTION -> {return TICK_FLAGS.get(0);}
            case WORLD_BORDER -> {return TICK_FLAGS.get(1);}
            case WEATHER -> {return TICK_FLAGS.get(2);}
            case AMBIENT_DARKNESS -> {return TICK_FLAGS.get(3);}
            case PENDING_BLOCK -> {return TICK_FLAGS.get(4);}
            case PENDING_FLUID -> {return TICK_FLAGS.get(5);}
            case RAID -> {return TICK_FLAGS.get(6);}
            case CHUNK -> {return TICK_FLAGS.get(7);}
            case BLOCK_EVENT -> {return TICK_FLAGS.get(8);}
            case DRAGON_FIGHT -> {return TICK_FLAGS.get(9);}
            case ENTITIES -> {return TICK_FLAGS.get(10);}
            case BLOCK_ENTITIES -> {return TICK_FLAGS.get(11);}
            case ENTITY_LOAD_CHUNK -> {return TICK_FLAGS.get(12);}
            case ENTITY_UNLOAD_CHUNK -> {return TICK_FLAGS.get(13);}
            case PLAYER -> {return TICK_FLAGS.get(14);}
            default -> {
                DraconicTech.LOGGER.error("[WorldTickManager] INVALID WorldTickFlags value");
                throw new IllegalStateException("Unexpected value: " + flag);
            }
        }
    }
    public static void setTickFrozenLevel(int lvl){
        if (lvl < 0 || lvl > 5) {
            throw new IllegalArgumentException("Invalid value for WorldTickManager.tickFrozenLevel: " + lvl);
        }
        WorldTickManager.tickFrozenLevel = lvl;
        TICK_FLAGS.set(0,10, WorldTickManager.tickFrozenLevel == 0);
    }
    public static int getTickFrozenLevel(){
        return WorldTickManager.tickFrozenLevel;
    }
}

