package io.github.dragon826307.draconictech.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.dragon826307.draconictech.config.AbstractConfigType;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import io.netty.util.internal.UnstableApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Colors;

//TODO
@UnstableApi
@Deprecated
public record ConfigValueArgumentType<T>(AbstractConfigType<T> config) implements ArgumentType<Object> {
    private static final DynamicCommandExceptionType CONFIG_ERR = new DynamicCommandExceptionType(err -> ServerTranslationUtil.getFullKeyAndTryTranslate("invalid_value",err).withColor(Colors.RED));

    public static <T> ConfigValueArgumentType<T> config(AbstractConfigType<T> config) {
        return new ConfigValueArgumentType<>(config);
    }

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
//        int start = reader.getCursor();
//        String raw_input = reader.readString();
//        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//            T raw_parse_result = config.parseStringToConfigValue(raw_input);
//            if (raw_parse_result == null || config.getConfigValidator().check(raw_parse_result)) {
//                throw CONFIG_ERR.create(config.getInvalidReason());
//            }
//        }else {
//
//        }
        return null;
    }

    public static Object getValueOrNull(CommandContext<?> context, String name) {
        return context.getArgument(name, Object.class);
    }

    public static Object getValueOrThrow(CommandContext<?> context, String name) throws CommandSyntaxException {
        Object obj = getValueOrNull(context, name);
        if (obj == null) throw CONFIG_ERR.create("NULL");
        return obj;
    }
}
