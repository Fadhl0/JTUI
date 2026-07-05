package inputForm;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import Keyhandle.KeyHandle;
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

  private boolean   startActive  = true;
  private boolean   active       = true;
  private char      start        = '1';
  private KeyHandle stop         = KeyPress.Escape;

  private Map<String, String> list = new LinkedHashMap<>();
  private String[] descs;
  private String descColor = "#969696";
  private int descSpacing;

  public SelectorTUI() {}


  // public SelectorTUI options(String... options) {
  //   String[] list = new String[options.length];
  //   for (int i = 0; i < list.length; i++) {
  //     list[i] = options[i].replaceAll("\n", "");
  //   }
  //   this.options = list;
  //   return this;
  // }

  public SelectorTUI add(String option, String description) {
    option = option.replaceAll("\n", "");
    description = description.replaceAll("\n", "");
    list.put(option, description);
    return this;
  }
  public SelectorTUI add(String option) {
    option = option.replaceAll("\n", "");
    list.put(option, "");
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
  public SelectorTUI setDescriptionColor(String color) {
    descColor = color;
    return this;
  }
  
  public SelectorTUI onSubmit(Consumer<Integer> set) {
    this.onSubmitCallback = set;
    return this;
  }

  public SelectorTUI startKey(char key) {
    this.start = key;
    return this;
  }
  public SelectorTUI stopKey(KeyHandle key) {
    this.stop = key;
    return this;
  }
  public SelectorTUI startActive(boolean active) {
    this.active = active;
    startActive = active;
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
                            + " ".repeat(descSpacing - options[i].length())
                            + new TextTUI(descs[i]).setColor(descColor)
                            + "\r"
                          );
      }
      else {
        System.out.println( 
                            " ".repeat(buffer)
                            + " " + inactiveIcon + " "
                            + new TextTUI(options[i]).setColor(inactiveColor)
                            + " ".repeat(descSpacing - options[i].length())
                            + new TextTUI(descs[i]).setColor(descColor)
                            + "\r"
                          );
      }
    }
    System.out.flush();
  }

  private int selected = 0;
  private boolean cancelled = false;

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

  public int prompt() {
    init();
    int result = execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);

    return result;
  }
  
  private int execute() {
    OnClick.reset();
    int totalLines = options.length + 1;
    selected = active ? 0 : -1;

    render(selected);

    OnClick.add(KeyPress.Up_Arrow, () -> {
      if (active) {
        clearLines(totalLines);
        selected--;
        selected = selected >= 0 ? selected : options.length-1;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      if (active) {
        clearLines(totalLines);
        selected++;
        selected = selected < options.length - 1 ? selected : selected % options.length;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Enter, () -> {
      if(active) {
        if (clear) clearLines(totalLines); // clear option after select
        System.out.flush();
        OnClick.cancel();
      }
    });

    if(!startActive) {
      OnClick.add(stop, () -> {
        if (active) {
          active = false;
          clearLines(totalLines);
          render(-1);
        }
      });
  
      OnClick.TypingReturn((c) -> {
        if (!active) {
          if (c == start) {
            active = true;
            clearLines(totalLines);
            selected = selected == -1 ? 0 : selected;
            render(selected);
          }
        }
      });
    }

    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      cancelled = true;
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
