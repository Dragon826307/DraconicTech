package io.github.dragon826307.draconictech.config;

import com.google.common.primitives.Ints;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.command.ServerCommandHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.function.BooleanConsumer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ConfigProjectManager {
    public static final ConfigProject.Main<Integer> AUTO_SAVE_INTERVAL = ConfigProject.Main.register(new ConfigProject.Main<>(new ConfigInfo("ConfigProjectManager:auto_save_interval_seconds"), Integer.class, 120,integer -> integer >= 30 && integer <= 14400 , (string, invalidReason) -> Ints.tryParse(string),null, () -> List.of("60","120","300","1800"),null, ConfigProject.UpdateCommandTreeFlags.NOTHING));
    protected static final Map<Class<? extends AbstractConfigType<?>>,ConcurrentHashMap<AbstractConfigType<?>, Object>> CACHE = new ConcurrentHashMap<>();
    protected static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");
    protected static final Path MAIN_CONFIG = ROOT.resolve("main.dat");
    protected static final Path AUTO_CONFIG = ROOT.resolve("auto.dat");

    private static final List<BooleanConsumer> ON_SAVE = new ArrayList<>();
    private static final AtomicBoolean IS_SAVING = new AtomicBoolean(false);
    private static final ScheduledExecutorService ASYNC_SAVE_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r,"DraconicTech-Config-Saver-Thread");
        thread.setDaemon(true);
        return thread;
    });

    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING, priority = @Location(provideTo = ServerCommandHandler.class))
    private static void init(){
        CACHE.put(ConfigProject.Main.getClazz(),new ConcurrentHashMap<>());
        CACHE.put(ConfigProject.Auto.getClazz(),new ConcurrentHashMap<>());
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProject.Main<?> project: ConfigProject.Main.values()) CACHE.get(project.getClass()).putIfAbsent(project, project.getDefaultValue());
        for (ConfigProject.Auto<?> project: ConfigProject.Auto.values()) CACHE.get(project.getClass()).putIfAbsent(project, project.getDefaultValue());
        ASYNC_SAVE_EXECUTOR.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    trySaveAll(false);
                }catch (Exception e){
                    DraconicTech.LOGGER.error("Error while saving config projects.",e);
                }finally {
                    if (!ASYNC_SAVE_EXECUTOR.isShutdown()) {
                        ASYNC_SAVE_EXECUTOR.schedule(this,Objects.requireNonNullElse(getConfig(AUTO_SAVE_INTERVAL),300),TimeUnit.SECONDS);
                    }
                }
            }
        },Objects.requireNonNullElseGet(getConfig(AUTO_SAVE_INTERVAL), () -> {
            DraconicTech.LOGGER.warn("The config 'ConfigProjectManager:auto_save_interval_seconds' value is null, using default value (300).");
            return 300;
        }),TimeUnit.SECONDS);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> trySaveAll(true));
    }

    protected static void onSave(BooleanConsumer feedback){
        if (feedback == null) return;
        ON_SAVE.add(feedback);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(AbstractConfigType<T> project) {
        if (project == null) return null;
        Object value = CACHE.get(project.getClass()).get(project);
        return value != null ? (T) value : project.getDefaultValue();
    }

    public static <T> boolean setConfig(AbstractConfigType<T> project,T value) {
        if (project == null || value == null) return false;
        if (project.getConfigValidator().check(value)) {
            CACHE.get(project.getClass()).put(project,value);
            return true;
        }
        return false;
    }

    protected static void saveALL(boolean feedback){
        for (BooleanConsumer run: ON_SAVE) {
            run.accept(feedback);
        }
        atomicWrite(MAIN_CONFIG,copyALL(CACHE.get(ConfigProject.Main.getClazz())),feedback);
        atomicWrite(AUTO_CONFIG,copyALL(CACHE.get(ConfigProject.Auto.getClazz())),feedback);
    }
    public static void trySaveAll(boolean feedback){
        if (ASYNC_SAVE_EXECUTOR.isShutdown() || IS_SAVING.compareAndSet(false, true)) {
            saveALL(feedback);
        }
        IS_SAVING.set(false);
    }
    protected static <T extends AbstractConfigType<?>> Map<String,Object> copyALL(Map<T,Object> cache){
        return cache.entrySet().parallelStream().collect(Collectors.toMap(entry -> entry.getKey().getID(),Map.Entry::getValue));
    }
    private static void loadALL(){
        loadFormFile(MAIN_CONFIG, ConfigProject.Main.values(),CACHE.get(ConfigProject.Main.getClazz()));
        loadFormFile(AUTO_CONFIG, ConfigProject.Auto.values(),CACHE.get(ConfigProject.Auto.getClazz()));
    }
    protected static <T extends AbstractConfigType<?>> void loadFormFile(Path path, T[] projects, Map<T, Object> entry) {
        if(!Files.exists(path)) return;
        HashMap<String, T> key_map = new HashMap<>(Arrays.stream(projects).parallel().collect(Collectors.toMap(element -> element.getID(),element -> element)));
        long start = System.nanoTime()/1000;
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path))) {
            byte[] raw = inputStream.readAllBytes();
            reverse(raw);
            Map<String,Object> value_map = SerializationUtils.deserialize(raw);
            entry.putAll(value_map.entrySet().parallelStream().filter(element -> {
                if (key_map.containsKey(element.getKey())) {
                    return true;
                }
                DraconicTech.LOGGER.warn("Unknown config project id: '{}'",element.getKey());
                return false;
            }).filter(element -> {
                T project = key_map.get(element.getKey());
                if (project == null) {
                    DraconicTech.LOGGER.warn("NULL config project id: '{}'", element.getKey());
                    return false;
                }
                if (!validateConfigValue((AbstractConfigType<?>) project, element.getValue())) {
                    DraconicTech.LOGGER.warn("Invalid config project value: '{}' with config project id: '{}'", element.getValue(), element.getKey());
                    return false;
                }
                return true;
            }).collect(Collectors.toMap(element -> key_map.get(element.getKey()), Map.Entry::getValue)));
        }catch (Exception e){
            DraconicTech.LOGGER.error("Failed to load config projects from {}",path,e);
            return;
        }
        DraconicTech.LOGGER.info("Read config projects from '{}' in {} ms",path,(System.nanoTime()/1000-start)/1000f);
    }
    private static <V> boolean validateConfigValue(AbstractConfigType<V> project, Object rawValue) {
        if (project.getConfigType().isInstance(rawValue)) {
            V castedValue = project.getConfigType().cast(rawValue);
            return project.getConfigValidator().check(castedValue);
        }
        return false;
    }
    protected static void atomicWrite(Path target, Object object, boolean feedback){
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        long start = System.nanoTime()/1000;
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tmp))) {
            byte[] raw = SerializationUtils.serialize((Serializable) object);
            reverse(raw);
            outputStream.write(raw);
            outputStream.flush();
        }catch (IOException e){
            DraconicTech.LOGGER.error("Failed to save config projects to {}",tmp,e);
            return;
        }
        try (FileChannel channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
            channel.force(true);
        }catch (IOException ignored){}
        try {
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            DraconicTech.LOGGER.error("Failed to save config file while replacing config file: {}", target, e);
            return;
        }
        if (feedback) DraconicTech.LOGGER.info("Saved config projects to ‘{}’ in {} ms",target,(System.nanoTime()/1000-start)/1000f);
    }
    private static void reverse(byte[] array) {
        if (array == null) return;
        for (int i = 4; i < array.length; i++) {
            array[i] = (byte) ~array[i];
        }
    }
}