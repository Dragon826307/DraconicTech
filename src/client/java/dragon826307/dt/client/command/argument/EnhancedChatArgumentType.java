package dragon826307.dt.client.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.client.util.enhanced_chat.ChatFeatures;
import dragon826307.dt.client.util.enhanced_chat.ParseResult;
import dragon826307.dt.util.StringReaderHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class EnhancedChatArgumentType implements ArgumentType<String> {
    private static StringBuilder suggestPrefix;
    private static final Set<String> FEATURE_SET = ChatFeatures.getNames();
    private static final List<String> SUGGEST_LIST = new ArrayList<>();
    private static final DynamicCommandExceptionType FORMAT_ERR = new DynamicCommandExceptionType(err -> Text.translatable("dt.enhanced_argument.format_err",err).withColor(Colors.RED));
    public static EnhancedChatArgumentType eChatArgument() {return new EnhancedChatArgumentType();}
    private static void initSuggestions() {
        suggestPrefix = new StringBuilder();
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        FEATURE_SET.addAll(ChatFeatures.getNames());
        SUGGEST_LIST.clear();
        ParseResult result = parseNode(reader);
        if (result.isSuccess()) return result.parseValue();
        throw FORMAT_ERR.createWithContext(reader,result.errMessage());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String s : SUGGEST_LIST) {
            builder.suggest(s);
        }
        return builder.buildFuture();
    }

    public static String getMessage(CommandContext<?> context , String name) {
        return context.getArgument(name, String.class);
    }
    private static ParseResult parseNode(StringReader stringReader) {
        if (stringReader.getRemaining().isEmpty()) return ParseResult.success("");
        StringBuilder builder = new StringBuilder();
        while (stringReader.canRead()) {
            char c = StringReaderHelper.readOut(stringReader);
            if (c == '<') {
                String key_with_args = StringReaderHelper.readUntilNotThrow(stringReader,'>',false);
                if (key_with_args != null) {
                    List<String> key_parser = new ArrayList<>(Arrays.asList(key_with_args.split(":")));
                    String key = key_parser.getFirst();
                    key_parser.removeFirst();
                    ChatFeatures feature;
                    if ((feature = ChatFeatures.getByID(key)) != null) {
                        if (feature.getArgCount() == key_parser.size()) {
                            String end_tag = "</" + key + ">";
                            int index = stringReader.getRemaining().indexOf(end_tag);
                            if (index != -1) {
                                String content = StringReaderHelper.readUntilMeet(stringReader, end_tag);
                                ParseResult result = parseNode(new StringReader(content));
                                if (result.isSuccess()) {
                                    String parsed = result.parseValue();
                                    ParseResult value = feature.getParser().parse(parsed, key_parser.toArray(new String[0]));
                                    if (value.isSuccess()) {
                                        builder.append(value.parseValue());
                                        stringReader.setCursor(stringReader.getCursor() + index + end_tag.length());
                                    }else {
                                        return ParseResult.error(value.errMessage());
                                    }
                                }else {
                                    return ParseResult.error(result.errMessage());
                                }
                            }else {
                                return ParseResult.error("End tag not found!");
                            }
                        }else {
                            return ParseResult.error("Argument count mismatch!");
                        }
                    }else {
                        return ParseResult.error("Unknown ChatFeature: " + key);
                    }
                }else {
                    String prefix = builder.toString();
                    for (String s : FEATURE_SET) {
                        SUGGEST_LIST.add(prefix + s);
                    }
                    return ParseResult.error("Missing '>' in command argument");
                }
            }
            builder.append(c);
        }
        return ParseResult.success(builder.toString());
    }
}
