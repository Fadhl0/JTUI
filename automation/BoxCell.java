package automation;

import java.util.function.Supplier;

import inputForm.boxShape;
import inputForm.TUICursor;
import inputForm.boxBorder.BoxTUI;
import utils.ANSIformat;
import utils.Component;
import utils.TextTUI;

public class BoxCell implements Cell {

  public BoxCell(){}

  private Supplier<TextTUI> text;
  private int buffer;

  private TextTUI inactiveBorder;
  private TextTUI label = new TextTUI();

  private boolean isRound = false;

  public BoxCell label(TextTUI label) {
    TextTUI newLabel = new TextTUI(label.toString().replaceAll("\n", ""));
    this.label = newLabel;
    return this;
  }
  public BoxCell setText(Supplier<TextTUI> text) {
    this.text = text;
    return this;
  }
  public BoxCell roundCorners(boolean isRound) {
    this.isRound = isRound;
    return this;
  }

  private String createBox() {
    String space = " ".repeat(buffer);
    String styling = ANSIformat.format("", text.get().getColor(), false);
    String orgnizeText = space + text.get().toString().replaceAll("\n", "\n" + space + styling);

    boxShape shape = isRound ? boxShape.SingleLine : boxShape.SharpCornerLine;
    BoxTUI box = new BoxTUI()
                .innerText(new TextTUI(orgnizeText))
                .shape(shape)
                .color(inactiveBorder.getColor())
                .label(label);
    
    String[] boxRows = box.build().split("\n");
    StringBuilder sb = new StringBuilder();

    TextTUI border = new TextTUI("├").setColor(inactiveBorder.getColor());
    // sb.append(inactiveBorder.toString()).append("\n");

    for (int i = 0; i < boxRows.length; i++) {
      if(i == 0 || i == boxRows.length - 1) {
        boxRows[i] = border.toString()
                     + Component.visibleSubstring(boxRows[i], 1, Component.visibleLength(boxRows[i]));
      }
      sb.append(boxRows[i]).append("\n");
    }

    sb.append(inactiveBorder).append("\n");
    return sb.toString();
  }

  @Override
  public int getHeight() {
    return text.toString().split("\n").length + 3;
  }

  @Override
  public String getId() {
    return "<Box>";
  }

  @Override
  public String getTitle() {
    return label.toString();
  }

  @Override
  public void run() {
    if(text != null && !text.get().isEmpty()) {
      System.out.print(TUICursor.HIDE_CURSOR);
      System.out.print(createBox());
    }
  }

  @Override
  public BoxCell setActiveBorder(TextTUI border) {
    return this;
  }

  @Override
  public BoxCell setInactiveBorder(TextTUI border) {
    inactiveBorder = border;
    return this;
  }

  @Override
  public BoxCell setActiveIcon(TextTUI icon) {
    return this;
  }

  @Override
  public BoxCell setInactiveIcon(TextTUI icon) {
    return this;
  }

  @Override
  public BoxCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }
  
}
