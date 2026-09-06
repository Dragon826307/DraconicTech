package io.github.dragon826307.draconictech.mixin.tick;

import com.mojang.brigadier.tree.CommandNode;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CommandNode.class)
public class CommandNodeMixin implements InstantCommandNode {
    @Unique
    private boolean isInstantCommand = false;

    @Override
    public void draconictech$setInstant(boolean instant) {
        isInstantCommand = instant;
    }

    @Override
    public boolean draconictech$isInstant() {
        return isInstantCommand;
    }
}
