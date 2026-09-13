package io.github.dragon826307.draconictech.mixin;

import com.mojang.brigadier.tree.CommandNode;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CommandNode.class)
public class CommandNodeMixin implements InstantCommandNode {
    @Unique
    private int flags = 0;

    @Override
    public void draconictech$setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public int draconictech$getFlags() {
        return flags;
    }
}
