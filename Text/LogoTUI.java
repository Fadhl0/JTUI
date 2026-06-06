package Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ANSIformat;
import utils.Colors;
import utils.Component;
import utils.TextTUI;

// Text to ASCII

public class LogoTUI {
  private Map<Character, String[]> ansiShadow = new HashMap<>();
  private List<String> ansiArt = new ArrayList<>();

  private String text; private TUIFont font;
  private int expectedHeight; private int expectedSpace;

  private String color; private String shadowColor;

  private boolean trim = false;
  private String version;

  public LogoTUI(String text, TUIFont font) {
    this.text = text;
    this.font = font;
  }

  public LogoTUI trim(){
    this.trim = true;
    return this;
  }

  private void init() {
    String path = font.getFont();
    try (BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
      String line;
      while ((line = br.readLine()) != null) {
        this.ansiArt.add(line);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load font: " + path, e);
    }

    String a = String.join("\n", ansiArt);
    String[] aa = a.split("@@");

    int j =2;
    this.expectedHeight = Integer.parseInt(aa[0].trim());
    this.expectedSpace = Integer.parseInt(aa[1].trim());
    for (char i : ASCIITable) {
      if (j >= aa.length) break;

      String[] rows = aa[j].split("\n");
      if (rows.length >= expectedHeight) {
        this.ansiShadow.put(i, rows);
      } else {
        // System.out.println("Skipping '" + i + "': Expected " + expectedHeight + " rows, but found " + rows.length);
        this.ansiShadow.put(i, new String[]{});
      }
      j++;
    }
  }

  private String logoMaker(String texts) {
    init();
    StringBuilder lineBuilder = new StringBuilder();

    try {
      for (int row = 0; row <= expectedHeight; row++) {
        for (char c : texts.toCharArray()) {
          if (ansiShadow.containsKey(c)) {
            String[] characterRows = ansiShadow.get(c);
            
            if (characterRows.length > row) {
              lineBuilder.append(characterRows[row]);
            }
          } else if (c == ' ') {
            lineBuilder.append(" ".repeat(expectedSpace));
          }
        }
        lineBuilder.append("\n");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return shadowColorLogic(colorLogic(lineBuilder.toString()));
  }

  public enum TUIFont {
    ANSIShadow("ANSIShadow.txt", true),
    ANSISingleShadow("ANSISingle.txt", true),
    Rebel("DOS Rebel.flf", true),
    Pagga("pagga.tlf", true),
    Terrace("Terrace.flf", true),
    ANSIRegular("ANSIRegular.flf", false),
    Kban("Kban.flf", false),
    Rectangles("Rectangles.flf", false),
    ANSICompact("ANSICompact.flf", false),
    SubZero("Sub-Zero.flf", false);

    private String font;
    private boolean shadow;

    private TUIFont(String font, boolean shadow) {
      this.font = font;
      this.shadow = shadow;
    }

    protected boolean hasShadow() {
      return this.shadow;
    }

    protected String get() {
      return this.font;
    }

    protected String getFont() {
      try {
        Path classDir = Path.of(
          TUIFont.class.getProtectionDomain()
                      .getCodeSource()
                      .getLocation()
                      .toURI()
        );
        return classDir.resolve("Text/fonts/" + this.font).normalize().toString();
      } catch (Exception e) {
        return Path.of("fonts", this.font).toString();
      }
    }
  }

  private final static char[] ASCIITable = {
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
    48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64,
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
    91, 92, 93, 94, 95, 96,
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122,
    123, 124, 125, 126,
    196, 214, 220, 228, 246, 252, 223
  };

  public LogoTUI setColor(Colors color) {
    this.color = color.getColor();
    return this;
  }
  public LogoTUI setColor(String color) {
    this.color = color;
    return this;
  }
  
  public LogoTUI setShadowColor(Colors color) {
    shadowColor = color.getColor();
    return this;
  }
  public LogoTUI setShadowColor(String color) {
    shadowColor = color;
    return this;
  }

  private String colorLogic(String logo) {
    if (this.color == null) return logo;
    if (!this.font.hasShadow()) return otherFontColorize(logo);

    String[] toColored = {"█", "▀", "▄"};
    String colorization = logo;
    for (String co : toColored) {
      colorization = colorization.replace(co, ANSIformat.format(co, color, true));
    }
    return colorization;
  }

  private String shadowColorLogic(String logo) {
    if (this.shadowColor == null) return logo;
    if (!this.font.hasShadow()) return logo;

    String colorization = logo;
    String[] toColored = {"░", "▒", "╚", "╝", "═", "║", "╔", "╗", "└", "┘",  "─", "│", "┌", "┐"};

    for (String co : toColored) {
      colorization = colorization.replace(co, ANSIformat.format(co, shadowColor, true));
    }

    return colorization;
  }

  private String otherFontColorize(String logo) {
    StringBuffer sb = new StringBuffer();
    String[] rows = logo.split("\n");

    for (String row : rows) {
      sb.append(row.replace(row, ANSIformat.format(row, color, true))).append("\n");
    }

    return sb.toString();
  }

  private String converter() {
    String[] texts = this.text.split("\n");
    StringBuilder s = new StringBuilder();

    for (String t : texts) {
      s.append(logoMaker(t));
    }

    if (trim) removeEmptyLines(s);

    if (version != null) {
      String[] list = s.toString().split("\n");
      int lastIndex = Component.visibleLength(list[list.length-1]);
      int verLen = Component.visibleLength(version);
      int subLen = verLen <= lastIndex
                 ? lastIndex - verLen
                 : -1;
      
      String ver;
      if (subLen == -1) ver = Component.visibleSubstring(version, 0, lastIndex);
      else {
        ver = " ".repeat(subLen) + version;
      }
      s.append("\n").append(ver);
    }

    return s.toString();
  }

  private void removeEmptyLines(StringBuilder texts) {
    String[] textSplit = texts.toString().split("\n");
    texts.setLength(0);

    for (String t : textSplit) {
      if (Component.visibleLength(t.replaceAll(" ", "")) == 0) continue;
      if (texts.length() > 0) texts.append("\n");
      texts.append(t);
    }

  }

  public String toString(){
    return converter();
  }

  public LogoTUI setVersion(TextTUI version) {
    this.version = version.toString().replaceAll("\n", "");
    return this;
  }

  
}
