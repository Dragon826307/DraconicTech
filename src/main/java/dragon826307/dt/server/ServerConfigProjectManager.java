package dragon826307.dt.server;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.config.ConfigGetterValue;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfigProjectManager extends ConfigProjectManager {
    private static final Map<ConfigProjects.Server, ConfigGetterValue> CACHE = new ConcurrentHashMap<>();
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        loadALL();
        saveALL();
    }
    public static ConfigGetterValue getConfig(ConfigProjects.Server project) {
        return CACHE.getOrDefault(project,new ConfigGetterValue(project.getDefaultValue()));
    }
    public static boolean setConfig(ConfigProjects.Server project, Object value) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return false;
        }
        if (project.getConfigType().getClazz().isInstance(value)) {
            CACHE.put(project, new ConfigGetterValue(value));
            return true;
        }
        return false;
    }
    public static void saveALL(){
        atomicWrite(SERVER_CONFIG,copyALL(CACHE));
    }
    private static void loadALL(){
        loadFormFile(SERVER_CONFIG,ConfigProjects.Server.values(),CACHE);
    }
}
