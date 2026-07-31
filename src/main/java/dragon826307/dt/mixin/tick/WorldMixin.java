package dragon826307.dt.mixin.tick;

import dragon826307.dt.DraconicTech;
import dragon826307.dt.features.microtick.WorldTickingFlags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "tickBlockEntities",at = @At("HEAD"),cancellable = true)
    private void shouldTickBlockEntities(CallbackInfo ci){
        if (!DraconicTech.getWorldTickManager().getWorldTickFlag(WorldTickingFlags.BLOCK_ENTITIES)) ci.cancel();
    }
}
