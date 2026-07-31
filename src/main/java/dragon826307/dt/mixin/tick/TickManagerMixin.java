package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.features.microtick.WorldTickingFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TickManager.class)
public class TickManagerMixin {
    @ModifyReturnValue(method = "shouldSkipTick",at = @At("RETURN"))
    private boolean shouldSkipEntity(boolean original, Entity entity) {
        return !DraconicTech.getWorldTickManager().getWorldTickFlag(WorldTickingFlags.ENTITIES) && !(entity instanceof PlayerEntity);
    }
    @Inject(method = "step",at = @At("HEAD"))
    private void onStep(CallbackInfo ci) {


    }
}
