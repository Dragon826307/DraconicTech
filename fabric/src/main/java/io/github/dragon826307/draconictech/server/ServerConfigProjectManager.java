package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.config.ConfigGetterValue;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigProjects;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
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
        ConfigProjectManager.onConfigSave(ServerConfigProjectManager::saveALL);
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
