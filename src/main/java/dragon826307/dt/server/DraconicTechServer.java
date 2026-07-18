package dragon826307.dt.server;

import dragon826307.dt.DraconicTech;
import net.fabricmc.api.DedicatedServerModInitializer;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        ServerConfigProjectManager.init();
    }
}
