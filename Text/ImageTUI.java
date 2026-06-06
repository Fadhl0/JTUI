package Text;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import javax.imageio.ImageIO;
import utils.ANSI;

public class ImageTUI {
  private StringBuffer art = new StringBuffer();
  private Path path;
  private String symbol = "█";
  private int wide = 2;
  private HashMap<Integer, Integer> hex = new HashMap<>();

  public ImageTUI(Path path) {
    this.path = path;
  }
  public ImageTUI setWide(int wide) {
    this.wide = wide;
    return this;
  }

  /**
   * Replaces a hexadecimal color code with an alternative color code.
   * <p>
   * <strong>Note:</strong> This method does not fully support colors with an alpha channel.
   * </p>
   * 
   * @param hexColor1 the original hexadecimal color code to be replaced
   * @param hexColor2 the alternative hexadecimal color code to use instead
   */
  public ImageTUI replaceColor(String hexColor1, String hexColor2) {
    if (!hexColor1.startsWith("#")) hexColor1 = "#" + hexColor1;
    if (!hexColor2.startsWith("#")) hexColor2 = "#" + hexColor2;

    if (hexColor1.length() == 7 && hexColor2.length() == 7) {
      Color color1 = Color.decode(hexColor1);
      Color color2 = Color.decode(hexColor2);

      int keyRgb = color1.getRGB() & 0x00FFFFFF; 
      int valueRgb = color2.getRGB() & 0x00FFFFFF;

      hex.put(keyRgb, valueRgb);
    }
    
        
    return this;
  }

  /**
   * Change the symbol of the drawing
   * Defualt value is " █ "
   * @param symbol
   */
  public ImageTUI setSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  private void init() {
    art.setLength(0);
    try {
      if (path == null) throw new NullPointerException("Failed to find Image!");
      File f = new File(path.toUri());
      BufferedImage img = ImageIO.read(f);
      toText(img);
    } catch (IOException e) {
      throw new RuntimeException("Failed to find Image: " + path, e);
    }
  }

  private void toText(BufferedImage img) {
    if(img == null) return;

    for(int i=0; i<img.getHeight(); i++) {
      for(int j=0; j<img.getWidth(); j++) {
        int argb = img.getRGB(j, i);
        int alpha = (argb >> 24) & 0xFF;

        if(alpha == 0) {
          art.append(" ".repeat(wide));
          continue;
        }

        int rgbOnly = argb & 0x00FFFFFF;
        Integer replacement = hex.get(rgbOnly);
        if (replacement != null) {
          argb = (alpha << 24) | replacement;
        }
        
        int red   = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue  = (argb) & 0xFF;

        String color = "\u001B[38;2;" + red + ";" + green + ";" + blue + "m";

        art.append(color).append(symbol.repeat(wide)).append(ANSI.Reset);
      }
      art.append("\n");
    }
    if (art.length() > 0) {
      art.deleteCharAt(art.length() - 1); // remove "\n"
    }
  }

  @Override
  public String toString() {
    init();
    return art.toString();
  }
}