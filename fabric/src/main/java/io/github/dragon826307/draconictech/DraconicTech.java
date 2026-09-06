package io.github.dragon826307.draconictech;

import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import io.github.dragon826307.draconictech.util.AutoInitializeManager;
import io.github.dragon826307.draconictech.util.InitializePhase;
import io.github.dragon826307.draconictech.util.TextColorHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DraconicTech implements ModInitializer {
    public static boolean DEBUG = false;
    public static final Text MOD_PREFIX = TextColorHelper.gradientColor("[Draconic Tech]",0xB061F0,0x371C82).styled(style -> style.withBold(true));
    public static final String MOD_NAME = "DraconicTech";
	public static final String MOD_ID = "draconictech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    @Override
    public void onInitialize() {
        DraconicTech.LOGGER.info("Initializing DraconicTech...");
        AutoInitializeManager.scanAndRegister(name -> !name.contains(".client."));
        AutoInitializeManager.trigger(InitializePhase.ON_MOD_INIT_MAIN);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> AutoInitializeManager.trigger(InitializePhase.ON_SERVER_STARTING, server));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> AutoInitializeManager.trigger(InitializePhase.ON_SERVER_STARTED, server));
        drawModLogoInLogger();
    }
    public static MicroTickManager getMicroTickManager(){
        return MicroTickManager.INSTANCE;
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