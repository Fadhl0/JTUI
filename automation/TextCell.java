package automation;

import inputForm.TUICursor;
import utils.TextTUI;

public class TextCell implements Cell {
  private TextTUI text = new TextTUI();

  // private TextTUI activeIcon;
  // private TextTUI activeBorder;
  private TextTUI inactiveIcon;
  private TextTUI inactiveBorder;

  private int buffer;

  public TextCell() {}

  public TextCell setText(TextTUI text) {
    this.text = text;
    return this;
  }
  public TextCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }
  public String getTitle() {
    return text.toString();
  }

  @Override
  public int getHeight() {
    // if (text.isEmpty()) return 0;
    return text.toString().split("\n", -1).length;
  }

  @Override
  public String getId() {
    return "<Text>";
  }

  @Override
  public void run() {
    System.out.print(TUICursor.HIDE_CURSOR);
    System.out.print(textAutomation());
  }

  protected TextTUI textAuto(){ return new TextTUI(textAutomation()); }

  protected String textAutomation() {
    String[] textArr = text.toString().split("\n", -1);
    String style = text.getStyle();

    StringBuilder sb = new StringBuilder();
    sb.append(inactiveIcon.toString());

    for (int i = 0; i < textArr.length; i++) {
      if(i != 0) sb.append(inactiveBorder.toString());
      sb.append(" ".repeat(buffer))
        .append(style)
        .append(textArr[i])
        .append("\n");
    }
    return sb.toString();
  }
  
  public String toString() {
    return textAutomation();
  }

  public TextCell setActiveIcon(TextTUI icon) {
    // this.activeIcon = icon;
    return this;
  }
  public TextCell setInactiveIcon(TextTUI icon) {
    this.inactiveIcon = icon;
    return this;
  }

  public TextCell setActiveBorder(TextTUI border) {
    // activeBorder = border;
    return this;
  }
  public TextCell setInactiveBorder(TextTUI border) {
    inactiveBorder = border;
    return this;
  }
}
