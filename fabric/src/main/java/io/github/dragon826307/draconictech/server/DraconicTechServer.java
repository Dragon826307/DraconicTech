package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitializeManager;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        try {
            Method trigger = AutoInitializeManager.class.getDeclaredMethod("trigger", InitializePhase.class, Object[].class);
            trigger.setAccessible(true);
            trigger.invoke(null, InitializePhase.ON_MOD_INIT_DEDICATED_SERVER, ArrayUtils.EMPTY_OBJECT_ARRAY);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
