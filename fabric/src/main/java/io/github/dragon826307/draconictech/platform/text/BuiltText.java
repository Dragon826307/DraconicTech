package io.github.dragon826307.draconictech.platform.text;

import java.util.Arrays;

public final class BuiltText {
    private final byte[] bytecode;
    private byte[] bytecodeRaw;
    private Object parsedNativeComponent = null;
    private boolean isParsed = false;

    BuiltText(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    /**
     * @return 立即复制一个byte[]作为输出
     */
    @SuppressWarnings("unused")
    public byte[] getBytecode() {
        return bytecode.clone();
    }

    /**
     * @return 获取一个不带魔数头的byte[]，总是获取到同一个引用
     */
    byte[] getBytecodeRaw() {
        if (bytecodeRaw == null) {
            bytecodeRaw = Arrays.copyOfRange(bytecode,4, bytecode.length);
        }
        return bytecodeRaw;
    }

    public BuiltText parse() {
        if (!isParsed) {
            this.parsedNativeComponent = MojangTextParser.parse(this.bytecode);
            this.isParsed = true;
        }
        return this;
    }
    /**
     * 获取Minecraft文本组件，必须先调用{@link BuiltText#parse()}才可调用此方法
     * @throws IllegalStateException 如果文本组件未被解析
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        if (!isParsed) {
            throw new IllegalStateException("Text has not been parsed yet! Parse() must be called before called get().");
        }
        return (T) parsedNativeComponent;
    }
    public boolean isEmpty() {
        return getBytecodeRaw().length == 0;
    }
}
