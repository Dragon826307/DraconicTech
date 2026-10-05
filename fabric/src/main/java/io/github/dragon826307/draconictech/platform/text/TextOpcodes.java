package io.github.dragon826307.draconictech.platform.text;

//规范：1字节长度操作码 + 4字节长度数据长度 + N字节数据
//必须遵循此规定，即使数据长度是固定的
public final class TextOpcodes {
    //魔数
    public static final int MAGIC_NUMBER = 0x826307;
    //节点类型
    public static final byte OP_NODE_LITERAL = 0x01; //N字节UTF-8
    public static final byte OP_NODE_TRANSLATABLE = 0x02;//1字节default + N字节UTF-8(key)
    //属性
    public static final byte OP_TRANSLATABLE_VALUES = 0x10;//N字节UTF-8
    public static final byte OP_STYLE_COLOR = 0x11; //4字节ARGB
    public static final byte OP_STYLE_FLAGS = 0x12; //1字节 Bitmask: 0x01-Bold, 0x02-Italic, 0x04-Underline, 0x08-Strikethrough, 0x10-Obfuscated
    //事件
    public static final byte OP_STYLE_HOVER_TEXT  = 0x20; //N字节子字节流
}
