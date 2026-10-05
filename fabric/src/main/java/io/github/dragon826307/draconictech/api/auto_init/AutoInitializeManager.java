package io.github.dragon826307.draconictech.api.auto_init;

import io.github.dragon826307.draconictech.DraconicTech;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.*;
import org.spongepowered.asm.mixin.Mixin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class AutoInitializeManager {
    private static final int MAXIMUM_DEPENDENCY_DEPTH = 128;
    private static final String MINECRAFT_CLIENT_PACKAGE_PATH = "net/minecraft/client/";
    private static final Map<InitializePhase, List<Method>> REGISTERED_METHODS = new EnumMap<>(InitializePhase.class);

    private static boolean initialized = false;
    @SuppressWarnings("unused")
    private static void scanAndRegister() {
        if (initialized) return;
        long start = System.currentTimeMillis();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (!mod.getMetadata().getId().equals(DraconicTech.MOD_ID)) {
                boolean dependsOnMe = mod.getMetadata().getDependencies().stream().anyMatch(dependency -> !dependency.getModId().equals(DraconicTech.MOD_ID));
                if (!dependsOnMe) continue;
            }
            for (Path root : mod.getRootPaths()) {
                final String[] classPath = {""};
                try (Stream<Path> paths = Files.walk(root)) {
                    paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".class")).forEach(path -> {
                        classPath[0] = root.relativize(path).toString().replace("/",".").replace("\\", ".").replace(".class", "");
                        try {
                            if (!isSafeToLoad(path)) return;
                            Class<?> clazz = Class.forName(classPath[0], false, AutoInitializeManager.class.getClassLoader());
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(AutoInitialize.class)) {
                                    AutoInitialize annotation = method.getAnnotation(AutoInitialize.class);
                                    method.setAccessible(true);
                                    REGISTERED_METHODS.computeIfAbsent(annotation.phase(), phase -> new ArrayList<>()).add(method);
                                }
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError | IOException e) {
                            DraconicTech.LOGGER.error("Failed to load class: '{}' \n", classPath[0], e);
                        }
                    });
                } catch (Throwable t) {
                    DraconicTech.LOGGER.error("Failed to read from class: '{}' \n", classPath[0], t);
                }
            }
        }
        resolvePrioritiesAndSort();
        initialized = true;
        DraconicTech.LOGGER.info("Scan and register all methods in {} ms", System.currentTimeMillis() - start);
    }
    @SuppressWarnings("unused")
    private static void trigger(InitializePhase phase, Object... contexts) {
        long start = System.currentTimeMillis();
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
                DraconicTech.LOGGER.error("Automatic initialization execution failed in method: '{}'", method.getName(), e);
            }
        }
        DraconicTech.LOGGER.info("{} method(s) were automatically initialized during the triggering phase '{}' .Used {} ms", methods.size(), phase.name(), System.currentTimeMillis() - start);
    }

    private static boolean isSafeToLoad(Path classPath) throws IOException {
        try (InputStream is = Files.newInputStream(classPath)) {
            ClassReader reader = new ClassReader(is);
            ClassScanVisitor visitor = new ClassScanVisitor();
            reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return visitor.isSafeForCurrentEnv && visitor.hasAutoInitialize && !visitor.isMixinClass;
        }
    }
    private static void resolvePrioritiesAndSort() {
        for (Map.Entry<InitializePhase, List<Method>> entry : REGISTERED_METHODS.entrySet()) {
            InitializePhase phase = entry.getKey();
            List<Method> methods = entry.getValue();
            Map<Class<?>, List<Method>> classMethodsMap = new HashMap<>();
            for (Method method : methods) {
                classMethodsMap.computeIfAbsent(method.getDeclaringClass(), k -> new ArrayList<>()).add(method);
            }
            Map<Method, Integer> computedPriorityMap = new HashMap<>();
            Map<Class<?>, Integer> classMinPriorityCache = new HashMap<>();
            Deque<Class<?>> visitingStack = new ArrayDeque<>();
            Set<Class<?>> visitingSet = new HashSet<>();
            for (Method method : methods) {
                Class<?> declaringClass = method.getDeclaringClass();
                computeClassMinPriority(declaringClass, phase, classMethodsMap, computedPriorityMap, classMinPriorityCache, visitingStack, visitingSet);
            }
            methods.sort(Comparator.comparingInt(m -> computedPriorityMap.getOrDefault(m, 1000)));
        }
    }
    private static int computeClassMinPriority(
            Class<?> targetClass,
            InitializePhase phase,
            Map<Class<?>, List<Method>> classMethodsMap,
            Map<Method, Integer> computedPriorityMap,
            Map<Class<?>, Integer> classMinPriorityCache,
            Deque<Class<?>> visitingStack,
            Set<Class<?>> visitingSet
    ) {
        if (classMinPriorityCache.containsKey(targetClass)) {
            return classMinPriorityCache.get(targetClass);
        }
        List<Method> targetMethods = classMethodsMap.get(targetClass);
        if (targetMethods == null || targetMethods.isEmpty()) {
            classMinPriorityCache.put(targetClass, 1000);
            return 1000;
        }
        if (visitingSet.contains(targetClass)) {
            StringBuilder cyclePath = new StringBuilder();
            List<Class<?>> stackList = new ArrayList<>(visitingStack);
            Collections.reverse(stackList);
            for (Class<?> c : stackList) {
                cyclePath.append(c.getName()).append(" -> ");
            }
            cyclePath.append(targetClass.getName());
            DraconicTech.LOGGER.error("Circular dependency detected in @Location 'provideTo' during phase '{}': {}", phase.name(), cyclePath);
            throw new IllegalStateException();
        }
        if (visitingStack.size() > MAXIMUM_DEPENDENCY_DEPTH) {
            DraconicTech.LOGGER.error("Exceeded maximum dependency depth ({}) while resolving @Location for class: {}", Integer.toString(MAXIMUM_DEPENDENCY_DEPTH), targetClass.getName());
            throw new IllegalStateException();
        }
        visitingStack.push(targetClass);
        visitingSet.add(targetClass);
        int minPriority = Integer.MAX_VALUE;
        for (Method method : targetMethods) {
            int priority = computeMethodPriority(method, phase, classMethodsMap, computedPriorityMap, classMinPriorityCache, visitingStack, visitingSet);
            if (priority < minPriority) {
                minPriority = priority;
            }
        }
        visitingSet.remove(targetClass);
        visitingStack.pop();
        classMinPriorityCache.put(targetClass, minPriority);
        return minPriority;
    }
    private static int computeMethodPriority(
            Method method,
            InitializePhase phase,
            Map<Class<?>, List<Method>> classMethodsMap,
            Map<Method, Integer> computedPriorityMap,
            Map<Class<?>, Integer> classMinPriorityCache,
            Deque<Class<?>> visitingStack,
            Set<Class<?>> visitingSet
    ) {
        if (computedPriorityMap.containsKey(method)) {
            return computedPriorityMap.get(method);
        }
        Location location = method.getAnnotation(Location.class);
        AutoInitialize autoInit = method.getAnnotation(AutoInitialize.class);
        int priority;
        if (location != null) {
            boolean hasProvideTo = location.provideTo() != Void.class;
            boolean hasExplicitPriority = location.priority() != Integer.MIN_VALUE;
            if (hasProvideTo && hasExplicitPriority) {
                DraconicTech.LOGGER.warn(
                        "Method '{}.{}' has both 'provideTo' ({}) and 'priority' ({}) in @Location. Using explicit priority and ignoring 'provideTo'.",
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        location.provideTo().getSimpleName(),
                        location.priority()
                );
                priority = location.priority();
            } else if (hasExplicitPriority) {
                priority = location.priority();
            } else if (hasProvideTo) {
                Class<?> targetClass = location.provideTo();
                int targetMinPriority = computeClassMinPriority(targetClass, phase, classMethodsMap, computedPriorityMap, classMinPriorityCache, visitingStack, visitingSet);
                priority = targetMinPriority - 1;
            } else {
                priority = autoInit != null ? autoInit.priority().priority() : 1000;
            }
        } else {
            priority = autoInit != null ? autoInit.priority().priority() : 1000;
        }
        computedPriorityMap.put(method, priority);
        return priority;
    }
    private static final class ClassScanVisitor extends ClassVisitor {
        boolean isSafeForCurrentEnv = true;
        boolean hasAutoInitialize = false;
        boolean isMixinClass = false;
        private final boolean isDedicatedServer =
                FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        public ClassScanVisitor() {
            super(Opcodes.ASM9);
        }
        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (Mixin.class.descriptorString().equals(descriptor)) {
                isMixinClass = true;
            }
            if (Environment.class.descriptorString().equals(descriptor)) {
                return new AnnotationVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        if (isDedicatedServer && EnvType.CLIENT.name().equals(value)) {
                            isSafeForCurrentEnv = false;
                        }
                    }
                };
            }
            return super.visitAnnotation(descriptor, visible);
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (isDedicatedServer && isSafeForCurrentEnv && descriptor != null) {
                if (descriptor.contains(MINECRAFT_CLIENT_PACKAGE_PATH)) {
                    isSafeForCurrentEnv = false;
                }
            }
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String methodDescriptor, boolean visible) {
                    if (AutoInitialize.class.descriptorString().equals(methodDescriptor)) {
                        hasAutoInitialize = true;
                    }
                    return super.visitAnnotation(methodDescriptor, visible);
                }
            };
        }
        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (isDedicatedServer && isSafeForCurrentEnv && descriptor != null) {
                if (descriptor.contains(MINECRAFT_CLIENT_PACKAGE_PATH)) {
                    isSafeForCurrentEnv = false;
                }
            }
            return super.visitField(access, name, descriptor, signature, value);
        }
    }
}