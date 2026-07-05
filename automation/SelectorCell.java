package automation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.TUICursor;
import utils.Colors;
import utils.Component;
import utils.Icons;
import utils.TextTUI;

public class SelectorCell implements Cell {

  private int limitOptions = -1; // -1 display all options

  private TextTUI activeIcon = new TextTUI(Icons.CircleSolid.get()).setColor("#2c9a1d");
  private TextTUI inactiveIcon = new TextTUI(Icons.CircleDotted.get()).setColor("#969696");
  private String[] options;
  private String prompt = "";
  private Consumer<Integer> onSubmitCallback;
  private int variable;
  private String activeColor = "#ffffff";
  private String inactiveColor = "#969696";

  private TextTUI activeBorderCell   = new TextTUI("");
  private TextTUI inactiveBorderCell = new TextTUI("");
  private TextTUI activeIconCell     = new TextTUI("");
  private TextTUI inactiveIconCell   = new TextTUI("");
  private int buffer;
  private int end = 2;

  private int selected = 0;
  private boolean cancelled = false;

  // private String startCorner;
  private String endCorner;

  private Map<String, String> list = new LinkedHashMap<>();
  private String[] descs;
  private String descColor = "#969696";
  private int descSpacing;

  public SelectorCell() {}

  // public SelectorCell options(String[] options) {
  //   String[] list = new String[options.length];
  //   for (int i = 0; i < list.length; i++) {
  //     list[i] = options[i].replaceAll("\n", "");
  //   }
  //   this.options = list;
  //   return this;
  // }
  public SelectorCell add(String option, String description) {
    option = option.replaceAll("\n", "");
    description = description.replaceAll("\n", "");
    list.put(option, description);
    return this;
  }

  public SelectorCell add(String option) {
    option = option.replaceAll("\n", "");
    list.put(option, "");
    return this;
  }

  public SelectorCell setActiveColor(Colors color) {
    activeColor = color.getColor();
    return this;
  }
  public SelectorCell setActiveColor(String color) {
    activeColor = color;
    return this;
  }
  public SelectorCell setInactiveColor(Colors color) {
    inactiveColor = color.getColor();
    return this;
  }
  public SelectorCell setInactiveColor(String color) {
    inactiveColor = color;
    return this;
  }
  public SelectorCell setDescriptionColor(String color) {
    descColor = color;
    return this;
  }
  
  public SelectorCell onSubmit(Consumer<Integer> set) {
    this.onSubmitCallback = set;
    return this;
  }

  private void clearLines(int n) {
    for (int i = 0; i < n; i++) System.out.print(TUICursor.CURSOR_UP.toString() + TUICursor.CLEAR_LINE.toString());
  }
  
  private void header() {
    TextCell SelectorCell = new TextCell()
                        .setText(new TextTUI(prompt))
                        .setInactiveBorder(activeBorderCell)
                        .setInactiveIcon(activeIconCell)
                        .setBuffer(buffer);

    SelectorCell.run();
  }

  private void init() {
    if(!list.isEmpty()) {
      options = list.keySet().toArray(new String[0]);
      descs = list.values().toArray(new String[0]);

      descSpacing = list.keySet().stream()
                          .max(Comparator.comparingInt(String::length))
                          .orElse("")
                          .length() + 2;
    }
  }

  private void render(int selected) {
    // System.out.println(prompt + "\r");
    header();
    for (int i = 0; i < options.length; i++) {
      if (i == selected) {
        System.out.println( 
                            activeBorderCell
                            + " ".repeat(buffer)
                            + " " + activeIcon + " "
                            + new TextTUI(options[i]).setColor(activeColor)
                            + " ".repeat(descSpacing - options[i].length())
                            + new TextTUI(descs[i]).setColor(descColor)
                            + "\r"
                          );
      }
      else {
        System.out.println( 
                            activeBorderCell
                            + " ".repeat(buffer)
                            + " " + inactiveIcon + " "
                            + new TextTUI(options[i]).setColor(inactiveColor)
                            + " ".repeat(descSpacing - options[i].length())
                            + new TextTUI(descs[i]).setColor(descColor)
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
    init();

    if(options.length == 0) return;
    
    execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);
  }
  
  public int execute() {
    OnClick.reset();
    int totalLines = getHeight();
    selected = 0;

    render(selected);

    OnClick.add(KeyPress.Up_Arrow, () -> {
        clearLines(totalLines - 1);
        selected--;
        selected = selected >= 0 ? selected : options.length-1;
        render(selected);
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
        clearLines(totalLines - 1);
        selected++;
        selected = selected < options.length - 1 ? selected : selected % options.length;
        render(selected);
    });

    // OnClick.add(KeyPress.Up_Arrow, () -> {
    //   if (selected > 0) {
    //     clearLines(totalLines - 1);
    //     selected--;
    //     render(selected);
    //   }
    // });

    // OnClick.add(KeyPress.Down_Arrow, () -> {
    //   if (selected < options.length - 1) {
    //     clearLines(totalLines - 1);
    //     selected++;
    //     render(selected);
    //   }
    // });

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      System.out.print(TUICursor.SHOW_CURSOR);
      cancelled = true;
      OnClick.cancel();
      Automation.cancel();
    });

    OnClick.add(KeyPress.Enter, () -> {
      clearLines(totalLines - 1); // clear option after select

      // System.out.println(prompt + " " + new TextTUI(options[selected]).setColor(activeIcon.getColor()));
      lastMessage(options[selected]);
      
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
    int two = list.size();
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
