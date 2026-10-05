package io.github.dragon826307.draconictech;

import io.github.dragon826307.draconictech.api.auto_init.AutoInitializeManager;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.functions.microtick.MicroTickManager;
import io.github.dragon826307.draconictech.util.TextColorHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DraconicTech implements ModInitializer {
    public static final Text MOD_PREFIX = TextColorHelper.gradientColor("[Draconic Tech]",0xB061F0,0x371C82).styled(style -> style.withBold(true));
    public static final String MOD_NAME = "DraconicTech";
	public static final String MOD_ID = "draconictech";
    public static final Logger LOGGER = new Logger(LoggerFactory.getLogger(MOD_NAME), MOD_NAME);
    @Override
    public void onInitialize() {
        DraconicTech.LOGGER.info("Initializing DraconicTech...");
        try {
            Method scanAndRegister = AutoInitializeManager.class.getDeclaredMethod("scanAndRegister");
            scanAndRegister.setAccessible(true);
            scanAndRegister.invoke(null);
            Method trigger = AutoInitializeManager.class.getDeclaredMethod("trigger", InitializePhase.class, Object[].class);
            trigger.setAccessible(true);
            trigger.invoke(null, InitializePhase.ON_MOD_INIT_MAIN, ArrayUtils.EMPTY_OBJECT_ARRAY);
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                try {
                    trigger.invoke(null, InitializePhase.ON_SERVER_STARTING, ArrayUtils.toArray(server));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ExceptionInInitializerError(e);
                }
            });
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                try {
                    trigger.invoke(null, InitializePhase.ON_SERVER_STARTED, ArrayUtils.toArray(server));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ExceptionInInitializerError(e);
                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ExceptionInInitializerError(e.getMessage());
        }
        drawModLogoInLogger();
    }
    public static MicroTickManager getMicroTickManager(){
        return MicroTickManager.getInstance();
    }
    private static void drawModLogoInLogger() {
        LOGGER.info("""
                
                ########  ########     ###     ######   #######  ##    ## ####  ###### \s
                ##     ## ##     ##   ## ##   ##    ## ##     ## ###   ##  ##  ##    ##\s
                ##     ## ##     ##  ##   ##  ##       ##     ## ####  ##  ##  ##      \s
                ##     ## ########  ##     ## ##       ##     ## ## ## ##  ##  ##      \s
                ##     ## ##   ##   ######### ##       ##     ## ##  ####  ##  ##      \s
                ##     ## ##    ##  ##     ## ##    ## ##     ## ##   ###  ##  ##    ##\s
                ########  ##     ## ##     ##  ######   #######  ##    ## ####  ###### \s
                
                ######## ########  ######  ##     ##\s
                   ##    ##       ##    ## ##     ##\s
                   ##    ##       ##       ##     ##\s
                   ##    ######   ##       #########\s
                   ##    ##       ##       ##     ##\s
                   ##    ##       ##    ## ##     ##\s
                   ##    ########  ######  ##     ##\s
                """);
    }
}