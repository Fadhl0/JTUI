package automation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.BaseSelector;
import inputForm.TUICursor;
import utils.Colors;
import utils.Component;
import utils.Icons;
import utils.TextTUI;

public class MultiSelectorCell extends BaseSelector<MultiSelectorCell> implements Cell {

  private TextTUI activeIcon = new TextTUI(Icons.SquereSolid.get()).setColor("#5af78e");
  private TextTUI inactiveIcon = new TextTUI(Icons.Squere.get()).setColor("#969696");
  private TextTUI hoverIcon = new TextTUI(Icons.Squere.get()).setColor("#4dcbf0");

  private TextTUI submitIcon = new TextTUI(Icons.CircleSolid.get()).setColor("#969696");

  private Consumer<List<Integer>> onSubmitCallback;
  private List<Integer> variable;
  private String submitColor = "#2c9a1d";

  private TextTUI activeBorderCell   = new TextTUI("");
  private TextTUI inactiveBorderCell = new TextTUI("");
  private TextTUI activeIconCell     = new TextTUI("");
  private TextTUI inactiveIconCell   = new TextTUI("");
  private int end = 2;
  private List<Integer> selected = new ArrayList<>();

  private int hover = 0;
  private boolean cancelled = false;

  // private String startCorner;
  private String endCorner;

  public MultiSelectorCell() {}

  public MultiSelectorCell setSubmitColor(Colors color) {
    submitColor = color.getColor();
    return this;
  }
  public MultiSelectorCell setSubmitColor(String color) {
    submitColor = color;
    return this;
  }
  
  public MultiSelectorCell setHoverIcon(String icon, String color) {
    hoverIcon = new TextTUI(icon).setColor(color);
    return this;
  }
  public MultiSelectorCell setHoverIcon(String icon, Colors color) {
    hoverIcon = new TextTUI(icon).setColor(color);
    return this;
  }

  public MultiSelectorCell setSubmitIcon(String icon, String color) {
    submitIcon = new TextTUI(icon).setColor(color);
    return this;
  }
  public MultiSelectorCell setSubmitIcon(String icon, Colors color) {
    submitIcon = new TextTUI(icon).setColor(color);
    return this;
  }

  public MultiSelectorCell onSubmit(Consumer<List<Integer>> set) {
    this.onSubmitCallback = set;
    return this;
  }

  private void header() {
    TextCell cell = new TextCell()
                        .setText(new TextTUI(prompt))
                        .setInactiveBorder(activeBorderCell)
                        .setInactiveIcon(activeIconCell)
                        .setBuffer(buffer);

    cell.run();
  }

  private void render(int hover) {
    header();

    int[] capacity = options.window(hover);

    for (int i = capacity[0]; i < capacity[1]; i++) {

      String option = options.options()[i];
      String desc = options.descs()[i];
      int descSpace = options.descSpacing();

      String color;
      TextTUI icon;
      if (i == hover && selected.contains(i)) {
        color = activeColor;
        icon = activeIcon;
      } else if (i == hover) {
        color = activeColor;
        icon = hoverIcon;
      } else if (selected.contains(i)) {
        color = inactiveColor;
        icon = activeIcon;
      } else {
        color = inactiveColor;
        icon = inactiveIcon;
      }
      System.out.println( 
                          activeBorderCell
                          + " ".repeat(buffer)
                          + " " + icon + " "
                          + new TextTUI(option).setColor(color)
                          + " ".repeat(descSpace - option.length())
                          + new TextTUI(desc).setColor(descColor)
                          + "\r"
                        );
    }
    System.out.print( 
                      activeBorderCell 
                      + " ".repeat(buffer)
                      + "\n"
                      + new TextTUI(endCorner).setColor(activeBorderCell.getColor())
                    );
    System.out.flush();
  }

  public MultiSelectorCell preSelect(int ...index) {
    for (int i : index) {
      selected.add(i);
    }
    return this;
  }

  public void run() {
    options.init();

    if(options.size() == 0) return;
    
    execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);
  }
  
  public List<Integer> execute() {
    OnClick.reset();
    int totalLines = getHeight();
    hover = 0;

    render(hover);

    OnClick.add(KeyPress.Up_Arrow, () -> {
      options.clearLines(totalLines - 1);
      hover--;
      hover = hover >= 0 ? hover : options.size()-1;
      render(hover);
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      options.clearLines(totalLines - 1);
      hover++;
      hover = hover < options.size() - 1 ? hover : hover % options.size();
      render(hover);
    });

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      System.out.print(TUICursor.SHOW_CURSOR);
      cancelled = true;
      OnClick.cancel();
      Automation.cancel();
    });

    OnClick.add(KeyPress.Space, () -> {
      options.clearLines(totalLines - 1);
      if (selected.contains(hover)) {
        selected.remove((Integer) hover);
      } else {
        selected.add(hover);
      }
      render(hover);
    });

    OnClick.add(KeyPress.Enter, () -> {
      options.clearLines(totalLines - 1); // clear option after select
      System.out.print("\033[J"); // clear all text bottom

      lastMessage(selected);
      
      System.out.flush();
      OnClick.cancel();
      Component.enableRawMode();
    });

    try {
      OnClick.execute();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    variable = cancelled ? List.of() : selected;
    return variable;
  }

  private String lastMessage(List<Integer> option) {
    TextTUI text = new TextTUI(prompt);
    for (int i = 0; i < options.size(); i++) {
      if (option.contains((Integer) i)) 
        text.appendText(new TextTUI("\n"))
            .appendText(new TextTUI(" ").appendText(submitIcon))
            .appendText(new TextTUI(" " + options.options()[i]).setColor(submitColor));
    }
    text.appendText(new TextTUI("\n"));
    
    TextCell cell = new TextCell()
                        .setText(text)
                        .setInactiveBorder(inactiveBorderCell)
                        .setInactiveIcon(inactiveIconCell)
                        .setBuffer(buffer);

    cell.run();
    return cell.textAutomation();
  }

  // Cell
  public String getTitle() {
    return prompt;
  }
  public int getHeight() {
    int one = prompt.split("\n", -1).length;
    int two = options.getRowsLength();
    return one + two + end;
  }

  public MultiSelectorCell setTitle(TextTUI prompt) {
    // String newText = prompt.toString().replaceAll("\n", "");
    this.prompt = prompt.toString();
    return this;
  }

  public MultiSelectorCell setActiveIcon(TextTUI icon) {
    activeIconCell = icon;
    return this;
  }
  public MultiSelectorCell setInactiveIcon(TextTUI icon) {
    inactiveIconCell = icon;
    return this;
  }
  public MultiSelectorCell setActiveBorder(TextTUI border) {
    activeBorderCell = border;
    return this;
  }
  public MultiSelectorCell setInactiveBorder(TextTUI border) {
    inactiveBorderCell = border;
    return this;
  }

  public String getId() {
    return "<Multi-Selector>";
  }

  @Override
  public MultiSelectorCell roundCorners(boolean isRound) {
    // startCorner = isRound ? "╭": "┌";
    endCorner = isRound ? "╰": "└";
    return this;
  }

}
