package dragon826307.dt.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextColorHelper {
    public static MutableText gradientColor(String string,int color1,int color2) {
        MutableText text = Text.empty();
        if (string == null || string.isEmpty()) return text;
        int length = string.length();
        if (length == 1 || color1 == color2) return text.append(string).withColor(color1);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        for (int i = 0; i < length; i++) {
            double t =  (double) i / (length - 1);
            int r = (int) Math.max(0, Math.min(255, r1 + (r2 - r1) * t));
            int g = (int) Math.max(0, Math.min(255, g1 + (g2 - g1) * t));
            int b = (int) Math.max(0, Math.min(255, b1 + (b2 - b1) * t));
            text.append(Text.literal(String.valueOf(string.charAt(i))).withColor(r << 16 | g << 8 | b));
        }
        return text;
    }
}
