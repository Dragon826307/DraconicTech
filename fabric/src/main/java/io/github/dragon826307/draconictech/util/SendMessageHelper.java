package io.github.dragon826307.draconictech.util;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.util.auto_init.InitializePhase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class SendMessageHelper {
    private static MinecraftServer server;
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init(MinecraftServer server) {
        SendMessageHelper.server = server;
    }
    public static void sendMessageToAllPlayer(Text message) {
        if (server != null && server.getPlayerManager() != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(message);
            }
        }
    }
    public static Text getMessage(Text text, boolean withPrefix) {
        if (withPrefix) {
            return Text.empty().append(DraconicTech.MOD_PREFIX).append(text);
        }else  {
            return text;
        }
    }
    public static Text getMessage(Text text) {
        return getMessage(text,false);
    }
    public static Text getMessage(String message,boolean withPrefix) {
        return getMessage(Text.of(message),withPrefix);
    }
    public static Text getMessage(String message) {
        return getMessage(Text.of(message));
    }
}
