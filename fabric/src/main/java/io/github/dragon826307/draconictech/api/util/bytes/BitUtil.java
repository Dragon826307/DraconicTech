package io.github.dragon826307.draconictech.api.util.bytes;

public final class BitUtil {
    public static int setInt(int origin, int index, boolean bl) {
        if (index < 0 || index >= 32) throw new IllegalArgumentException("index out of range");
        if (bl) {
            return origin | (1 << index);
        }else  {
            return origin & ~(1 << index);
        }
    }
    public static byte setByte(byte origin, int index, boolean bl) {
        int tmp = setInt(origin, index, bl);
        return (byte) tmp;
    }
    public static boolean get(int origin, int index) {
        if (index < 0 || index >= 32) throw new IllegalArgumentException("index out of range");
        return ((origin >>> index) & 1) == 1;
    }
}
