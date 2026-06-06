package inputForm;

import utils.ANSI;
import utils.Colors;
import utils.Component;
import utils.TextTUI;

public class boxBorder {
  private boxShape shape;

  private String vertical = "";        private String horizontal = "";
  private String leftTop = "";         private String rightTop = "";
  private String leftBottom = "";      private String rightBottom = "";

  private String color = "";

  private boolean responsive = false;

  /**
   * - This for advanced use cases (Use TUIBox instead).
   */
  protected boxBorder(boxShape shape, String color) {
    this.shape = shape;
    this.color = color;
    init();
  }
  protected void setColor(String color) {
    this.color = color;
  }

  public static class BoxTUI {
    private TextTUI label = new TextTUI("");
    private TextTUI text = new TextTUI();
    private boxShape shape = boxShape.SingleLine;
    private String color = Colors.White.getColor();
    private boolean responsive = false;
    
    public BoxTUI label(TextTUI label)  { this.label = label; return this; }
    public BoxTUI innerText(TextTUI text) { this.text = text; return this; }
    public BoxTUI shape(boxShape shape) { this.shape = shape; return this; }
    public BoxTUI color(Colors color) { this.color = color.getColor(); return this; }
    public BoxTUI color(String color) { this.color = color; return this; }

    /**
     * - Convert box into responsive box.
     */
    public BoxTUI responsive() { this.responsive = true; return this; }
    
    /**
     * - convert TUIBox into box
     * @return String that contains box with its content.
    */
    public String build() {
      return new boxBorder(this).prompt(text, label);
    }

    public String toString() {
      return build();
    }
  }

  private String prompt(TextTUI text, TextTUI label) {
    int terminalLength = Component.getTerminalSize()[1];

    if (responsive) return drowAll(text, label, terminalLength);

    int width = Math.min(
                  terminalLength,
                  Math.max(Component.visibleLengthNoBreak(text.toString()), Component.visibleLength(label.toString())) + 5
                );
    return drowAll(text, label, width);
  }

  private boxBorder(BoxTUI b) {
    this.shape = b.shape;
    this.color = b.color;
    this.responsive = b.responsive;
    init();
  }

  protected String getTop(String text, int length) {
    return 
        drowBox(text, length, this.leftTop, this.rightTop);
  }
  protected String getBottom(int length) {
    return 
        drowBox("", length, this.leftBottom, this.rightBottom);
  }
  protected String getSide() {
    return new TextTUI(this.vertical).setColor(this.color).toString();
  }

  private String drowBox(String text, int length, String left, String right) {
    TextTUI a = new TextTUI(left).setColor(this.color);
    TextTUI b = new TextTUI(this.horizontal.repeat(length) + right).setColor(this.color);
    return a.toString() + text + b.toString();
  }

  private void init() {
    switch (this.shape) {
      case SideLine -> this.vertical = "┃";
      case QuantaLine -> this.horizontal = "─";
      case SingleLine -> {
        this.horizontal = "─";   this.vertical = "│";
        this.leftTop = "╭";    this.rightTop = "╮";
        this.leftBottom = "╰"; this.rightBottom = "╯";
      }
      case MultiLine -> {
        this.horizontal = "═";   this.vertical = "║";
        this.leftTop = "╔";    this.rightTop = "╗";
        this.leftBottom = "╚"; this.rightBottom = "╝";
      }
      case SharpCornerLine -> {
        this.horizontal = "─";   this.vertical = "│";
        this.leftTop = "┌";    this.rightTop = "┐";
        this.leftBottom = "└"; this.rightBottom = "┘";
      }
      default -> {}

    }
  }

  private String drowAll(TextTUI text, TextTUI label, int width) {
    int innerWidth = width - 2;

    String cpLabel = label.toString().replace("\n", "");
    int labelLength = Component.visibleLength(cpLabel);
    if(labelLength >= innerWidth) 
      cpLabel = Component.visibleSubstring(cpLabel, 0, innerWidth - 2) + "…" + ANSI.Reset;

    int newlabelLength = Component.visibleLength(cpLabel);
    String a = getTop(cpLabel, innerWidth - newlabelLength);
    String b = getBottom(width - 2);

    StringBuilder sb = new StringBuilder();
    String[] textSplit = text.toString().split("\n");
    for (String t : textSplit) {
      int textLength = Component.visibleLength(t);
      String mid;

      if (textLength <= innerWidth) {
          mid = getSide()
              + t
              + " ".repeat(innerWidth - textLength)
              + ANSI.Reset
              + getSide();
      } else {
          mid = getSide()
              + Component.visibleSubstring(t, 0, innerWidth - 1)
              + "…"
              + ANSI.Reset
              + getSide();
      }
      sb.append(mid).append("\n");
    }
    return a + "\n" + sb.toString() + b;
  }
}
