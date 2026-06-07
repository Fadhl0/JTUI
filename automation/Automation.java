package automation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import inputForm.TUICursor;
import inputForm.boxShape;
import inputForm.boxBorder.BoxTUI;
import utils.ANSIformat;
import utils.Component;
import utils.Icons;
import utils.TextTUI;
import utils.WindowsAPI;

public final class Automation {

  private TextTUI title;
  private TextTUI summaryLabel;
  private Supplier<TextTUI> summaryText;


  private final List<Cell> cells = new ArrayList<>();

  private int buffer = 2;
  
  private TextTUI activeBorderColor = new TextTUI("│").setColor("#169cda");
  private TextTUI inactiveBorderColor = new TextTUI("│").setColor("#34353f");

  private TextTUI activeIcon = new TextTUI(Icons.DiamondSolid.get()).setColor("#169cda");
  private TextTUI inactiveIcon = new TextTUI(Icons.Diamond.get()).setColor("#54e566");
  private TextTUI error = new TextTUI("Operation Failed.").setColor("#a90000");
  private TextTUI cancel = new TextTUI("Operation Cancelled.").setColor("#a90000");
  private TextTUI errIcon = new TextTUI(Icons.SquereSolid.get()).setColor("#a90000");


  private Automation() {}
  public static Automation create() {
    stop = false;
    return new Automation();
  }

  public Automation appendText(TextTUI title) { 
    cells.add(new TextCell().setText(title));
    return this;
  }
  public Automation appendCell(Cell cell) { 
    cells.add(cell);
    return this;
  }
  public Automation summary(TextTUI label, Supplier<TextTUI> text) {
    TextTUI newLabel = new TextTUI(label.toString().replaceAll("\n", ""));
    summaryLabel = newLabel;
    summaryText = text;
    return this;
  }

  // styling
  public Automation setBuffer(int number) { 
    if (number < 0) return this;
    buffer = number;
    return this;
  }

  public Automation setActiveIcon(TextTUI icon) {
    TextTUI newIcon = new TextTUI(icon.toString().replaceAll("\n", ""));
    this.activeIcon = newIcon;
    return this;
  }
  public Automation setInactiveIcon(TextTUI icon) {
    TextTUI newIcon = new TextTUI(icon.toString().replaceAll("\n", ""));
    this.inactiveIcon = newIcon;
    return this;
  }
  public Automation setTitle(TextTUI title) {
    TextTUI newTitle = new TextTUI(title.toString().replaceAll("\n", ""));
    this.title = newTitle;
    return this;
  }
  public Automation setErrorIcon(TextTUI icon) {
    TextTUI newIcon = new TextTUI(icon.toString().replaceAll("\n", ""));
    this.errIcon = newIcon;
    return this;
  }
  public Automation setErrorMsg(TextTUI message) {
    TextTUI newMessage = new TextTUI(message.toString().replaceAll("\n", ""));
    this.error = newMessage;
    return this;
  }
  //----
  // get styling
  public TextTUI[] getIcon() {
    return new TextTUI[]{activeIcon, inactiveIcon};
  }
  public TextTUI[] getBorder() {
    return new TextTUI[]{activeBorderColor, inactiveBorderColor};
  }
  public int getBuffer() {
    return buffer;
  }
  public TextTUI getSummary() {
    return summaryText.get();
  }
  public TextTUI getErrorMsg() {
    return error;
  }
  public TextTUI getCancelMsg() {
    return cancel;
  }
  public TextTUI getErrorIcon() {
    return errIcon;
  }


  public Automation start() {
    WindowsAPI.apply();

    // Title
    if (title != null) {
      System.out.println(
          new TextTUI("┌" + " ".repeat(buffer))
                      .setColor(inactiveBorderColor.getColor())
                      .appendText(title)
                      .appendText(new TextTUI("\n"))
                      .appendText(inactiveBorderColor)
      );
    }

    // Cells
    AutomationRunner.run(cells, this);

    //summery
    if(summaryText != null && !summaryText.get().isEmpty() && !stop) {
      System.out.print(boxSummery());
    }

    // ending
    if(!cells.isEmpty() && !stop) {
      System.out.print( inactiveBorderColor 
                          + " ".repeat(buffer)
                          + "\n"
                          + new TextTUI("└").setColor(inactiveBorderColor.getColor())
                        );
    }
    System.out.print(TUICursor.SHOW_CURSOR);
    Component.disableRawMode();
    return this;
  }

  private String boxSummery() {
    String space = " ".repeat(buffer);
    String styling = ANSIformat.format("", summaryText.get().getColor(), false);
    String orgnizeText = space + summaryText.get().toString().replaceAll("\n", "\n" + space + styling);

    BoxTUI box = new BoxTUI()
                .innerText(new TextTUI(orgnizeText))
                .shape(boxShape.SharpCornerLine)
                .color(inactiveBorderColor.getColor())
                .label(summaryLabel);
    
    String[] boxRows = box.build().split("\n");
    StringBuilder sb = new StringBuilder();

    TextTUI border = new TextTUI("├").setColor(inactiveBorderColor.getColor());
    sb.append(inactiveBorderColor.toString()).append("\n");

    for (int i = 0; i < boxRows.length; i++) {
      if(i == 0 || i == boxRows.length - 1) {
        boxRows[i] = border.toString()
                     + Component.visibleSubstring(boxRows[i], 1, Component.visibleLength(boxRows[i]));
      }
      sb.append(boxRows[i]).append("\n");
    }
    return sb.toString();
  }

  private static boolean stop = false;
  protected static void cancel() {
    stop = true;
  }

  protected static boolean isCancelled() {
    return stop;
  }

}
