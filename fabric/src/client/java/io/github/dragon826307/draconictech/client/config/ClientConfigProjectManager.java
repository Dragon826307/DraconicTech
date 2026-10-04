package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.client.command.ClientCommandHandler;
import io.github.dragon826307.draconictech.config.ConfigProject;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientConfigProjectManager extends ConfigProjectManager {
    private static final Path CLIENT_CONFIG = ROOT.resolve("client.dat");
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED,priority = @Location(provideTo = ClientCommandHandler.class))
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ClientConfigProjectManager");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Cannot initialize ClientConfigManager on a physical Server!");
        }
        CACHE.put(ConfigProject.Client.getClazz(),new ConcurrentHashMap<>());
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProject.Client<?> project: ConfigProject.Client.values()) CACHE.get(ConfigProject.Client.getClazz()).putIfAbsent(project, project.getDefaultValue());
        ConfigProjectManager.onSave(ClientConfigProjectManager::saveALL);
    }
    public static void saveALL(boolean feedback){
        atomicWrite(CLIENT_CONFIG,copyALL(CACHE.get(ConfigProject.Client.getClazz())),feedback);
    }
    private static void loadALL(){
        loadFormFile(CLIENT_CONFIG, ConfigProject.Client.values(),CACHE.get(ConfigProject.Client.getClazz()));
    }
}
