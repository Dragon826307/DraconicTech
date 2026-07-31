package dragon826307.dt;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AutoInitializeManager {
    private static final Map<InitializePhase, List<Method>> REGISTERED_METHODS = new EnumMap<>(InitializePhase.class);
    public static void scanAndRegister(Predicate<String> packageFilter) {
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            CustomValue customValue = modContainer.getMetadata().getCustomValue(DraconicTech.MOD_ID + ":auto_init");
            if (customValue != null && customValue.getType() == CustomValue.CvType.ARRAY) {
                List<String> targetPackages = new ArrayList<>();
                for (CustomValue cv : customValue.getAsArray()) {
                    targetPackages.add(cv.getAsString());
                }
                for (Path root : modContainer.getRootPaths()) {
                    try (Stream<Path> paths = Files.walk(root)) {
                        paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".class")).forEach(path -> {
                            String className = root.relativize(path).toString().replace("/", ".").replace("\\", ".").replace(".class", "");
                            boolean belongsToPackage = targetPackages.stream().anyMatch(className::startsWith);
                            if (belongsToPackage && !className.contains(".mixin.") && packageFilter.test(className)) {
                                try {
                                    Class<?> clazz = Class.forName(className, false, AutoInitializeManager.class.getClassLoader());
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method.isAnnotationPresent(AutoInitialize.class)) {
                                            AutoInitialize annotation = method.getAnnotation(AutoInitialize.class);
                                            method.setAccessible(true);
                                            REGISTERED_METHODS.computeIfAbsent(annotation.phase(), phase -> new ArrayList<>()).add(method);
                                        }
                                    }
                                }catch (NoClassDefFoundError | ClassNotFoundException e) {
                                    DraconicTech.LOGGER.error("[{}]Failed to load class: '{}' \n",DraconicTech.MOD_NAME, className, e);
                                }
                            }
                        });
                    }catch (Throwable e) {
                        DraconicTech.LOGGER.error("[{}]Failed to read from file: '{}' \n",DraconicTech.MOD_NAME, root, e);
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
}