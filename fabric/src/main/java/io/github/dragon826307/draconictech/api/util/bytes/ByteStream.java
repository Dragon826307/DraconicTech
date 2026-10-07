package io.github.dragon826307.draconictech.api.util.bytes;

import java.util.Arrays;

public final class ByteStream {
    public static final class Writer {
        private byte[] buffer;
        private int count;
        public Writer(int initialCapacity) {
            buffer = new byte[initialCapacity];
        }
        public Writer() {
            this(64);
        }
        public void writeByte(byte b) {
            ensureCapacity(count + 1);
            buffer[count++] = b;
        }
        public void writeInt(int v) {
            ensureCapacity(count + 4);
            buffer[count++] = (byte) (v >>> 24);
            buffer[count++] = (byte) (v >>> 16);
            buffer[count++] = (byte) (v >>> 8);
            buffer[count++] = (byte) v;
        }
        public void writeBytes(byte[] b) {
            ensureCapacity(count + b.length);
            System.arraycopy(b, 0, buffer, count, b.length);
            count += b.length;
        }
        public byte[] toByteArray() {
            return Arrays.copyOf(buffer, count);
        }
        private void ensureCapacity(int minCapacity) {
            if (buffer.length < minCapacity) {
                int newCapacity = buffer.length << 1;
                if (newCapacity < minCapacity) newCapacity = minCapacity;
                buffer = Arrays.copyOf(buffer, newCapacity);
            }
        }
    }
    public static final class Reader {
        private final byte[] buffer;
        private int pos;
        public Reader(byte[] buf) {
            this.buffer = buf;
        }
        public boolean hasRemaining() {
            return pos < buffer.length;
        }
        public byte readByte() {
            return buffer[pos++];
        }
        public int readInt() {
            int tmp = (buffer[pos] & 0xFF) << 24 | (buffer[pos + 1] & 0xFF) << 16 | (buffer[pos + 2] & 0xFF) << 8 | (buffer[pos + 3] & 0xFF);
            pos += 4;
            return tmp;
        }
        public byte[] readBytes(int len) {
            byte[] res = new byte[len];
            System.arraycopy(buffer, pos, res, 0, len);
            pos += len;
            return res;
        }
        public void skipBytes(int len) {
            pos += len;
        }
        public int getPos() {
            return pos;
        }
    }
}
