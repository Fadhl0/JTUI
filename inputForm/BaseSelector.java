package inputForm;

import utils.Colors;
import utils.Icons;
import utils.TextTUI;

public abstract class BaseSelector<T extends BaseSelector<T>> {
  protected OptionSet options = new OptionSet();
  protected String activeColor = "#ffffff";
  protected String inactiveColor = "#969696";
  protected String descColor = "#969696";

  protected int buffer = 2;
  protected String prompt = "";
  protected String activeIconColor = "#2c9a1d";
  protected String inactiveIconColor = "#969696";

  protected TextTUI activeIcon = new TextTUI(Icons.CircleSolid.get()).setColor("#2c9a1d");
  protected TextTUI inactiveIcon = new TextTUI(Icons.CircleDotted.get()).setColor("#969696");

  protected BaseSelector() {}

  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  /**
   * Adding option and description.
   * @param option
   * @param description
   * @return
   */
  public T add(String option, String description) {
    options.add(option, description);
    return self();
  }

  /**
   * Adding option.
   * @param option
   * @return
   */
  public T add(String option) {
    options.add(option, "");
    return self();
  }

  public T setActiveColor(String color) {
    activeColor = color;
    return self();
  }
  public T setInactiveColor(String color) {
    inactiveColor = color;
    return self();
  }
  public T setDescriptionColor(String color) {
    descColor = color;
    return self();
  }
  public T setActiveColor(Colors color) {
    activeColor = color.getColor();
    return self();
  }
  public T setInactiveColor(Colors color) {
    inactiveColor = color.getColor();
    return self();
  }
  public T setDescriptionColor(Colors color) {
    descColor = color.getColor();
    return self();
  }
  public T setBuffer(int buffer) {
    this.buffer = buffer;
    return self();
  }
  public String getTitle() {
    return prompt;
  }

  public T setInactiveSelectorIcon(String icon, String color) {
    inactiveIcon = new TextTUI(icon).setColor(color);
    inactiveIconColor = color;
    return self();
  }

  public T setActiveSelectorIcon(String icon, String color) {
    activeIcon = new TextTUI(icon).setColor(color);
    activeIconColor = color;
    return self();
  }

  public T setInactiveSelectorIcon(String icon, Colors color) {
    inactiveIcon = new TextTUI(icon).setColor(color);
    inactiveIconColor = color.getColor();
    return self();
  }

  public T setActiveSelectorIcon(String icon, Colors color) {
    activeIcon = new TextTUI(icon).setColor(color);
    activeIconColor = color.getColor();
    return self();
  }

  /**
   * limit the number of options that display.
   * <ul><li>default is -1, means no limit</li></ul>
   * @param limit number of limit (must be greater than 3)
   */
  public T limitDisplay(int limit){
    options.limitDisplay(limit);
    return self();
  }

  // each subclass decides whether newlines survive
  public abstract T setTitle(TextTUI prompt);
}