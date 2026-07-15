package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.project.microtick.WorldTickingFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TickManager.class)
public class TickManagerMixin {
    @ModifyReturnValue(method = "shouldSkipTick",at = @At("RETURN"))
    private boolean shouldSkipEntity(boolean original, Entity entity) {
        return !WorldTickManager.getWorldTickFlag(WorldTickingFlags.ENTITIES) && !(entity instanceof PlayerEntity);
    }
    @ModifyReturnValue(method = "shouldTick",at = @At("RETURN"))
    private boolean shouldTick(boolean original) {
        return WorldTickManager.getTickFrozenLevel() == 0;
    }
}
