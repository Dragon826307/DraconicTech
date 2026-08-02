package dragon826307.dt.mixin.tick;

import net.minecraft.server.world.ServerEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityManager.class)
public class ServerEntityManagerMixin {
    @Inject(method = "loadChunks",at = @At("HEAD"),cancellable = true)
    private void onLoadChunks(CallbackInfo ci) {

    }
    @Inject(method = "unloadChunks",at = @At("HEAD"),cancellable = true)
    private void onUnloadChunks(CallbackInfo ci) {

    }
}
