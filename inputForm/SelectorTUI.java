package inputForm;

import java.util.function.Consumer;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.boxBorder.BoxTUI;
import utils.TextTUI;

public class SelectorTUI extends BaseSelector<SelectorTUI> implements TUIComponent {
  
  private Consumer<Integer> onSubmitCallback;
  private int variable = -1;
  private int preSelect = -1;

  private BoxTUI activeBox;
  private BoxTUI inactiveBox;
  private boxShape box = boxShape.None;
  private String activeBorderColor = "";
  private String inactiveBorderColor = "";

  private boolean clear = false;
  private boolean disableActivation = false;

  private volatile boolean active = true;
  private KeyHandle start         = KeyModifier.CTRL.with('b');
  private KeyHandle stop          = KeyPress.Escape;

  private int selected = 0;

  private boolean invert = false;
  private boolean isResponsive = false;

  public SelectorTUI() {}
  
  /**
   * Adding a varible that returns when submit.
   * @param set
   */
  public SelectorTUI onSubmit(Consumer<Integer> set) {
    this.onSubmitCallback = set;
    return this;
  }

  /**
   * Adding starting key to activate the Selectors.
   * @param key KeyHandle
   */
  public SelectorTUI startKey(KeyHandle key) {
    this.start = key;
    return this;
  }
  /**
   * Adding stoping key to deactivate the Selectors.
   * @param key KeyHandle
   */
  public SelectorTUI stopKey(KeyHandle key) {
    this.stop = key;
    return this;
  }

  /**
   * Add default value.
   * @param index integer value that 
   */
  public SelectorTUI preSelect(int index) {
    preSelect = index;
    return this;
  }

  /**
   * Turn on/off stopping key
   * @param status
   */
  public SelectorTUI turnOffSwitch(boolean status) {
    disableActivation = status;
    return this;
  }

  /**
   * Adding box (boxTUI) cover around SelectorTUI
   * @param shape boxShape enum
   * @param activeColor
   * @param inactiveColor
   * @param isResponsive if true then box will take full width
   */
  public SelectorTUI wrapInBorder(boxShape shape, String activeColor, String inactiveColor, boolean isResponsive){
    box = shape;
    activeBorderColor = activeColor;
    inactiveBorderColor = inactiveColor;
    this.isResponsive = isResponsive;
    return this;
  }

  /**
   * - Invert active option's color. (legacy mode)
   */
  public SelectorTUI invertActiveSelectors(){
    invert = true;
    return this;
  }

  private void initBorderBox() {
    if(!box.equals(boxShape.None)) {
      activeBox = new BoxTUI().shape(box).color(activeBorderColor);
      inactiveBox = new BoxTUI().shape(box).color(inactiveBorderColor);

      if (isResponsive) {
        activeBox.responsive();
        inactiveBox.responsive();
      }
    }
  }

  private String render(int selected) {
    StringBuilder sb = new StringBuilder();
    
    if(active) activeIcon.setColor(activeIconColor);
    else activeIcon.setColor(inactiveIconColor);

    if(box.equals(boxShape.None) && !prompt.isEmpty()) sb.append("\r" + prompt);
    sb.append("\n");

    int[] capacity = options.window(selected);

    for (int i = capacity[0]; i < capacity[1]; i++) {
      String option = options.options()[i];
      TextTUI opt = new TextTUI(option);
      String desc = options.descs()[i];
      int descSpace = options.descSpacing();

      
      if (i == selected) {
        if(invert) opt.invert();
        sb.append(" ".repeat(buffer))
          .append(" " + activeIcon + " ")
          .append(opt.setColor(activeColor).toString())
          .append(" ".repeat(descSpace - option.length()))
          .append(new TextTUI(desc).setColor(descColor).toString())
          // .append("\r")
          .append("\n");
      }
      else {
        sb.append(" ".repeat(buffer))
          .append(" " + inactiveIcon + " ")
          .append(opt.setColor(inactiveColor).toString())
          .append(" ".repeat(descSpace - option.length()))
          .append(new TextTUI(desc).setColor(descColor).toString())
          // .append("\r")
          .append("\n");
      }
    }
    
    StringBuilder sb2 = new StringBuilder();
    if(!box.equals(boxShape.None)){
      TextTUI content = new TextTUI(sb.toString());
      activeBox.innerText(content).label(new TextTUI(prompt));
      inactiveBox.innerText(content).label(new TextTUI(prompt));
      sb2.append(active ? activeBox.build() : inactiveBox.build());
    } else sb2.append(sb.toString());

    if (!isContainer) {
      System.out.println(sb2.toString());
      System.out.flush();
      return "";
    } 
    else return sb2.toString();
  }

