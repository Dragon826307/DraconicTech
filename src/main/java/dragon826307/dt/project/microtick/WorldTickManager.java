package dragon826307.dt.project.microtick;

import java.util.BitSet;

public class WorldTickManager {
    private static final BitSet TICK_FLAGS = new BitSet(WorldTickingFlags.values().length);
    private static int tickFrozenLevel = 0;
    // level:
    // 0 -> normal
    // 1 -> global
    // 2 -> phase
    // 3 -> event
    // 4 -> update
    // 5 -> ???
    public static WorldTickingFlags flag$frozenLevel0;
    public static void init(){
        TICK_FLAGS.set(0, WorldTickingFlags.values().length);
    }
    public static void setWorldTickFlag(WorldTickingFlags flag,boolean bl){
        TICK_FLAGS.set(flag.ordinal(),bl);
    }
    public static boolean getWorldTickFlag(WorldTickingFlags flag){
        return TICK_FLAGS.get(flag.ordinal());
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

