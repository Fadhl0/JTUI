package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that provide set() method which you can pass your text to add gradient color.
 */
public class Gradient {
  /**
   * 
   * @param text add text to be gradient color
   * @param direction accept one of these options "diagonal", "vertical", "horizontal"
   * @param color add more than a color to apply gradient color.
   * @return String value
   */
  public static String set(String text, String direction, Colors... color) {
    List<String> colors = new ArrayList<>();
    for (Colors c : color) {
      colors.add(c.getColor());
    }
    return paint(text, direction, colors);
  }

  /**
   * 
   * @param text add text to be gradient color
   * @param direction accept one of these options "diagonal", "vertical", "horizontal"
   * @param color add more than a color to apply gradient color.
   * @return String value
   */
  public static String set(String text, String direction, String... color) {
    List<String> colors = Arrays.asList(color);
    return paint(text, direction, colors);
  }

  private static String paint(String text, String direction, List<String> colors) {
    if (colors.size() == 0) return text;

    final String[] lines = Component.removeStyle(text).split("\n");
    final int height = lines.length;

    int width = 0;
    for (String l : lines) {
      width = Math.max(l.length(), width);
    }    

    StringBuilder result = new StringBuilder();

    for (int i = 0; i < height; i++) {
      String line = lines[i];
      int lineLen = line.length();
      for (int j = 0; j < lineLen; j++) {
        float p;

        if ("vertical".equals(direction))
          p = height > 1 ? (float) i / (height - 1) : 0f;

        else if ("horizontal".equals(direction))
          p = lineLen > 1 ? (float) j / (lineLen - 1) : 0f;

        else { // diagonal
          float px = width  > 1 ? (float) j / (width  - 1) : 0f;
          float py = height > 1 ? (float) i / (height - 1) : 0f;
          p = (px + py) / 2f;
        }
        result.append(ANSI.Start)
              .append(interpolate(colors, p))
              .append("m")
              .append(line.charAt(j));
      }

      if (i < height - 1) {
        result.append(ANSI.Reset).append("\n");
      }
    }

    result.append(ANSI.Reset);
    return result.toString();
  }

  private static String interpolate(List<String> colors, float p) {
    if (colors.size() == 1) return ANSIformat.formatText(colors.get(0));

    float scaled = p * (colors.size() - 1);
    int idx = Math.min((int) scaled, colors.size() - 2);
    float localP = scaled - idx;

    return blend(colors.get(idx), colors.get(idx + 1), localP);
  }

  // color1 + (color2 - color1) * position
  private static String blend(String hex1, String hex2, float p) {
    int[] a = ANSIformat.hexToRgb(hex1);
    int[] b = ANSIformat.hexToRgb(hex2);
    
    int red = Math.round(a[0] + (b[0] - a[0]) * p);
    int green = Math.round(a[1] + (b[1] - a[1]) * p);
    int blue = Math.round(a[2] + (b[2] - a[2]) * p);

    return "38;2;" + red + ";" + green + ";" + blue;
  }
}
