package io.github.dragon826307.draconictech.platform.text;

public class UnknowTextOpcodeException extends RuntimeException {
    public UnknowTextOpcodeException(byte op, int pos) {
        super("Failed to decode byte " + op + " at position " + pos);
    }
}
