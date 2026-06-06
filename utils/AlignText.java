package utils;

import Keyhandle.OnClick;

public class AlignText {
  private static boolean cleanupRegistered = false;

  public static String center(String... text){
    String txt = join(text);
    if(txt.isEmpty()) return "";

    int[] size = Component.getTerminalSize();
    String[] lines = txt.split("\n", -1);
    int maxLineLen = Component.visibleLengthNoBreak(txt);

    int row = Math.max(1, (size[0] - lines.length) / 2 + 1);
    int col = Math.max(1, (size[1] - maxLineLen) / 2 + 1);

    return at(row, col, txt);
  }

  /**
   * 
   * - Place text at specific point in Terminal.
   * @param row
   * @param column 
   * @param text (varargs) Support mutiple of String varible.
   * 
   */
  public static String at(int row, int column, String... text){
    String txt = join(text);
    if(txt.isEmpty()) return "";

    String[] lines = txt.split("\n", -1);

    if (lines.length == 1) {
      return render(column, row, txt);
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      sb.append("\033[").append(row + i).append(";").append(column).append("H")
        .append(lines[i]);
    }
    sb.append(ANSI.Reset);
    return sb.toString();
  }

  public static String up(String... text) {
    return at(1, 1, text);
  }

  public static String bottom(String... text) {
    int row = Component.getTerminalSize()[0];
    OnClick.OnClose(() -> cleanup());
    ensureScrollRegion(row);
    System.out.flush();
    return at(row, 1, text);
  }

  public static String centerTop(String... text) {
    String txt = join(text);
    if(txt.isEmpty()) return "";

    int size = Component.getTerminalSize()[1];
    int col = centerAtColumn(size, txt);

    return render(col, 1, txt);
  }

  public static String centerBottom(String... text) {
    String txt = join(text);
    if(txt.isEmpty()) return "";
    
    int[] size = Component.getTerminalSize();
    int row = size[0];
    int col  = centerAtColumn(size[1], txt);
    ensureScrollRegion(row);
    
    return render(col, row, txt);
  }

  private static String render(int width, int height, String text) {
    return 
      "\033[s" +
      "\033[" + height + ";" + width + "H" +
      "\033[2K" +
      // "\033[7m" +
      text +
      // "\033[0m" +
      ANSI.Reset +
      "\033[u";
  }

  private static void ensureScrollRegion(int rows) {
    System.out.print("\033[1;" + (rows - 1) + "r");
    System.out.flush();
    registerCleanup();
  }

  private static void registerCleanup() {
    if (!cleanupRegistered) {
      cleanupRegistered = true;
      OnClick.OnClose(AlignText::cleanup);
    }
  }

  private static void cleanup() {
    System.out.print("\033[r");
    System.out.flush();
  }

  private static int centerAtColumn(int terminalCols, String txt) {
    int maxLen = Component.visibleLengthNoBreak(txt);
    return Math.max(1, (terminalCols - maxLen) / 2 + 1);
  }

  private static String join(String... parts) {
    StringBuilder sb = new StringBuilder();
    for (String p : parts) {
      if (p != null) sb.append(p);
    }
    return sb.toString();
  }
}
