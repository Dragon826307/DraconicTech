package dragon826307.dt.mixin.tick;

import dragon826307.dt.DraconicTech;
import dragon826307.dt.project.microtick.WorldTickingFlags;
import net.minecraft.server.world.ServerEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityManager.class)
public class ServerEntityManagerMixin {
    @Inject(method = "loadChunks",at = @At("HEAD"),cancellable = true)
    private void onLoadChunks(CallbackInfo ci) {
        if (!DraconicTech.getWorldTickManager().getWorldTickFlag(WorldTickingFlags.ENTITY_LOAD_CHUNK)) ci.cancel();
    }
    @Inject(method = "unloadChunks",at = @At("HEAD"),cancellable = true)
    private void onUnloadChunks(CallbackInfo ci) {
        if (!DraconicTech.getWorldTickManager().getWorldTickFlag(WorldTickingFlags.ENTITY_UNLOAD_CHUNK)) ci.cancel();
    }
}
