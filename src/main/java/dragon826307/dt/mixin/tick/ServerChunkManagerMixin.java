package dragon826307.dt.mixin.tick;

import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.project.microtick.WorldTickingFlags;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @Inject(method = "tick",at = @At("HEAD"),cancellable = true)
    private static void onTick(BooleanSupplier shouldKeepTicking, boolean tickChunks, CallbackInfo ci){
        if (!WorldTickManager.getWorldTickFlag(WorldTickingFlags.CHUNK)) ci.cancel();
    }
}
