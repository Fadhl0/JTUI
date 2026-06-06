package inputForm;

import java.util.function.Consumer;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import utils.Colors;
import utils.Icons;
import utils.TextTUI;

public class SelectorTUI {

  private int limitOptions = -1; // TODO: -1 display all options

  private TextTUI activeIcon = new TextTUI(Icons.CircleSolid.get()).setColor("#2c9a1d");
  private TextTUI inactiveIcon = new TextTUI(Icons.CircleDotted.get()).setColor("#969696");
  private String[] options;
  private String prompt;
  private Consumer<Integer> onSubmitCallback;
  private int variable;
  private String activeColor = "#ffffff";
  private String inactiveColor = "#969696";
  private boolean clear = false;
  private int buffer = 2;

  public SelectorTUI() {}
  public SelectorTUI options(String... options) {
    String[] list = new String[options.length];
    for (int i = 0; i < list.length; i++) {
      list[i] = options[i].replaceAll("\n", "");
    }
    this.options = list;
    return this;
  }
  public SelectorTUI setActiveColor(Colors color) {
    activeColor = color.getColor();
    return this;
  }
  public SelectorTUI setActiveColor(String color) {
    activeColor = color;
    return this;
  }
  public SelectorTUI setInactiveColor(Colors color) {
    inactiveColor = color.getColor();
    return this;
  }
  public SelectorTUI setInactiveColor(String color) {
    inactiveColor = color;
    return this;
  }
  
  public SelectorTUI onSubmit(Consumer<Integer> set) {
    this.onSubmitCallback = set;
    return this;
  }

  private void clearLines(int n) {
    for (int i = 0; i < n; i++) System.out.print(TUICursor.CURSOR_UP.toString() + TUICursor.CLEAR_LINE.toString());
  }

  private void render(int selected) {
    System.out.println("\r" + prompt);
    for (int i = 0; i < options.length; i++) {
      if (i == selected) {
        System.out.println( 
                            " ".repeat(buffer)
                            + " " + activeIcon + " "
                            + new TextTUI(options[i]).setColor(activeColor)
                            + "\r"
                          );
      }
      else {
        System.out.println( 
                            " ".repeat(buffer)
                            + " " + inactiveIcon + " "
                            + new TextTUI(options[i]).setColor(inactiveColor)
                            + "\r"
                          );
      }
    }
    System.out.flush();
  }

  private int selected = 0;
  private boolean cancelled = false;

  public int prompt() {
    int result = execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);

    return result;
  }
  
  private int execute() {
    OnClick.reset();
    int totalLines = options.length + 1;
    selected = 0;

    render(selected);

    OnClick.add(KeyPress.Up_Arrow, () -> {
      if (selected > 0) {
        clearLines(totalLines);
        selected--;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      if (selected < options.length - 1) {
        clearLines(totalLines);
        selected++;
        render(selected);
      }
    });

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      cancelled = true;
      OnClick.cancel();
    });

    OnClick.add(KeyPress.Enter, () -> {
      if (clear) clearLines(totalLines); // clear option after select

      // System.out.println(prompt + " " + new TextTUI(options[selected]).setColor(activeIcon.getColor()));
      // lastMessage(options[selected]);
      
      System.out.flush();
      OnClick.cancel();
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

  public SelectorTUI clearAfterSubmit() {
    clear = true;
    return this;
  }

  public String getTitle() {
    return prompt;
  }

  public SelectorTUI setTitle(TextTUI prompt) {
    String newText = prompt.toString().replaceAll("\n", "");
    this.prompt = newText;
    return this;
  }
  public SelectorTUI setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }

}
