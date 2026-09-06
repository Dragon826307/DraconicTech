package io.github.dragon826307.draconictech.mixin.tick;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(targets = "net.minecraft.server.world.ServerChunkLoadingManager$EntityTracker")
public interface EntityTrackerInvoker {
    @Invoker("updateTrackedStatus")
    void updateAllTrackedStatus(List<ServerPlayerEntity> players);
}
