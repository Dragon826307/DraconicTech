package io.github.dragon826307.draconictech.client.util;

import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import io.github.dragon826307.draconictech.util.SendMessageHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class ClientChatHudHelper {
    private static MinecraftClient client;
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED)
    private static void init(MinecraftClient client) {
        ClientChatHudHelper.client = client;
    }
    private static void send(Text text) {
        client.execute(() -> client.inGameHud.getChatHud().addMessage(text));
    }
    public static void addMessageInChat(Text text) {
        send(SendMessageHelper.getMessage(text));
    }
    public static void addMessageInChat(String message, boolean withPrefix) {
        send(SendMessageHelper.getMessage(message,withPrefix));
    }
    public static void addMessageInChat(Text text, boolean withPrefix) {
        send(SendMessageHelper.getMessage(text,withPrefix));
    }
    public static void addMessageInChat(String message) {
        send(SendMessageHelper.getMessage(message));
    }
    public static void sendDebugMessageInChat(Text text) {
        send(SendMessageHelper.getDebug(text));
    }
    public static void sendDebugMessageInChat(String message) {
        send(SendMessageHelper.getDebug(message));
    }
}
