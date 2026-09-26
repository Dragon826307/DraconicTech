package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.util.auto_init.InitializePhase;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static final Map<AbstractConfigType<?>, Object> CACHE = new ConcurrentHashMap<>();
    private static final Map<ConfigProject.Main<?>, Object> CACHE_MAIN = new ConcurrentHashMap<>();
    private static final Map<ConfigProject.Auto<?>, Object> CACHE_AUTO = new ConcurrentHashMap<>();
    protected static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");
    protected static final Path MAIN_CONFIG = ROOT.resolve("main.dat");
    protected static final Path AUTO_CONFIG = ROOT.resolve("auto.dat");

    private static final List<Runnable> ON_SAVE = new ArrayList<>();
    private static final AtomicBoolean IS_SAVING = new AtomicBoolean(false);
    private static final ScheduledExecutorService ASYNC_SAVE_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r,"DraconicTech-Config-Saver-Thread");
        thread.setDaemon(true);
        return thread;
    });


    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING, priority = 999)
    private static void init(){
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProject.Main<?> project: ConfigProject.Main.values()) CACHE.putIfAbsent(project, project.getDefaultValue());
        for (ConfigProject.Auto<?> project: ConfigProject.Auto.values()) CACHE.putIfAbsent(project, project.getDefaultValue());
        ASYNC_SAVE_EXECUTOR.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!IS_SAVING.get()) {
                        IS_SAVING.set(true);
                        saveALL();
                        IS_SAVING.set(false);
                    }
                }catch (Exception e){
                    DraconicTech.LOGGER.error("Error while saving config projects.",e);
                }finally {
                    if (!ASYNC_SAVE_EXECUTOR.isShutdown()) {
                        ASYNC_SAVE_EXECUTOR.schedule(this,getConfig(ConfigProject.Main.AUTO_SAVE_INTERVAL),TimeUnit.SECONDS);
                    }
                }
            }
        },getConfig(ConfigProject.Main.AUTO_SAVE_INTERVAL),TimeUnit.SECONDS);
    }

    protected static void onSave(Runnable runnable){
        if (runnable == null) return;
        ON_SAVE.add(runnable);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(AbstractConfigType<T> project) {
        if (project == null) return null;
        Object value = CACHE.get(project);
        return value != null ? (T) value : project.getDefaultValue();
    }
    public static Object getConfigWithoutType(AbstractConfigType<?> project) {
        if (project == null) return null;
        Object value = CACHE.get(project);
        return value != null ? value : project.getDefaultValue();
    }
    public static <T> boolean setConfig(AbstractConfigType<T> project,T value) {
        if (project == null || value == null) return false;
        if (project.getConfigValidator().check(value)) {
            CACHE.put(project,value);
            return true;
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    public static <T> boolean setConfigWithoutType(AbstractConfigType<?> project,Object value) {
        if (project == null || value == null) return false;
        AbstractConfigType<T> typedProject = (AbstractConfigType<T>) project;
        T castedValue;
        if (typedProject.getConfigType().isInstance(value)) {
            castedValue = (T) value;
        }else if (value instanceof String string) {
            castedValue = typedProject.getParser().parse(string);
        } else return false;
        if (castedValue == null || !typedProject.getConfigValidator().check(castedValue)) {
            return false;
        }
        CACHE.put(typedProject, castedValue);
        return true;
    }
    protected synchronized static void saveALL(){
        for (Runnable runnable: ON_SAVE) {
            runnable.run();
        }
        atomicWrite(MAIN_CONFIG,copyALL(CACHE_MAIN));
        atomicWrite(AUTO_CONFIG,copyALL(CACHE_AUTO));
    }
    public static void trySaveAll(){
        if (!IS_SAVING.get() || ASYNC_SAVE_EXECUTOR.isShutdown()) {
            IS_SAVING.set(true);
            saveALL();
            IS_SAVING.set(false);
        }
    }
    protected synchronized static <T extends AbstractConfigType<?>> Map<String,Object> copyALL(Map<T,Object> cache){
        return cache.entrySet().parallelStream().collect(Collectors.toMap(entry -> entry.getKey().getID(),Map.Entry::getValue));
    }
    private static void loadALL(){
        loadFormFile(MAIN_CONFIG, ConfigProject.Main.values(),CACHE_MAIN);
        loadFormFile(AUTO_CONFIG, ConfigProject.Auto.values(),CACHE_AUTO);
    }
    @SuppressWarnings("unchecked")
    protected static <T extends AbstractConfigType<?>,U> void loadFormFile(Path path, T[] projects, Map<T, Object> entry){
        if(!Files.exists(path)) return;
        HashMap<String, T> key_map = new HashMap<>(Arrays.stream(projects).parallel().collect(Collectors.toMap(element -> element.getID(),element -> element)));
        long start = System.currentTimeMillis();
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path))) {
            Map<String,Object> value_map = (Map<String,Object>) inputStream.readObject();
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
        DraconicTech.LOGGER.info("Read config projects from {} in {} ms",path,System.currentTimeMillis()-start);
    }
    private static <V> boolean validateConfigValue(AbstractConfigType<V> project, Object rawValue) {
        if (project.getConfigType().isInstance(rawValue)) {
            V castedValue = project.getConfigType().cast(rawValue);
            return project.getConfigValidator().check(castedValue);
        }
        return false;
    }
    protected static void atomicWrite(Path target, Object object){
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tmp))) {
            outputStream.writeObject(object);
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
        }
    }
}