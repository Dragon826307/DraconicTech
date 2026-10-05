package io.github.dragon826307.draconictech.platform.text;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.bytes.BitUtil;
import io.github.dragon826307.draconictech.util.bytes.ByteStream;
import org.apache.commons.lang3.SerializationException;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public final class TextBuilder implements Cloneable{
    private static final BuiltText EMPTY_TEXT = TextBuilder.start().build().parse();
    private ByteStream.Writer stream = new ByteStream.Writer();

    //缓存属性
    private boolean hasActiveNode = false;
    private byte activeNodeType = 0;
    private byte[] activeContentBytes = null;
    private byte[] translateValues = null;
    private boolean translatedFallback = false;
    private int activeColor = -1; //-1表示未设置，即0xFFFFFFFF，即纯白色
    private byte activeFormattingMask = 0;
    private byte[] activeHoverBytecode = null;

    private TextBuilder() {}
    public static TextBuilder start() {
        TextBuilder textBuilder = new TextBuilder();
        textBuilder.stream.writeInt(TextOpcodes.MAGIC_NUMBER);
        return textBuilder;
    }
    public static BuiltText of(String text) {
        return TextBuilder.start().apply(text).build();
    }
    public static BuiltText empty() {
        return EMPTY_TEXT;
    }

    public TextBuilder clone() {
        try {
            TextBuilder cloned = (TextBuilder) super.clone();

            //深拷贝
            cloned.stream = new ByteStream.Writer();
            cloned.stream.writeBytes(this.stream.toByteArray());
            if (this.activeContentBytes != null) cloned.activeContentBytes = this.activeContentBytes.clone();
            if (this.translateValues != null) cloned.translateValues = this.translateValues.clone();
            if (this.activeHoverBytecode != null) cloned.activeHoverBytecode = this.activeHoverBytecode.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone TextBuilder", e);
        }
    }

    public TextBuilder apply(String literal) {
        flushCurrentNode();
        hasActiveNode = true;
        activeNodeType = TextOpcodes.OP_NODE_LITERAL;
        activeContentBytes = literal.getBytes(StandardCharsets.UTF_8);
        return this;
    }
    public TextBuilder applyTranslatable(String key, boolean withFallback, Object... values) {
        flushCurrentNode();
        hasActiveNode = true;
        translatedFallback = withFallback;
        activeNodeType = TextOpcodes.OP_NODE_TRANSLATABLE;
        activeContentBytes = key.getBytes(StandardCharsets.UTF_8);
        ByteStream.Writer tmp = new ByteStream.Writer();
        for (Object value : values) {
            tmp.writeByte(TextOpcodes.OP_TRANSLATABLE_VALUES);
            byte[] raw = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
            tmp.writeInt(raw.length);
            tmp.writeBytes(raw);
        }
        translateValues = tmp.toByteArray();
        return this;
    }
    public TextBuilder apply(BuiltText literal) {
        flushCurrentNode();
        this.hasActiveNode = false;
        activeNodeType = 0;
        stream.writeBytes(literal.getBytecodeRaw());
        return this;
    }

    public TextBuilder setColor(int argb) {
        checkHasActiveNode();
        activeColor = argb;
        return this;
    }
    public TextBuilder setBold(boolean bold) {
        checkHasActiveNode();
        activeFormattingMask = BitUtil.setByte(activeFormattingMask, 0, bold);
        return this;
    }
    public TextBuilder setItalic(boolean italic) {
        checkHasActiveNode();
        activeFormattingMask = BitUtil.setByte(activeFormattingMask, 1, italic);
        return this;
    }
    public TextBuilder setUnderline(boolean underline) {
        checkHasActiveNode();
        activeFormattingMask = BitUtil.setByte(activeFormattingMask, 2, underline);
        return this;
    }
    public TextBuilder setStrikethrough(boolean strikethrough) {
        checkHasActiveNode();
        activeFormattingMask = BitUtil.setByte(activeFormattingMask, 3, strikethrough);
        return this;
    }
    public TextBuilder setObfuscated(boolean obfuscated) {
        checkHasActiveNode();
        activeFormattingMask = BitUtil.setByte(activeFormattingMask, 4, obfuscated);
        return this;
    }

    public TextBuilder setHover(BuiltText builtText) {
        checkHasActiveNode();
        if (builtText == null) {
            activeHoverBytecode = null;
            return this;
        }
        activeHoverBytecode = builtText.getBytecodeRaw().clone();
        return this;
    }

    public BuiltText build() {
        flushCurrentNode();
        return new BuiltText(byteSanitizer(stream.toByteArray()));
    }

    //刷入字节流
    private void flushCurrentNode() {
        if (!hasActiveNode || activeNodeType == 0) return;
        if (activeNodeType == TextOpcodes.OP_NODE_LITERAL) {
            stream.writeByte(TextOpcodes.OP_NODE_LITERAL);
            stream.writeInt(activeContentBytes.length);
            stream.writeBytes(activeContentBytes);
        } else if (activeNodeType == TextOpcodes.OP_NODE_TRANSLATABLE) {
            stream.writeByte(TextOpcodes.OP_NODE_TRANSLATABLE);
            stream.writeInt(activeContentBytes.length + 1);
            stream.writeByte((byte) (translatedFallback ? 1 : 0));
            stream.writeBytes(activeContentBytes);
            if (translateValues != null && translateValues.length > 0) {
                stream.writeBytes(translateValues);
            }
        }
        if (activeColor != -1) {
            stream.writeByte(TextOpcodes.OP_STYLE_COLOR);
            stream.writeInt(4);
            stream.writeInt(activeColor);
        }
        if (activeFormattingMask != 0) {
            stream.writeByte(TextOpcodes.OP_STYLE_FLAGS);
            stream.writeInt(1);
            stream.writeByte(activeFormattingMask);
        }
        if (activeHoverBytecode != null && activeHoverBytecode.length > 0) {
            stream.writeByte(TextOpcodes.OP_STYLE_HOVER_TEXT);
            stream.writeInt(activeHoverBytecode.length);
            stream.writeBytes(activeHoverBytecode);
        }
        resetActiveState();
    }
    //重置缓存
    private void resetActiveState() {
        hasActiveNode = false;
        translateValues = null;
        translatedFallback = false;
        activeNodeType = 0;
        activeContentBytes = null;
        activeColor = -1;
        activeFormattingMask = 0;
        activeHoverBytecode = null;
    }
    private void checkHasActiveNode() {
        if (!hasActiveNode) {
            throw new IllegalStateException("No activated node! Must first call apply() or applyTranslateable() to set text properties.");
        }
    }
    //清洗器
    private static byte[] byteSanitizer(byte[] raw, boolean hasMagicNumber, byte ignoreOp) {
        ByteStream.Writer writer = new ByteStream.Writer(raw.length);
        ByteStream.Reader reader = new ByteStream.Reader(raw);
        if (hasMagicNumber) {
            int magicNumber = reader.readInt();
            if (magicNumber != TextOpcodes.MAGIC_NUMBER) {
                throw new SerializationException("Invalid magic number 0x" + Integer.toHexString(magicNumber).toUpperCase());
            }
            writer.writeInt(TextOpcodes.MAGIC_NUMBER);
        }
        byte currentNode = 0;
        while (reader.hasRemaining()) {
            byte op = reader.readByte();
            if (op == 0) continue;
            int length = reader.readInt();
            if (op == ignoreOp) {
                reader.skipBytes(length);
                continue;
            }
            if ((currentNode == 0) && !(op == TextOpcodes.OP_NODE_LITERAL) && !(op == TextOpcodes.OP_NODE_TRANSLATABLE)) {
                throw new UnknowTextOpcodeException(op, reader.getPos() - 5);
            }
            switch (op) {
                case TextOpcodes.OP_NODE_LITERAL -> {
                    currentNode = op;
                    writer.writeByte(TextOpcodes.OP_NODE_LITERAL);
                    writer.writeInt(length);
                    writer.writeBytes(reader.readBytes(length));
                }
                case TextOpcodes.OP_NODE_TRANSLATABLE -> {
                    currentNode = op;
                    writer.writeByte(TextOpcodes.OP_NODE_TRANSLATABLE);
                    writer.writeInt(length);
                    writer.writeBytes(reader.readBytes(length));
                }
                case TextOpcodes.OP_TRANSLATABLE_VALUES -> {
                    if (currentNode != TextOpcodes.OP_NODE_TRANSLATABLE) {
                        DraconicTech.LOGGER.warn("Invalid text operation code at {} \n Only Translatable node can have translatable values!", Integer.toString(reader.getPos()));
                        reader.skipBytes(length);
                        continue;
                    }
                    writer.writeByte(TextOpcodes.OP_TRANSLATABLE_VALUES);
                    writer.writeInt(length);
                    writer.writeBytes(reader.readBytes(length));
                }
                case TextOpcodes.OP_STYLE_COLOR -> {
                    writer.writeByte(op);
                    writer.writeInt(length);
                    writer.writeInt(reader.readInt());
                }
                case TextOpcodes.OP_STYLE_FLAGS -> {
                    writer.writeByte(op);
                    writer.writeInt(length);
                    writer.writeByte(reader.readByte());
                }
                case TextOpcodes.OP_STYLE_HOVER_TEXT -> {
                    byte[] sanitizedHover = byteSanitizer(reader.readBytes(length), false, TextOpcodes.OP_STYLE_HOVER_TEXT);
                    writer.writeByte(TextOpcodes.OP_STYLE_HOVER_TEXT);
                    writer.writeInt(sanitizedHover.length);
                    writer.writeBytes(sanitizedHover);
                }
                default -> {
                    DraconicTech.LOGGER.error("Unknow text operation code 0x{} at position {}! Skip {} bytes.", Integer.toHexString(op), reader.getPos() - 5, Integer.toString(length));
                    reader.skipBytes(length);
                }
            }
        }
        return writer.toByteArray();
    }

    /**
     * @return 一串合法的文本组件操作码
     */
    public static byte[] byteSanitizer(byte[] raw) {
        return byteSanitizer(raw, true, (byte) 0);
    }
}
