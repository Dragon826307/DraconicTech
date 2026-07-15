package dragon826307.dt.client.util;

import dragon826307.dt.util.SendMessageHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class ClientChatHudHelper {
    private static MinecraftClient mc;
    public static void init() {
        mc = MinecraftClient.getInstance();
    }
    private static void send(Text text) {
        mc.execute(() -> mc.inGameHud.getChatHud().addMessage(text));
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
