package io.github.dragon826307.draconictech.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ArgumentBuilder.class)
public abstract class ArgumentBuilderMixin implements InstantCommandBuilder {
    @Unique
    private int flags = 0;

    @Override
    public void draconictech$setSingleFlag(int flag, boolean bl) {
        flags = (flags | flag) & (bl ? -1 : ~flag);
    }

    @Override
    public int draconictech$getFlags() {
        return flags;
    }
}
