package io.github.dragon826307.draconictech.mixin.tick;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkHolder.class)
public interface ChunkHolderInvoker {
    @Invoker("flushUpdates")
    void flushAllUpdates(WorldChunk chunk);
}
