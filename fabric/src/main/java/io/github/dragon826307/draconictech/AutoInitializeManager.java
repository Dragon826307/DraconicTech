package io.github.dragon826307.draconictech;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AutoInitializeManager {
    private static final String[] TARGET_ARRAYS = new String[]{"mixins", "client", "server"};
    private static final Map<InitializePhase, List<Method>> REGISTERED_METHODS = new EnumMap<>(InitializePhase.class);
    public static void scanAndRegister(Predicate<String> packageFilter) {
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            CustomValue customValue = modContainer.getMetadata().getCustomValue(DraconicTech.MOD_ID + ":auto_init");
            if (customValue != null && customValue.getType() == CustomValue.CvType.ARRAY) {
                List<String> targetPackages = new ArrayList<>();
                for (CustomValue cv : customValue.getAsArray()) {
                    targetPackages.add(cv.getAsString());
                }
                Set<String> mixinBlacklist = getMixinClasses(modContainer);
                for (Path root : modContainer.getRootPaths()) {
                    final String[] className = {""};
                    try (Stream<Path> paths = Files.walk(root)) {
                        paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".class")).forEach(path -> {
                            className[0] = root.relativize(path).toString().replace("/", ".").replace("\\", ".").replace(".class", "");
                            boolean belongsToPackage = targetPackages.stream().anyMatch(className[0]::startsWith);
                            if (belongsToPackage && !isMixinClass(className[0], mixinBlacklist) && packageFilter.test(className[0])) {
                                try {
                                    Class<?> clazz = Class.forName(className[0], false, AutoInitializeManager.class.getClassLoader());
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method.isAnnotationPresent(AutoInitialize.class)) {
                                            AutoInitialize annotation = method.getAnnotation(AutoInitialize.class);
                                            method.setAccessible(true);
                                            REGISTERED_METHODS.computeIfAbsent(annotation.phase(), phase -> new ArrayList<>()).add(method);
                                        }
                                    }
                                }catch (NoClassDefFoundError | ClassNotFoundException e) {
                                    DraconicTech.LOGGER.error("[{}]Failed to load class: '{}' \n",DraconicTech.MOD_NAME, className[0], e);
                                }
                            }
                        });
                    }catch (Throwable e) {
                        DraconicTech.LOGGER.error("[{}]Failed to read from class: '{}' \n",DraconicTech.MOD_NAME, className[0], e);
                    }
                }
            }
        }
        for (List<Method> methods : REGISTERED_METHODS.values()) {
            methods.sort((m1, m2) -> {
                int p1 = m1.getAnnotation(AutoInitialize.class).priority();
                int p2 = m2.getAnnotation(AutoInitialize.class).priority();
                return Integer.compare(p1, p2);
            });
        }
    }
    public static void trigger(InitializePhase phase, Object... contexts) {
        List<Method> methods = REGISTERED_METHODS.getOrDefault(phase, Collections.emptyList());
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            loop:
            for (int i = 0; i < parameterTypes.length; i++) {
                for (Object context : contexts) {
                    if (context != null && parameterTypes[i].isAssignableFrom(context.getClass())) {
                        args[i] = context;
                        continue loop;
                    }
                }
                args[i] = null;
            }
            try {
                method.invoke(null, args);
            }catch (IllegalAccessException | InvocationTargetException e) {
                DraconicTech.LOGGER.error("[{}]Automatic initialization execution failed in method: '{}'",DraconicTech.MOD_NAME, method.getName(), e);
            }
        }
        DraconicTech.LOGGER.info("[{}] {} method(s) were automatically initialized during the triggering phase '{}'",DraconicTech.MOD_NAME, methods.size(), phase.name());
    }
    private static Set<String> getMixinClasses(ModContainer container) {
        Set<String> mixinClasses = new HashSet<>();
        try {
            Optional<Path> fabricJsonOpt = container.findPath("fabric.mod.json");
            if (fabricJsonOpt.isEmpty()) return mixinClasses;
            try (Reader reader = Files.newBufferedReader(fabricJsonOpt.get())) {
                JsonObject fabricJson = JsonParser.parseReader(reader).getAsJsonObject();
                if (!fabricJson.has("mixins")) return mixinClasses;
                JsonArray mixinsArray = fabricJson.getAsJsonArray("mixins");
                for (JsonElement mixin : mixinsArray) {
                    String mixinConfigFile = null;
                    if (mixin.isJsonObject()) {
                        mixinConfigFile = mixin.getAsJsonObject().get("config").getAsString();
                    }else if (mixin.isJsonPrimitive()) {
                        mixinConfigFile = mixin.getAsString();
                    }
                    if (mixinConfigFile != null) {
                        parseMixinConfig(container, mixinConfigFile, mixinClasses);
                    }
                }
            }
        } catch (Exception e) {
            DraconicTech.LOGGER.error("Failed to parse fabric.mod.json config file in mod: '{}'",container.getOrigin().getParentModId() , e);
        }
        return mixinClasses;
    }
    private static void parseMixinConfig(ModContainer container, String configName, Set<String> mixinClasses) {
        try {
            Optional<Path> configOpt = container.findPath(configName);
            if (configOpt.isEmpty()) return;
            try (Reader reader = Files.newBufferedReader(configOpt.get())) {
                JsonObject configJson = JsonParser.parseReader(reader).getAsJsonObject();
                String pkg = configJson.has("package") ? configJson.get("package").getAsString() : "";
                if (!pkg.isEmpty() && !pkg.endsWith(".")) {
                    pkg += ".";
                }
                for (String target : TARGET_ARRAYS) {
                    if (configJson.has(target)) {
                        JsonArray array = configJson.getAsJsonArray(target);
                        for (JsonElement element : array) {
                            mixinClasses.add(pkg + element.getAsString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            DraconicTech.LOGGER.error("Failed to parse mixin config '{}' in mod: '{}'",configName,container.getOrigin().getParentModId(), e);
        }
    }
    private static boolean isMixinClass(String className, Set<String> mixinBlacklist) {
        if (mixinBlacklist.contains(className)) {
            return true;
        }
        int dollarIndex = className.indexOf('$');
        if (dollarIndex != -1) {
            String outerClass = className.substring(0, dollarIndex);
            return mixinBlacklist.contains(outerClass);
        }
        return false;
    }
}