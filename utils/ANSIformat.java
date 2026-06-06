package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// hex to ANSI:
// https://stackoverflow.com/questions/65727751/convert-base-10-colors-or-hex-colors-to-ansi-colors-in-javascript

// documentaion + design pattern approchs

final public class ANSIformat { // \u001B[S;38;2;R;G;Bm
    private final static String keyANSI = "\u001B[";

    public static String format(String text, String color, List<Integer> style, String background, boolean Reset) {
        if(style.size() == 0) style.add(0);
        // final String keyANSI = "\u001B[";
        final String finalStyle = Splitter.formatANSI(style);
        final String keyColorANSI = formatText(color);
        final String bgColorANSI = formatBG(background);

        String seaparte1 = keyColorANSI != null ? ";" + keyColorANSI : "";
        String seaparte2 = bgColorANSI != null ? ";" + bgColorANSI : "";

        String merge = keyANSI + finalStyle + seaparte1 + seaparte2 + "m" + text;
        return Reset ? merge + ANSI.Reset : merge;
    }

    public static String format(String text, String color, Style style, String background, boolean Reset) {
        final String finalStyle = String.valueOf(style.getStyle());
        final String keyColorANSI = formatText(color);
        final String bgColorANSI = formatBG(background);

        String seaparte1 = keyColorANSI != null ? ";" + keyColorANSI : "";
        String seaparte2 = bgColorANSI != null ? ";" + bgColorANSI : "";

        String merge = keyANSI + finalStyle + seaparte1 + seaparte2 + "m" + text;
        return Reset ? merge + ANSI.Reset : merge;
    }

    public static String format(String text, String color, boolean Reset) {
        final String keyColorANSI = formatText(color);

        String seaparte1 = keyColorANSI != null ? keyColorANSI : "0";

        String merge = keyANSI + seaparte1 + "m" + text;
        return Reset ? merge + ANSI.Reset : merge;
    }

    public static String formatBG(String background) {
        return toANSIColor(background) != null
               ? "48;2;" + toANSIColor(background)
               : null;
    }

    public static String formatText(String color) {
        return toANSIColor(color) != null 
               ? "38;2;" + toANSIColor(color)
               : null;
    }

    protected static String toANSIColor(String hexColor) {
        if(hexColor == null || hexColor.isEmpty()) return null;
        String color = hexColor.strip().replaceAll("[^A-Fa-f0-9]", "");
        if (color.length() != 6) return null;

        int[] rgb = hexToRgb(color);

        List<Integer> list = new ArrayList<>();
        for (int value : rgb) {
            list.add(value);
        }
        return Splitter.formatANSI(list);
    }

    protected static int[] hexToRgb(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        return new int[]{
            Integer.parseInt(hex.substring(0, 2), 16),
            Integer.parseInt(hex.substring(2, 4), 16),
            Integer.parseInt(hex.substring(4, 6), 16)
        };
    }
}


/*
To format the styling into ANSI N;N;N
*/
class Splitter {
    public static String formatANSI(List<Integer> list) {
        StringBuilder merge  = new StringBuilder();
        Iterator<Integer> iter = list.iterator();
        while(iter.hasNext()) {
            merge.append(iter.next());
            if(iter.hasNext()){
                merge.append(";");
            }
        }
        return merge.toString();
    }
}