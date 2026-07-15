package dragon826307.dt.util;

import com.mojang.brigadier.StringReader;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StringReaderHelper {
    /**
     * @param reader {@link StringReader}
     * <p>
     *     与原版的{@link StringReader#read()}类似，只不过可以处理转义字符
     * </p>
     * @return 当前读取的{@code char}，如果{@code \}位于末尾则直接返回{@code \}
     */
    public static char readOut(StringReader reader) {
        char c = reader.read();
        if (c == '\\') {
            if (!reader.canRead()) return '\\';
            char escape = reader.read();
            return escape;
        }else return c;
    }
    /**
     * @param reader {@link StringReader}
     * <p>
     *     与原版的{@link StringReader#readStringUntil(char)}类似。
     * <p>
     *     从当前{@link StringReader}的{@code cursor}位置开始读取，直到遇到指定字符，并将光标回退至该方法调用前的状态
     * @return 两侧边界的字符串，如果没有找到指定字符串，则返回{@code null}，可通过{@code include_bord}指定是否包含边界
     */
    @Nullable
    public static String readUntilNotThrow(@Nullable StringReader reader, char c, boolean include_bord) {
        if (reader == null) return null;
        int start = reader.getCursor();
        StringBuilder result = new StringBuilder();
        if (include_bord) result.append(reader.peek());
        while (reader.canRead()) {
            char read = readOut(reader);
            if (read == c) {
                if (include_bord) result.append(read);
                reader.setCursor(start);
                return result.toString();
            }
            result.append(read);
        }
        reader.setCursor(start);
        return null;
    }

    /**
     * @param reader {@link StringReader}
     * @param string 需要匹配的字符串
     * <p>
     *     与原版的{@link StringReader#readStringUntil(char)}类似，
     *     从当前的{@code cursor}开始读取，直到遇到匹配到字符串，同时将光标回退至该方法调用前的状态。
     * </p>
     * @return 读取到的字符串。如果直到结束都没有匹配的字符串，则返回{@code null}
     */
    @Nullable
    public static String readUntilMeet(@Nullable StringReader reader, String string) {
        if (reader == null) return null;
        if (string.isEmpty()) return "";
        int start = reader.getCursor();
        StringBuilder result = new StringBuilder();
        char c = string.charAt(0);
        String remain = string.substring(1);
        while (reader.canRead()) {
            char read = readOut(reader);
            if (read == c) {
                if (reader.getRemaining().startsWith(remain)) {
                    reader.setCursor(start);
                    return result.toString();
                }
            }
            result.append(read);
        }
        reader.setCursor(start);
        return null;
    }
}
