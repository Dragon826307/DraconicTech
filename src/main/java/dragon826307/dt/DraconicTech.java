package dragon826307.dt;

import dragon826307.dt.command.ServerCommandHandler;
import dragon826307.dt.command.argument.ConfigValueArgumentType;
import dragon826307.dt.command.argument.serializer.ConfigValueArgumentSerializer;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.network.Mod$CheckingModS2CPacket;
import dragon826307.dt.network.ModNetworkHandler;
import dragon826307.dt.project.analog_circuit.ContainerSignalModifier;
import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.util.TextColorHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DraconicTech implements ModInitializer {
    public static boolean DEBUG = false;
    public static MinecraftServer minecraftServer;
    public static final Text MOD_PREFIX = TextColorHelper.gradientColor("[Draconic Tech]",0xB061F0,0x371C82).styled(style -> style.withBold(true));
    public static final String MOD_NAME = "DraconicTech";
	public static final String MOD_ID = "draconictech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    @Override
    public void onInitialize() {
        DraconicTech.LOGGER.info("Initializing DraconicTech...");
        drawModLogoInLogger();
        ConfigProjectManager.init();//ConfigProjectManager必须优先于ServerCommandHandler
        ServerCommandHandler.init();
        ModNetworkHandler.init();
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (minecraftServer == null) minecraftServer = server;
        });
        ServerWorldEvents.LOAD.register((server, world) -> {
            WorldTickManager.init();
        });
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            packetSender.sendPacket(new Mod$CheckingModS2CPacket());
        });
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
            PlayerRecorder.removePlayer(serverPlayNetworkHandler.getPlayer().getUuid());
        });
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(ServerCommandHandler.commandRoot);
            commandDispatcher.register(ServerCommandHandler.commandRoot_copy);
        });
        ServerLifecycleEvents.BEFORE_SAVE.register((server, b1, b2) -> {
            DraconicTech.LOGGER.info("Saving all config...");
            ConfigProjectManager.saveALL();
        });
        UseBlockCallback.EVENT.register(new ContainerSignalModifier());
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