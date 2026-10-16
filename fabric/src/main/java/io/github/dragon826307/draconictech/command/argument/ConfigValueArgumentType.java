package io.github.dragon826307.draconictech.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.dragon826307.draconictech.api.config.AbstractConfigType;
import io.github.dragon826307.draconictech.api.text.BuiltText;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;

public record ConfigValueArgumentType<T>(AbstractConfigType<T> config) implements ArgumentType<AbstractConfigType<T>> {
    private static final DynamicCommandExceptionType CONFIG_ERR = new DynamicCommandExceptionType(err -> TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("invalid_value"),true).applyBuiltText((BuiltText) err).done().build().parse().get());
    public static <T> ArgumentType<AbstractConfigType<T>> config(AbstractConfigType<T> config) {
        return new ConfigValueArgumentType<>(config);
    }

    @Override
    public AbstractConfigType<T> parse(StringReader reader) throws CommandSyntaxException {
        String raw_input = reader.readString();
        T parsed_value = config.parseStringToConfig(raw_input);
        if (parsed_value == null) {
            throw CONFIG_ERR.create(config.getInvalidReason());
        }
        if (!config.isConfigValueValid(parsed_value)) {
            throw CONFIG_ERR.create(config.getInvalidReason());
        }
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNewValue(CommandContext<?> context, String name) {
        System.out.println(context.getArgument(name, String.class));
        return (T) context.getArgument(name, String.class);
    }
}
