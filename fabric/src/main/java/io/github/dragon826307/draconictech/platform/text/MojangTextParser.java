package io.github.dragon826307.draconictech.platform.text;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.text.TextOpcodes;
import io.github.dragon826307.draconictech.api.text.UnknowTextOpcodeException;
import io.github.dragon826307.draconictech.api.util.bytes.BitUtil;
import io.github.dragon826307.draconictech.api.util.bytes.ByteStream;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.apache.commons.lang3.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MojangTextParser { //ojng
    private static Text parse(byte[] bytecode, boolean hasMagicNumber, byte ignoreOp) {
        if (bytecode == null || bytecode.length == 0) {
            return Text.empty();
        }
        ByteStream.Reader reader = new ByteStream.Reader(bytecode);
        MutableText root = Text.empty();
        MutableText currentNode = null;
        if (hasMagicNumber) {
            int magicNumber = reader.readInt();
            if (magicNumber != TextOpcodes.MAGIC_NUMBER) {
                throw new SerializationException("Invalid magic number 0x" + Integer.toHexString(magicNumber).toUpperCase());
            }
        }
        while (reader.hasRemaining()) {
            byte op = reader.readByte();
            if (op == 0) continue;
            int length = reader.readInt();
            if (op == ignoreOp) {
                reader.skipBytes(length);
                continue;
            }
            if ((currentNode == null) && !(op == TextOpcodes.OP_NODE_LITERAL) && !(op == TextOpcodes.OP_NODE_TRANSLATABLE)) {
                throw new UnknowTextOpcodeException(op, reader.getPos() - 5);
            }
            switch (op) {
                case TextOpcodes.OP_NODE_LITERAL -> {
                    String text = new String(reader.readBytes(length), StandardCharsets.UTF_8);
                    MutableText node = Text.literal(text);
                    root.append(node);
                    currentNode = node;
                }
                case TextOpcodes.OP_NODE_TRANSLATABLE -> {
                    boolean withFallback = reader.readByte() != 0;
                    String key = new String(reader.readBytes(length - 1), StandardCharsets.UTF_8);
                    List<String> values = new ArrayList<>();
                    while (reader.hasRemaining()) {
                        byte code = reader.readByte();
                        if (code == TextOpcodes.OP_TRANSLATABLE_VALUES) {
                            int l = reader.readInt();
                            values.add(new String(reader.readBytes(l), StandardCharsets.UTF_8));
                        } else {
                            reader.skipBytes(-1);
                            break;
                        }
                    }
                    MutableText node;
                    if (withFallback) {
                        node = Text.translatableWithFallback(key, Language.getInstance().get(key), values.toArray());
                    } else {
                        node = Text.translatable(key, values.toArray());
                    }
                    root.append(node);
                    currentNode = node;
                }
                case TextOpcodes.OP_NODE_BUILT_TEXT -> {
                    Text text = parse(reader.readBytes(length), false, ignoreOp);
                    root.append(text);
                    currentNode = null;
                }
                case TextOpcodes.OP_TRANSLATABLE_VALUES -> {
                    DraconicTech.LOGGER.warn("Invalid translatable values operation 0x{} at {}", Integer.toHexString(op), Integer.toHexString(reader.getPos() - length));
                    reader.skipBytes(length);
                }
                case TextOpcodes.OP_STYLE_COLOR, TextOpcodes.OP_GLOBAL_COLOR -> {
                    int argb = reader.readInt();
                    if (op == TextOpcodes.OP_STYLE_COLOR) {
                        currentNode.withColor(argb);
                    } else {
                        root.withColor(argb);
                    }
                }
                case TextOpcodes.OP_STYLE_FLAGS, TextOpcodes.OP_GLOBAL_STYLE -> {
                    byte flags = reader.readByte();
                    MutableText node;
                    if (op == TextOpcodes.OP_STYLE_FLAGS) {
                        node = currentNode;
                    } else {
                        node = root;
                    }
                    Style newStyle = node.getStyle()
                            .withBold(BitUtil.get(flags, 0))
                            .withItalic(BitUtil.get(flags, 1))
                            .withUnderline(BitUtil.get(flags, 2))
                            .withStrikethrough(BitUtil.get(flags, 3))
                            .withObfuscated(BitUtil.get(flags, 4));
                    node.setStyle(newStyle);
                }
                case TextOpcodes.OP_STYLE_HOVER_TEXT, TextOpcodes.OP_GLOBAL_HOVER_TEXT -> {
                    byte[] hoverBytes = reader.readBytes(length);
                    MutableText node;
                    if (op == TextOpcodes.OP_STYLE_HOVER_TEXT) {
                        node = currentNode;
                    } else {
                        node = root;
                    }
                    Text hoverTextComponent = parse(hoverBytes, false, TextOpcodes.OP_STYLE_HOVER_TEXT);
                    Style hoverStyle = node.getStyle().withHoverEvent(new HoverEvent.ShowText(hoverTextComponent));
                    node.setStyle(hoverStyle);
                }
                default -> throw new UnknowTextOpcodeException(op, reader.getPos() - 5);
            }
        }
        return root;
    }
    public static Text parse(byte[] bytecode) {
        return parse(bytecode, true, (byte) 0);
    }
}
