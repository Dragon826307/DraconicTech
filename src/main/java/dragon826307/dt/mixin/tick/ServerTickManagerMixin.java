package dragon826307.dt.mixin.tick;

import dragon826307.dt.events.ObjectCreatedEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerTickManager.class)
public class ServerTickManagerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(MinecraftServer server, CallbackInfo ci) {
        ServerTickManager self = (ServerTickManager)(Object)this;
        ObjectCreatedEvents.SERVER_TICK_MANAGER.invoker().onCreated(self);
    }
}
