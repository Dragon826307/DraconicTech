package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.project.microtick.WorldTickingFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 0),ordinal = 0)
    private boolean shouldTickWorldStatus(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.WORLD_BORDER) || WorldTickManager.getWorldTickFlag(WorldTickingFlags.WEATHER);
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;tick()V"))
    private void shouldTickWorldBorder(WorldBorder instance, Operation<Void> original){
        if (WorldTickManager.getWorldTickFlag(WorldTickingFlags.WORLD_BORDER)) original.call(instance);
    }
    @Inject(method = "tickWeather",at = @At("HEAD"),cancellable = true)
    private void shouldTick(CallbackInfo ci){
        if (!WorldTickManager.getWorldTickFlag(WorldTickingFlags.WEATHER))ci.cancel();
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 1),ordinal = 0)
    private boolean shouldCountAmbientDarkness(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.AMBIENT_DARKNESS);
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 2),ordinal = 0)
    private boolean shouldTickPendingEvents(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.PENDING_BLOCK) || WorldTickManager.getWorldTickFlag(WorldTickingFlags.PENDING_FLUID);
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",ordinal = 0))
    private <T> void shouldTickPendingBlocks(WorldTickScheduler<?> instance, long time, int maxTicks, BiConsumer<BlockPos, T> ticker, Operation<Void> original){
        if (WorldTickManager.getWorldTickFlag(WorldTickingFlags.PENDING_BLOCK)) original.call(instance, time, maxTicks, ticker);
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",ordinal = 1))
    private <T> void shouldTickPendingFluid(WorldTickScheduler<?> instance, long time, int maxTicks, BiConsumer<BlockPos, T> ticker, Operation<Void> original){
        if (WorldTickManager.getWorldTickFlag(WorldTickingFlags.PENDING_FLUID)) original.call(instance, time, maxTicks, ticker);
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 3),ordinal = 0)
    private boolean shouldTickRaid(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.RAID);
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 4),ordinal = 0)
    private boolean shouldTickBlockEvents(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.BLOCK_EVENT);
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 5),ordinal = 0)
    private boolean shouldTickIdleTime(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.ENTITIES);
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 6),ordinal = 0)
    private boolean shouldTickDragonFight(boolean original){
        return WorldTickManager.getWorldTickFlag(WorldTickingFlags.DRAGON_FIGHT);
    }
}
