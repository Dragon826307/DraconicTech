package io.github.dragon826307.draconictech.mixin.tick;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ArgumentBuilder.class)
public abstract class ArgumentBuilderMixin implements InstantCommandBuilder {
    @Unique
    private boolean isInstantCommand = false;

    @Override
    public void draconictech$setInstant(boolean instant) {
        this.isInstantCommand = instant;
    }

    @Override
    public boolean draconictech$isInstant() {
        return this.isInstantCommand;
    }
}
