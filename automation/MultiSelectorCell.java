package automation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.TUICursor;
import utils.Colors;
import utils.Component;
import utils.Icons;
import utils.TextTUI;

public class MultiSelectorCell implements Cell {

  private int limitOptions = -1; // -1 display all options

  private TextTUI selectedIcon = new TextTUI(Icons.SquereSolid.get()).setColor("#5af78e");
  private TextTUI activeIcon = new TextTUI(Icons.Squere.get()).setColor("#4dcbf0");
  private TextTUI inactiveIcon = new TextTUI(Icons.Squere.get()).setColor("#969696");
  private TextTUI submitIcon = new TextTUI(Icons.CircleSolid.get()).setColor("#969696");
  private String[] options;
  private String prompt = "";
  private Consumer<List<Integer>> onSubmitCallback;
  private List<Integer> variable;
  private String activeColor = "#ffffff";
  private String inactiveColor = "#969696";
  private String submitColor = "#2c9a1d";

  private TextTUI activeBorderCell   = new TextTUI("");
  private TextTUI inactiveBorderCell = new TextTUI("");
  private TextTUI activeIconCell     = new TextTUI("");
  private TextTUI inactiveIconCell   = new TextTUI("");
  private int buffer;
  private int end = 2;

  public MultiSelectorCell() {}

  public MultiSelectorCell options(String[] options) {
    String[] list = new String[options.length];
    for (int i = 0; i < list.length; i++) {
      list[i] = options[i].replaceAll("\n", "");
    }
    this.options = list;
    return this;
  }
  public MultiSelectorCell setActiveColor(Colors color) {
    activeColor = color.getColor();
    return this;
  }
  public MultiSelectorCell setActiveColor(String color) {
    activeColor = color;
    return this;
  }
  public MultiSelectorCell setInactiveColor(Colors color) {
    inactiveColor = color.getColor();
    return this;
  }
  public MultiSelectorCell setInactiveColor(String color) {
    inactiveColor = color;
    return this;
  }
  public MultiSelectorCell setSubmitColor(Colors color) {
    submitColor = color.getColor();
    return this;
  }
  public MultiSelectorCell setSubmitColor(String color) {
    submitColor = color;
    return this;
  }
  
  public MultiSelectorCell onSubmit(Consumer<List<Integer>> set) {
    this.onSubmitCallback = set;
    return this;
  }

  private void clearLines(int n) {
    for (int i = 0; i < n; i++) System.out.print(TUICursor.CURSOR_UP.toString() + TUICursor.CLEAR_LINE.toString());
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
    for (int i = 0; i < options.length; i++) {
      String color;
      TextTUI icon;
      if (i == hover && selected.contains(i)) {
        color = activeColor;
        icon = selectedIcon;
      } else if (i == hover) {
        color = activeColor;
        icon = activeIcon;
      } else if (selected.contains(i)) {
        color = inactiveColor;
        icon = selectedIcon;
      } else {
        color = inactiveColor;
        icon = inactiveIcon;
      }
      System.out.println( 
                          activeBorderCell
                          + " ".repeat(buffer)
                          + " " + icon + " "
                          + new TextTUI(options[i]).setColor(color)
                          + "\r"
                        );
    }
    System.out.print( 
                      activeBorderCell 
                      + " ".repeat(buffer)
                      + "\n"
                      + new TextTUI("└").setColor(activeBorderCell.getColor())
                    );
    System.out.flush();
  }

  private List<Integer> selected = new ArrayList<>();
  private int hover = 0;
  private boolean cancelled = false;

  public MultiSelectorCell preSelect(int ...index) {
    for (int i : index) {
      selected.add(i);
    }
    return this;
  }

  public void run() {
    if(options.length == 0) return;
    
    execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);
  }
  
  public List<Integer> execute() {
    OnClick.reset();
    int totalLines = getHeight();
    hover = 0;

    render(hover);

    OnClick.add(KeyPress.Up_Arrow, () -> {
      if (hover > 0) {
        clearLines(totalLines - 1);
        hover--;
        render(hover);
      }
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      if (hover < options.length - 1) {
        clearLines(totalLines - 1);
        hover++;
        render(hover);
      }
    });

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      System.out.print(TUICursor.SHOW_CURSOR);
      cancelled = true;
      OnClick.cancel();
      Automation.cancel();
    });

    OnClick.add(KeyPress.Space, () -> {
      clearLines(totalLines - 1);
      if (selected.contains(hover)) {
        selected.remove((Integer) hover);
      } else {
        selected.add(hover);
      }
      render(hover);
    });

    OnClick.add(KeyPress.Enter, () -> {
      clearLines(totalLines - 1); // clear option after select
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
    for (int i = 0; i < options.length; i++) {
      if (option.contains((Integer) i)) 
        text.appendText(new TextTUI("\n"))
            .appendText(new TextTUI(" ").appendText(submitIcon))
            .appendText(new TextTUI(" " + options[i]).setColor(submitColor));
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
    int two = options.length;
    return one + two + end;
  }

  public MultiSelectorCell setTitle(TextTUI prompt) {
    // String newText = prompt.toString().replaceAll("\n", "");
    this.prompt = prompt.toString();
    return this;
  }

  public MultiSelectorCell setBuffer(int buffer) {
    this.buffer = buffer;
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
  public MultiSelectorCell setSelectedIcon(TextTUI icon) {
    selectedIcon = icon;
    return this;
  }
  public MultiSelectorCell setSubmitIcon() {
    return this;
  }

  public String getId() {
    return "<Multi-Selector>";
  }

}
