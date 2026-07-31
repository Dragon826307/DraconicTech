package dragon826307.dt.mixin.tick;

import dragon826307.dt.DraconicTech;
import dragon826307.dt.features.microtick.WorldTickingFlags;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tickNetworkIo",at = @At("HEAD"), cancellable = true)
    public void tickNetworkIo(CallbackInfo ci) {
        if (!DraconicTech.getWorldTickManager().getWorldTickFlag(WorldTickingFlags.PLAYER)) {
            //TODO
        }
    }
}
