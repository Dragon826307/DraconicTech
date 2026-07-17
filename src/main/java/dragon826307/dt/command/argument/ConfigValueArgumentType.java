package dragon826307.dt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjectsInt;
import dragon826307.dt.config.ConfigType;
import dragon826307.dt.config.ConfigParserValue;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public record ConfigValueArgumentType(ConfigProjectsInt config) implements ArgumentType<Object> {
    private static final DynamicCommandExceptionType CONFIG_ERR = new DynamicCommandExceptionType(err -> Text.translatable("dt.config_value.invalid_range", err).withColor(Colors.RED));

    public static ConfigValueArgumentType setConfig(ConfigProjectsInt config) {
        return new ConfigValueArgumentType(config);
    }

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
        String rawString = reader.readString();
        if (DraconicTech.DEBUG) {
            DraconicTech.LOGGER.debug("[ConfigValueArgumentType]: ParseValue:{}", rawString);
        }
        if (config == null) {
            throw CONFIG_ERR.create("NULL Config");
        }
        Object parsedObject = ConfigProjectManager.parseValueFromString(rawString, config.getConfigType());
        if (parsedObject == null && config.getConfigType() != ConfigType.STRING) {
            throw CONFIG_ERR.create(rawString);
        }
        ConfigParserValue validation = ConfigProjectManager.parseValue(parsedObject, config);
        if (validation.isSuccess()) {
            return parsedObject;
        } else {
            throw CONFIG_ERR.create(validation.message());
        }
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