  /**
   * Run the Selector
   */
  public void execute() {
    initBorderBox();
    options.init();
    prompt();
  }

  private void prompt() {
    OnClick.reset();
    selected = preSelect > 0 && preSelect < options.size() ? preSelect : active ? 0 : -1;

    render(selected);
    setupHandlers();

    try {
      OnClick.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setupHandlers() {
    int listSize = options.options().length;
    int totalLines = box.equals(boxShape.None) ? listSize + 2 : listSize + 4; // 2 for "prompt" line

    OnClick.add(KeyPress.Up_Arrow, () -> {
      if (active) {
        if(!isContainer) options.clearLines(totalLines);
        selected--;
        selected = selected >= 0 ? selected : listSize-1;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Down_Arrow, () -> {
      if (active) {
        if(!isContainer) options.clearLines(totalLines);
        selected++;
        selected = selected < listSize - 1 ? selected : selected % listSize;
        render(selected);
      }
    });

    OnClick.add(KeyPress.Enter, () -> {
      if (active) {
        activeIcon.setColor(inactiveIconColor);
        render(selected);
        if (clear && !isContainer) {
          options.clearLines(totalLines); // clear option after select
          System.out.flush();
        }
        if (!isContainer) OnClick.cancel();
        active = false;
        variable = selected;
        if (onSubmitCallback != null) onSubmitCallback.accept(variable);
      }
    });

    if (start != null) {
      OnClick.add(start, () -> {
        if (!active) {
          active = true;
          if(!isContainer) options.clearLines(totalLines);
          selected = selected == -1 ? 0 : selected;
          render(selected);
        }
      });
    }
    
    if (stop != null) {
      OnClick.add(stop, () -> {
        if (active && !disableActivation) {
          active = false;
          if(!isContainer) options.clearLines(totalLines);
          // selected = -1;
          render(selected);
        }
      });
    }


    OnClick.add(KeyModifier.CTRL.with('c'), () -> {
      if(!isContainer) {
        if (onSubmitCallback != null) onSubmitCallback.accept(-1);
        OnClick.cancel();
      }
    });
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

  private boolean isContainer = false;
  private boolean initialized = false;

  /**
   * <ul>
   * <li>Used Only in Container Class</li>
   * <li>Do not use it; it will be called by default in the Container.</li>
   * </ul>
  */
  @Override
  public String fire() {
    if (!initialized) {
      isContainer = true;
      initBorderBox();
      options.init();
      selected = preSelect > 0 && preSelect < options.size() ? preSelect : active ? 0 : -1;
      initialized = true;
    }
    return render(selected);
  }

  /**
   * <ul>
   * <li>Used Only in Container Class</li>
   * <li>Do not use it; it will be called by default in the Container.</li>
   * </ul>
  */
  @Override
  public void onFocus() {
    setupHandlers();
    active = true;
    selected= variable != -1 ? variable : preSelect > 0 && preSelect < options.size() ? preSelect : 0;
  }

  /**
   * <ul>
   * <li>Used Only in Container Class</li>
   * <li>Do not use it; it will be called by default in the Container.</li>
   * </ul>
  */
  @Override
  public void onBlur() {
    selected = variable != -1 ? variable : selected;
    active = false;
  }

  @Override
  public KeyHandle getStartKey() {
    return start;
  }
  @Override
  public KeyHandle getStopKey() {
    return stop;
  }
  @Override
  public boolean isFocusable() {
    return true;
  }
  @Override
  public SelectorTUI startActive(boolean startActive) {
    this.active = startActive;
    return this;
  }

  public boolean isActive() {
    return active;
  }

}
