package dragon826307.dt.client.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.client.util.enhanced_chat.ChatFeatures;
import dragon826307.dt.client.util.enhanced_chat.EnhancedChatParseResult;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class EnhancedChatArgumentType implements ArgumentType<String> {
    private static final Text ESCAPE_AT_END = Text.translatable("dt.e_chat.escape_at_end");
    private static final Text MISS_CHAR = Text.translatable("dt.e_chat.miss_char");
    private static final Set<String> FEATURE_SET = ChatFeatures.getNames();
    private static final List<String> SUGGEST_LIST = new ArrayList<>();
    private static final DynamicCommandExceptionType FORMAT_ERR = new DynamicCommandExceptionType(err -> Text.translatable("dt.enhanced_argument.format_err",err).withColor(Colors.RED));
    private static int argument_start = 0;
    public static EnhancedChatArgumentType eChatArgument() {return new EnhancedChatArgumentType();}
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        FEATURE_SET.addAll(ChatFeatures.getNames());
        SUGGEST_LIST.clear();
        argument_start = reader.getCursor();
        return parseNode(reader, null);
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
    private static String parseNode(StringReader reader,@Nullable ChatFeatures parent) throws CommandSyntaxException {
        StringBuilder result = new StringBuilder();
        while (reader.canRead()) {
            int currentCursor = reader.getCursor();
            if (reader.peek() == '\\') {
                reader.skip();
                if (reader.canRead()) {
                    result.append(reader.read());
                } else {
                    reader.setCursor(currentCursor);
                    throw FORMAT_ERR.createWithContext(reader, ESCAPE_AT_END);
                }
                continue;
            }
            if (parent != null && reader.peek() == '<') {
                String expectedCloseTag = "</" + parent.getID() + ">";
                if (reader.getRemaining().startsWith(expectedCloseTag)) {
                    break;
                }
            }
            if (reader.peek() == '<') {
                int tagStartCursor = reader.getCursor();
                reader.skip();
                String tagContent = readUntilUnescaped(reader);
                if (!reader.canRead()) {
                    reader.setCursor(tagStartCursor);
                    String prefix = reader.getString().substring(argument_start, tagStartCursor + 1);
                    ChatFeatures f = null;
                    if (tagContent.endsWith(":")) f = ChatFeatures.getByID(tagContent.substring(0, tagContent.length()-1));
                    if (f == null) {
                        for (String s : FEATURE_SET) {
                            SUGGEST_LIST.add(prefix + s);
                        }
                    }else {
                        String[] suggest = f.getSuggestions();
                        reader.setCursor(argument_start);
                        if (suggest != null) {
                            for (String s : suggest) {
                                SUGGEST_LIST.add(reader.getRemaining() + s + ">");
                            }
                        }else SUGGEST_LIST.add(reader.getRemaining() + ">");
                        reader.setCursor(tagStartCursor);
                    }
                    throw FORMAT_ERR.createWithContext(reader, MISS_CHAR);
                }
                reader.skip();
                String[] parts = tagContent.split(":");
                String tagName = parts[0];
                FEATURE_SET.remove(tagName);
                if (tagName.startsWith("/")) {
                    reader.setCursor(tagStartCursor);
                    throw FORMAT_ERR.createWithContext(reader, Text.translatable("dt.e_chat.unclose_tag",tagName));
                }
                ChatFeatures feature = ChatFeatures.getByID(tagName);
                if (feature == null) {
                    reader.setCursor(tagStartCursor);
                    throw FORMAT_ERR.createWithContext(reader, Text.translatable("dt.e_chat.unknow",tagName));
                }
                if (parts.length - 1 != feature.getArgCount()) {
                    reader.setCursor(tagStartCursor);
                    throw FORMAT_ERR.createWithContext(reader, Text.translatable("dt.e_chat.args_count_err",tagName,feature.getArgCount()));
                }
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);
                String innerContent = parseNode(reader, feature);
                int closeTagStart = reader.getCursor();
                String expectedCloseTag = "</" + feature.getID() + ">";
                if (!reader.getRemaining().startsWith(expectedCloseTag)) {
                    reader.setCursor(argument_start);
                    SUGGEST_LIST.add(reader.getRemaining() + expectedCloseTag);
                    reader.setCursor(closeTagStart);
                    throw FORMAT_ERR.createWithContext(reader, Text.translatable("dt.e_chat.parsed_fail",tagName));
                }
                reader.setCursor(closeTagStart + expectedCloseTag.length());
                EnhancedChatParseResult enhancedChatParseResult = feature.getParser().parse(innerContent, args);
                if (!enhancedChatParseResult.isSuccess()) {
                    reader.setCursor(tagStartCursor);
                    throw FORMAT_ERR.createWithContext(reader, Text.translatable(enhancedChatParseResult.errMessage(), enhancedChatParseResult.args()));
                }
                result.append(enhancedChatParseResult.parseValue());

            } else {
                result.append(reader.read());
            }
        }
        return result.toString();
    }
    private static String readUntilUnescaped(StringReader reader) {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
            if (reader.peek() == '\\') {
                reader.skip();
                if (reader.canRead()) builder.append(reader.read());
                continue;
            }
            if (reader.peek() == '>') {
                break;
            }
            builder.append(reader.read());
        }
        return builder.toString();
    }
}
