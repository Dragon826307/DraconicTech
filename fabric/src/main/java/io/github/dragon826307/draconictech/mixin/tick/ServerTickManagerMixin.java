package io.github.dragon826307.draconictech.mixin.tick;

import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerTickManager.class)
public class ServerTickManagerMixin{
    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(MinecraftServer server, CallbackInfo ci){
        new MicroTickManager(server);
    }
}
