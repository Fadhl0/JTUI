package automation;

import java.util.function.Consumer;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.TUICursor;
import inputForm.BaseSelector;
import utils.Component;
import utils.TextTUI;

public class SelectorCell extends BaseSelector<SelectorCell> implements Cell {

  private Consumer<Integer> onSubmitCallback;
  private int variable;

  private TextTUI activeBorderCell   = new TextTUI("");
  private TextTUI inactiveBorderCell = new TextTUI("");
  private TextTUI activeIconCell     = new TextTUI("");
  private TextTUI inactiveIconCell   = new TextTUI("");
  private int end = 2;

  private int selected = 0;
  private boolean cancelled = false;

  private String endCorner;

  public SelectorCell() {}
  
  public SelectorCell onSubmit(Consumer<Integer> set) {
    this.onSubmitCallback = set;
    return this;
  }
  
  private void header() {
    TextCell SelectorCell = new TextCell()
                        .setText(new TextTUI(prompt))
                        .setInactiveBorder(activeBorderCell)
                        .setInactiveIcon(activeIconCell)
                        .setBuffer(buffer);

    SelectorCell.run();
  }

  private void render(int selected) {
    header();

    int[] capacity = options.window(selected);

    for (int i = capacity[0]; i < capacity[1]; i++) {
      String option = options.options()[i];
      String desc = options.descs()[i];
      int descSpace = options.descSpacing();

      if (i == selected) {
        System.out.println( 
                            activeBorderCell
                            + " ".repeat(buffer)
                            + " " + activeIcon + " "
                            + new TextTUI(option).setColor(activeColor)
                            + " ".repeat(descSpace - option.length())
                            + new TextTUI(desc).setColor(descColor)
                            + "\r"
                          );
      }
      else {
        System.out.println( 
                            activeBorderCell
                            + " ".repeat(buffer)
                            + " " + inactiveIcon + " "
                            + new TextTUI(option).setColor(inactiveColor)
                            + " ".repeat(descSpace - option.length())
                            + new TextTUI(desc).setColor(descColor)
                            + "\r"
                          );
      }
    }
    System.out.print( activeBorderCell 
                        + " ".repeat(buffer)
                        + "\n"
                        + new TextTUI(endCorner).setColor(activeBorderCell.getColor())
                      );
    System.out.flush();
  }

  public void run() {
    options.init();

    int listSize = options.options().length;

    if(listSize == 0) return;
    
    execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);
  }
  
  public int execute() {
    OnClick.reset();
    int totalLines = getHeight();
    selected = 0;

    render(selected);

    OnClick.add(KeyPress.Up_Arrow, () -> {
        options.clearLines(totalLines - 1);
        selected--;
        selected = selected >= 0 ? selected : options.size()-1;
        render(selected);
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
        options.clearLines(totalLines - 1);
        selected++;
        selected = selected < options.size() - 1 ? selected : selected % options.size();
        render(selected);
    });

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      System.out.print(TUICursor.SHOW_CURSOR);
      cancelled = true;
      OnClick.cancel();
      Automation.cancel();
    });

    OnClick.add(KeyPress.Enter, () -> {
      options.clearLines(totalLines - 1); // clear option after select

      lastMessage(options.options()[selected]);
      
      System.out.flush();
      System.out.print("\033[J"); // clear all text bottom

      OnClick.cancel();
      Component.enableRawMode();
    });

    try {
      OnClick.execute();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    variable = cancelled ? -1 : selected;
    return variable;
  }

  private String lastMessage(String option) {
    TextTUI text = new TextTUI(option).setColor(activeIcon.getColor());
    TextCell SelectorCell = new TextCell()
                        .setText(new TextTUI(prompt + " " + text + "\n"))
                        .setInactiveBorder(inactiveBorderCell)
                        .setInactiveIcon(inactiveIconCell)
                        .setBuffer(buffer);

    SelectorCell.run();
    return SelectorCell.textAutomation();
  }

  // SelectorCell
  public String getTitle() {
    return prompt;
  }
  public int getHeight() {
    int one = prompt.split("\n", -1).length;
    int two = options.getRowsLength();
    return one + two + end;
  }

  public SelectorCell setTitle(TextTUI prompt) {
    // String newText = prompt.toString().replaceAll("\n", "");
    this.prompt = prompt.toString();
    return this;
  }

  public SelectorCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }

  public SelectorCell setActiveIcon(TextTUI icon) {
    activeIconCell = icon;
    return this;
  }
  public SelectorCell setInactiveIcon(TextTUI icon) {
    inactiveIconCell = icon;
    return this;
  }
  public SelectorCell setActiveBorder(TextTUI border) {
    activeBorderCell = border;
    return this;
  }
  public SelectorCell setInactiveBorder(TextTUI border) {
    inactiveBorderCell = border;
    return this;
  }


  public String getId() {
    return "<Selector>";
  }

  @Override
  public SelectorCell roundCorners(boolean isRound) {
    // startCorner = isRound ? "╭": "┌";
    endCorner = isRound ? "╰": "└";
    return this;
  }
}
