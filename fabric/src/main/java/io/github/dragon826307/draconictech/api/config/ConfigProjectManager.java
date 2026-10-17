package io.github.dragon826307.draconictech.api.config;

import com.google.common.primitives.Ints;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.api.text.Colors;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.command.ServerCommandHandler;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import lombok.Synchronized;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.function.BooleanConsumer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
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
    public static final ConfigProject.Main<Integer> AUTO_SAVE_INTERVAL = ConfigProject.Main.register(new ConfigProject.Main<>(ConfigBuilder.CommonConfigBuilder.create("ConfigProjectManager:auto_save_interval_seconds", Integer.class, 300, ((string, invalidReason) -> Ints.tryParse(string))).setConfigValidator(ConfigBuildHelper.INTEGER_VALIDATOR(60,3600)).setSuggestionsSupplier(() -> List.of("60","120","300","1800")).setName(TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("save_sec"),true).setColor(Colors.ARGB.BLUE_GREY_400).build()).setDescription(TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("save_sec_dec"), true).setColor(Colors.ARGB.GRAY).build())));
    private static final Map<String, ConcurrentHashMap<AbstractConfigType<?>, Object>> CACHE = new ConcurrentHashMap<>();
    protected static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");

    private static final List<BooleanConsumer> ON_SAVE = new ArrayList<>();
    private static final AtomicBoolean IS_SAVING = new AtomicBoolean(false);
    private static final ScheduledExecutorService ASYNC_SAVE_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r,"DraconicTech-Config-Saver-Thread");
        thread.setDaemon(true);
        return thread;
    });

    public static <T extends AbstractConfigType<?>> void register(T[] type) {
        if (type.length == 0) return;
        String name = type[0].typeName();
        if (!CACHE.containsKey(name)) CACHE.put(name, new ConcurrentHashMap<>());
        Map<AbstractConfigType<?>, Object> map = CACHE.get(name);
        for (AbstractConfigType<?> config : type) {
            map.putIfAbsent(config, config.getDefaultValue());
        }
    }

    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING, priority = @Location(provideTo = ServerCommandHandler.class))
    private static void init(){
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        register(ConfigProject.Main.values());
        register(ConfigProject.Auto.values());
        loadToCacheFormFile(ConfigProject.Main.TYPE);
        loadToCacheFormFile(ConfigProject.Auto.TYPE);
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
        Map<AbstractConfigType<?>, Object> map = CACHE.get(project.typeName());
        if (map == null) {
            DraconicTech.LOGGER.warn("The config cache map '{}' is null. Using '{}' default value '{}'",project.getClass().descriptorString(), project.getID(), String.valueOf(project.getDefaultValue()));
            return project.getDefaultValue();
        }
        Object value = map.get(project);
        return value != null ? (T) value : project.getDefaultValue();
    }

    public static <T> boolean setConfig(AbstractConfigType<T> project,T value) {
        if (project == null || value == null) return false;
        Map<AbstractConfigType<?>, Object> map = CACHE.get(project.typeName());
        if (map == null) {
            DraconicTech.LOGGER.warn("Failed to set config '{}' because config cache map '{}' is null", project.getID(), project.getClass().descriptorString());
            return false;
        }
        if (project.isConfigValueValid(value)) {
            map.put(project,value);
            return true;
        }
        return false;
    }
    public static void trySaveAll(boolean feedback){
        if (ASYNC_SAVE_EXECUTOR.isShutdown() || IS_SAVING.compareAndSet(false, true)) {
            try {
                for (BooleanConsumer run: ON_SAVE) {
                    run.accept(feedback);
                }
                atomicWrite(ConfigProject.Main.TYPE, feedback);
                atomicWrite(ConfigProject.Auto.TYPE, feedback);
            } catch (Throwable e) {
                DraconicTech.LOGGER.error("Unknown error while saving config projects.",e);
            } finally {
                IS_SAVING.set(false);
            }
        }
    }
    protected static void loadToCacheFormFile(String type) {
        Path path = ROOT.resolve(type + ".dat");
        Map<AbstractConfigType<?>, Object> entry = CACHE.get(type);
        if(entry == null || !Files.exists(path)) return;
        long start = System.nanoTime()/1000;
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {
            byte[] raw = inputStream.readAllBytes();
            reverse(raw);
            @SuppressWarnings("unchecked")
            Map<String,Object> value_map = ((Map<String,Object>) SerializationUtils.deserialize(raw)).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));
            entry.entrySet().stream()
                    .filter(abstractConfigTypeObjectEntry -> {
                        String id = abstractConfigTypeObjectEntry.getKey().getID();
                        if (Objects.isNull(value_map.get(id))) {
                            DraconicTech.LOGGER.warn("NULL config! project id: '{}'", id);
                            return false;
                        }
                        return true;
                    })
                    .filter(abstractConfigTypeObjectEntry -> value_map.containsKey(abstractConfigTypeObjectEntry.getKey().getID()))
                    .filter(abstractConfigTypeObjectEntry -> {
                        String id = abstractConfigTypeObjectEntry.getKey().getID();
                        Object value = value_map.get(id);
                        if (validateConfigValue(abstractConfigTypeObjectEntry.getKey(), value)) return true;
                        DraconicTech.LOGGER.warn("Invalid config project value: '{}' with config project id: '{}'", value, id);
                        return false;
                    })
                    .forEach(abstractConfigTypeObjectEntry -> entry.put(abstractConfigTypeObjectEntry.getKey(), value_map.get(abstractConfigTypeObjectEntry.getKey().getID())));
        }catch (Exception e){
            DraconicTech.LOGGER.error("Failed to load config projects from {}",path,e);
            return;
        }
        DraconicTech.LOGGER.info("Read config projects from '{}' in {} ms",path,(System.nanoTime()/1000-start)/1000f);
    }
    private static <V> boolean validateConfigValue(AbstractConfigType<V> project, Object rawValue) {
        if (project.getConfigType().isInstance(rawValue)) {
            V castedValue = project.getConfigType().cast(rawValue);
            return project.isConfigValueValid(castedValue);
        }
        return false;
    }
    @Synchronized
    protected static void atomicWrite(String type, boolean feedback) {
        Path target = ROOT.resolve(type + ".dat");
        Map<String, Object> object = copyALL(type);
        Path tmp = target.resolveSibling(type + ".tmp");
        long start = System.nanoTime()/1000;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(tmp))) {
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
    private static Map<String,Object> copyALL(String type) {
        Map<AbstractConfigType<?>, Object> cache = CACHE.get(type);
        if (cache == null) return Collections.emptyMap();
        return cache.entrySet().parallelStream().collect(Collectors.toMap(entry -> entry.getKey().getID(), Map.Entry::getValue, (k, v) -> k, HashMap::new));
    }
    private static void reverse(byte[] array) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) ~array[i];
        }
    }
}