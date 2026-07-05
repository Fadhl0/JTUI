package inputForm;

import java.util.function.Consumer;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import utils.TextTUI;

public class SelectorTUI extends BaseSelector<SelectorTUI> {
  
  private Consumer<Integer> onSubmitCallback;
  private int variable;

  private boolean clear = false;

  private boolean   startActive  = true;
  private boolean   active       = true;
  private char      start        = '1';
  private KeyHandle stop         = KeyPress.Escape;

  private int selected = 0;
  private boolean cancelled = false;

  public SelectorTUI() {}
  
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

  private void render(int selected) {
    System.out.println("\r" + prompt);

    int[] capacity = options.window(selected);

    for (int i = capacity[0]; i < capacity[1]; i++) {
      String option = options.options()[i];
      String desc = options.descs()[i];
      int descSpace = options.descSpacing();

      if (i == selected) {
        System.out.println( 
                            " ".repeat(buffer)
                            + " " + activeIcon + " "
                            + new TextTUI(option).setColor(activeColor)
                            + " ".repeat(descSpace - option.length())
                            + new TextTUI(desc).setColor(descColor)
                            + "\r"
                          );
      }
      else {
        System.out.println( 
                            " ".repeat(buffer)
                            + " " + inactiveIcon + " "
                            + new TextTUI(option).setColor(inactiveColor)
                            + " ".repeat(descSpace - option.length())
                            + new TextTUI(desc).setColor(descColor)
                            + "\r"
                          );
      }
    }
    System.out.flush();
  }


  public int prompt() {
    options.init();
    int result = execute();
    
    if (onSubmitCallback != null) onSubmitCallback.accept(variable);

    return result;
  }
  
  private int execute() {
    int listSize = options.options().length;
    OnClick.reset();
    int totalLines = listSize + 1;
    selected = active ? 0 : -1;

    render(selected);

    OnClick.add(KeyPress.Up_Arrow, () -> {
      if (active) {
        options.clearLines(totalLines);
        selected--;
        selected = selected >= 0 ? selected : listSize-1;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      if (active) {
        options.clearLines(totalLines);
        selected++;
        selected = selected < listSize - 1 ? selected : selected % listSize;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Enter, () -> {
      if(active) {
        if (clear) options.clearLines(totalLines); // clear option after select
        System.out.flush();
        OnClick.cancel();
      }
    });

    if(!startActive) {
      OnClick.add(stop, () -> {
        if (active) {
          active = false;
          options.clearLines(totalLines);
          render(-1);
        }
      });
  
      OnClick.TypingReturn((c) -> {
        if (!active) {
          if (c == start) {
            active = true;
            options.clearLines(totalLines);
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

  @Override
  public SelectorTUI setTitle(TextTUI prompt) {
    this.prompt = prompt.toString().replaceAll("\n", "");
    return this;
  }

}
