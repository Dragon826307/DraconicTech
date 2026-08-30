package dragon826307.dt.client.config;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.config.ConfigGetterValue;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientConfigProjectManager extends ConfigProjectManager {
    private static final Map<ConfigProjects.Client, ConfigGetterValue> CACHE = new ConcurrentHashMap<>();
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED,priority = 999)
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ClientConfigProjectManager");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Cannot initialize ClientConfigManager on a physical Server!");
        }
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProjects.Client project: ConfigProjects.Client.values()) CACHE.putIfAbsent(project, new ConfigGetterValue(project.getDefaultValue()));
        ServerLifecycleEvents.BEFORE_SAVE.register((minecraftServer, b, b1) -> ClientConfigProjectManager.saveALL());
    }
    public static ConfigGetterValue getConfig(ConfigProjects.Client project) {
        return CACHE.get(project);
    }
    public static boolean setConfig(ConfigProjects.Client project, Object value) {
        if (project.getConfigType().getClazz().isInstance(value)) {
            CACHE.put(project, new ConfigGetterValue(value));
            return true;
        }
        return false;
    }
    public static void saveALL(){
        atomicWrite(CLIENT_CONFIG,copyALL(CACHE));
    }
    private static void loadALL(){
        loadFormFile(CLIENT_CONFIG,ConfigProjects.Client.values(),CACHE);
    }
}
