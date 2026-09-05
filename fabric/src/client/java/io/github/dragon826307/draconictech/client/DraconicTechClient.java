package io.github.dragon826307.draconictech.client;

import io.github.dragon826307.draconictech.AutoInitializeManager;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.InitializePhase;
import io.github.dragon826307.draconictech.client.render.RenderManager;
import io.github.dragon826307.draconictech.client.util.ClientChatHudHelper;
import io.github.dragon826307.draconictech.client.util.click_event.CommandBaseClickEvent;
import io.github.dragon826307.draconictech.network.Mod$DebugModeToggleC2SPacket;
import io.github.dragon826307.draconictech.util.TextColorHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class DraconicTechClient implements ClientModInitializer {
    public static long windowHandle = 114514;
    public static boolean DEBUG = false;
    public static boolean serverHadDraconicTech = false;
	@Override
	public void onInitializeClient() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Client...");
        AutoInitializeManager.scanAndRegister(name -> name.contains(".client."));
        AutoInitializeManager.trigger(InitializePhase.ON_MOD_INIT_CLIENT);
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> AutoInitializeManager.trigger(InitializePhase.ON_CLIENT_STARTED, client));
        KeyBinding openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("draconictech.key.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, KeyBinding.Category.create(Identifier.of(DraconicTech.MOD_ID, DraconicTech.MOD_ID))));
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (windowHandle == 114514) {
                windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
                Debug.init();
            }
            while (openMenuKeyBinding.wasPressed()) {
                //TODO
                RenderManager.Render3DBoxTask(114, 0,0,0,2,2,2).setRainbow(false).setLifetimeMs(5000).setColor(0x8000FFFF);
                RenderManager.Render3DBoxTask(514, 4,0,4,6,2,6).setRainbow(true).setLifetimeMs(5000).setColor(0x8000FFFF);
            }
        });
        WorldRenderEvents.END_MAIN.register(RenderManager::RenderAll);
    }
    private static final class Debug {
        private static final int[] SEQUENCE = {GLFW.GLFW_KEY_UP,GLFW.GLFW_KEY_UP,GLFW.GLFW_KEY_DOWN,GLFW.GLFW_KEY_DOWN,GLFW.GLFW_KEY_LEFT,GLFW.GLFW_KEY_RIGHT,GLFW.GLFW_KEY_LEFT,GLFW.GLFW_KEY_RIGHT,GLFW.GLFW_KEY_B,GLFW.GLFW_KEY_A,GLFW.GLFW_KEY_B,GLFW.GLFW_KEY_A};
        private static int sequenceIndex = 0;
        private static long lastPressTime = 0;
        private static final long MAX_INTERVAL_MS = 3333;
        private static GLFWKeyCallback oldCallback;
        private static void init() {
            oldCallback = GLFW.glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
                if (action == GLFW.GLFW_PRESS) {
                    if (System.currentTimeMillis() - lastPressTime > MAX_INTERVAL_MS) {
                        sequenceIndex = 0;
                    }
                    if (sequenceIndex < SEQUENCE.length && key == SEQUENCE[sequenceIndex]) {
                        sequenceIndex++;
                        lastPressTime = System.currentTimeMillis();
                        if (sequenceIndex == SEQUENCE.length) {
                            sequenceIndex = 0;
                            DEBUG = !DEBUG;
                            ClientChatHudHelper.addMessageInChat("Client-Side DEBUG MODE:" + (DEBUG ? "§aON" : "§cOFF"),true);
                            if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.hasPermissionLevel(4) && serverHadDraconicTech) {
                                ClientChatHudHelper.addMessageInChat(TextColorHelper.gradientColor("[TOGGLE SERVER-SIDE DEBUG MODE]", 0x549933, 0x2A4D1A).styled(style -> style.withBold(true).withClickEvent(CommandBaseClickEvent.run(() -> {
                                    ClientPlayNetworking.send(new Mod$DebugModeToggleC2SPacket());
                                    ClientChatHudHelper.sendDebugMessageInChat("Sending DebugModeToggleC2SPacket to server");
                                }))));
                            }
                        }
                    }else sequenceIndex = 0;
                }
                if (oldCallback != null) {
                    oldCallback.invoke(win, key, scancode, action, mods);
                }
            });
        }
    }
}