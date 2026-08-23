package dragon826307.dt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.util.ServerTranslationUtil;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public record TickHaltFlagArgumentType(String flagName) implements ArgumentType<String> {
    private static final String UNKNOW_FLAG_KEY = ServerTranslationUtil.getFullKey("tick_arg_err");
    private static final DynamicCommandExceptionType UNKNOW_FLAG = new DynamicCommandExceptionType(err -> ServerTranslationUtil.getTranslatedWithFallback(UNKNOW_FLAG_KEY, err));

    public static TickHaltFlagArgumentType flags(String name) {
        return new TickHaltFlagArgumentType(name);
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String read = stringReader.readString();
        if (!read.equals(flagName)) throw UNKNOW_FLAG.create(stringReader);
        return flagName;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ArgumentType.super.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
