package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.config.ConfigGetterValue;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigProjects;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.api.EnvType;
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
        onConfigSave(ClientConfigProjectManager::saveALL);
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
