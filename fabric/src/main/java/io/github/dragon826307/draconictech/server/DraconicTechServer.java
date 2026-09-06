package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.AutoInitializeManager;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.api.DedicatedServerModInitializer;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        AutoInitializeManager.trigger(InitializePhase.ON_MOD_INIT_DEDICATED_SERVER);
    }
}
