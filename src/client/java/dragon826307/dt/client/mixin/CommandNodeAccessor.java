package dragon826307.dt.client.mixin;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandNode.class)
public interface CommandNodeAccessor {
    @Accessor("literals")
    Map<String, LiteralCommandNode<?>> getLiterals();
    @Accessor("arguments")
    Map<String, ArgumentCommandNode<?,?>> getArguments();
}
