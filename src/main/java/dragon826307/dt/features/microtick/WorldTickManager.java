package dragon826307.dt.features.microtick;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerTickManager;

public class WorldTickManager {
    private final ServerTickManager serverTickManager;
    private int TICK_FLAGS = -1;
    private int tickFrozenLevel = 0;
    // level:
    // 0 -> normal
    // 1 -> global
    // 2 -> phase
    // 3 -> event
    // 4 -> update
    // 5 -> ???
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> DraconicTech.getWorldTickManager().tickFrozenLevel = 0);
    }
    public WorldTickManager(ServerTickManager serverTickManager) {
        this.serverTickManager = serverTickManager;
    }
    public void setWorldTickFlag(WorldTickingFlags flag,boolean bl) {
        TICK_FLAGS = ~(1 << flag.ordinal()) & TICK_FLAGS | (bl ? 1 << flag.ordinal() : 0);
    }
    public boolean getWorldTickFlag(WorldTickingFlags flag) {
        return ((TICK_FLAGS >>> flag.ordinal()) & 1) == 1;
    }
    public int getWorldTickFlags() {
        return TICK_FLAGS;
    }
    public void setTickFrozenLevel(int lvl){
        if (lvl < 0 || lvl > 5) {
            throw new IllegalArgumentException("Invalid value for WorldTickManager.tickFrozenLevel: " + lvl);
        }
        tickFrozenLevel = lvl;
        if (lvl == 0) TICK_FLAGS |= 16383;
        else if (lvl == 1) TICK_FLAGS &= -16384;
    }
    public int getTickFrozenLevel(){
        return tickFrozenLevel;
    }
    public ServerTickManager getServerTickManager() {
        return serverTickManager;
    }
}

