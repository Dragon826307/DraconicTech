package io.github.dragon826307.draconictech.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import io.github.dragon826307.draconictech.features.microtick.MicroTickingFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

@Mixin(value = ServerWorld.class,priority = 999)
public class ServerWorldMixin {
    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;tick()V"))
    private void shouldTickWorldStatus(BooleanSupplier shouldKeepTicking, CallbackInfo ci){

    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;tick()V"))
    private void shouldTickWorldBorder(WorldBorder instance, Operation<Void> original){
        original.call(instance);
    }
    @Inject(method = "tickWeather",at = @At("HEAD"))
    private void shouldTick(CallbackInfo ci){

    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 1),ordinal = 0)
    private boolean shouldCountAmbientDarkness(boolean original){
        return original;
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 2),ordinal = 0)
    private boolean shouldTickPendingEvents(boolean original){
        return original;
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",ordinal = 0))
    private <T> void shouldTickPendingBlocks(WorldTickScheduler<?> instance, long time, int maxTicks, BiConsumer<BlockPos, T> ticker, Operation<Void> original){
        original.call(instance, time, maxTicks, ticker);
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() > 1 && !MicroTickManager.INSTANCE.getMicroTickFlag(MicroTickingFlags.PENDING_BLOCK)) {
            MicroTickManager.INSTANCE.tryFreeze(Text.of("test"));
        }
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",ordinal = 1))
    private <T> void shouldTickPendingFluid(WorldTickScheduler<?> instance, long time, int maxTicks, BiConsumer<BlockPos, T> ticker, Operation<Void> original){
        original.call(instance, time, maxTicks, ticker);
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() > 1 && !MicroTickManager.INSTANCE.getMicroTickFlag(MicroTickingFlags.PENDING_FLUID)) {
            MicroTickManager.INSTANCE.tryFreeze(Text.of("test2"));
        }
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 3),ordinal = 0)
    private boolean shouldTickRaid(boolean original){
        return original;
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 4),ordinal = 0)
    private boolean shouldTickBlockEvents(boolean original){
        return original;
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 5),ordinal = 0)
    private boolean shouldTickIdleTime(boolean original){
        return original;
    }
    @ModifyVariable(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "LOAD",ordinal = 6),ordinal = 0)
    private boolean shouldTickDragonFight(boolean original){
        return original;
    }
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
    private void shouldTickBlockEntities(ServerWorld instance, Operation<Void> original){
        original.call(instance);
    }
}
