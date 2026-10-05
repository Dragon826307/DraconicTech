package io.github.dragon826307.draconictech.client;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitializeManager;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.client.render.RenderManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DraconicTechClient implements ClientModInitializer {
    public static long windowHandle = 114514;
    public static boolean serverHadDraconicTech = false;
	@Override
	public void onInitializeClient() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Client...");
        try {
            Method scanAndRegister = AutoInitializeManager.class.getDeclaredMethod("scanAndRegister");
            scanAndRegister.setAccessible(true);
            scanAndRegister.invoke(null);
            Method trigger = AutoInitializeManager.class.getDeclaredMethod("trigger", InitializePhase.class, Object[].class);
            trigger.setAccessible(true);
            trigger.invoke(null, InitializePhase.ON_MOD_INIT_CLIENT, ArrayUtils.EMPTY_OBJECT_ARRAY);
            ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
                try {
                    trigger.invoke(null, InitializePhase.ON_CLIENT_STARTED, ArrayUtils.toArray(client));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ExceptionInInitializerError(e);
                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ExceptionInInitializerError(e);
        }


        KeyBinding openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("draconictech.key.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, KeyBinding.Category.create(Identifier.of(DraconicTech.MOD_ID, DraconicTech.MOD_ID))));
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (windowHandle == 114514) {
                windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
            }
            while (openMenuKeyBinding.wasPressed()) {
                //TODO
                RenderManager.Render3DBoxTask(114, 0,0,0,2,2,2).setRainbow(false).setLifetimeMs(5000).setColor(0x8000FFFF);
                RenderManager.Render3DBoxTask(514, 4,0,4,6,2,6).setRainbow(true).setLifetimeMs(5000).setColor(0x8000FFFF);
            }
        });
        WorldRenderEvents.END_MAIN.register(RenderManager::RenderAll);
    }
}