package dragon826307.dt.server;

import dragon826307.dt.AutoInitializeManager;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import net.fabricmc.api.DedicatedServerModInitializer;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        AutoInitializeManager.scanAndRegister(name -> name.contains(".server."));
        AutoInitializeManager.trigger(InitializePhase.ON_MOD_INIT_SERVER);
    }
}
