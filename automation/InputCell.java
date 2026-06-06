package automation;

import java.util.function.Consumer;
import java.util.function.Predicate;

import inputForm.InputType;
import inputForm.TUICursor;
import inputForm.boxShape;
import inputForm.inputTUI;
import inputForm.inputTUI.InputTUI;
import utils.Colors;
import utils.Component;
import utils.Icons;
import utils.TextTUI;

public class InputCell implements Cell {

  private String prompt;
  private InputType type = InputType.Text;

  private TextTUI  activeBorder;
  private TextTUI  inactiveBorder;
  private TextTUI  activeIcon;
  private TextTUI  inactiveIcon;
  private int      buffer;

  private String warningColor = "#f3eb67";
  private String warningIcon  = Icons.Tringle.get();

  TextTUI lastBorder = new TextTUI("\n└");
  private InputTUI input = new InputTUI()
                              .shape(boxShape.None)
                              .setAppendText(
                                () -> new TextTUI("\n")
                                          .appendText(activeBorder)
                                          .appendText(
                                            lastBorder.setColor(activeBorder.getColor())
                                          )
                                          .appendText(new TextTUI(" ".repeat(buffer)))
                              );
  private String submitColor = "#2c9a1d";
                
  public InputCell() {}

  public InputCell setTitle(TextTUI title) {
    prompt = title.toString();
    return this;
  }

  public InputCell setPlaceholder(TextTUI placeholder) {
    input.placeholder(placeholder);
    return this;
  }
  public InputCell setMaxWidth(int width) {
    input.width(width);
    return this; 
  }
  public InputCell setLimitInput(int max) {
    input.maxLength(max);
    return this;
  }
  public InputCell setValidator(Predicate<String> validator, String errorMessage) {
    TextTUI err = new TextTUI(warningIcon + " " + errorMessage).setColor(warningColor);

    input.validator(validator, err);
    return this;
  }
  public InputCell onSubmit(Consumer<String> callback) {
    input.onSubmit(callback);
    return this;
  }

  public InputCell setWarnIcon(String icon) {
    warningIcon = icon;
    return this;
  }
  public InputCell setWarnColor(String color) {
    warningColor = color;
    return this;
  }
  public InputCell setWarnColor(Colors color) {
    warningColor = color.getColor();
    return this;
  }

  public InputCell type(InputType type) {
    this.type = type;
    input.type(type);
    return this;
  }
  public InputCell setIcon(TextTUI icon) { input.setIcon(icon); return this; }

  public InputCell setSubmitColor(Colors color) {
    submitColor = color.getColor();
    return this;
  }
  public InputCell setSubmitColor(String color) {
    submitColor = color;
    return this;
  }
  
  private void moveToTop(int height) {
    System.out.print("\r");
    for (int i = 0; i < height; i++) System.out.print(TUICursor.CURSOR_UP);
  }
  private void render() {
    String cell = new TextCell()
                        .setText(new TextTUI(prompt + "\n"))
                        .setInactiveBorder(activeBorder)
                        .setInactiveIcon(activeIcon)
                        .setBuffer(buffer)
                        .textAutomation();

    String cell1 = Component.visibleSubstring(cell, 0, Component.visibleLength(cell) - 1);
    
    input.setPrependText(() -> new TextTUI(cell1));
    input.handleCancellation(() -> {
      System.out.print(TUICursor.SHOW_CURSOR);
      Automation.cancel();
    });

    inputTUI render = input.build();

    String result = render.prompt();
    
    Component.enableRawMode();
     // clear option after select
    if(!Automation.isCancelled()) {
      System.out.print("\033[J"); // clear all text bottom
      moveToTop(getHeight() - 1);
      lastMessage(result);
    }
  }

  public void run() {
    render();
  }

  private void lastMessage(String result) {
    TextTUI text;
    if (type.equals(InputType.Hidden)) {
      text = new TextTUI();
    } else if (type.equals(InputType.Password)) {
      int len = result.length();
      text = new TextTUI("•".repeat(len)).setColor(submitColor);
    } else {
      text = new TextTUI(result).setColor(submitColor);
    }

    if (result.length() == 0) {
      int placeholderLength = Component.visibleLength(input.getPlaceholder().toString());
      text.innerText(" ".repeat(placeholderLength));
    }
    TextCell InputCell = new TextCell()
                        .setText(new TextTUI(prompt + "\n" + text + " \n"))
                        .setInactiveBorder(inactiveBorder)
                        .setInactiveIcon(inactiveIcon)
                        .setBuffer(buffer);

    InputCell.run();
    // return InputCell.textAutomation();
  }

  // InputCell
  public String getTitle() {
    return prompt;
  }
  public int getHeight() {
    int len1 = prompt.split("\n", -1).length;
    return len1 + 3; // 2 is for lastBorder 
  }

  public InputCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }

  public InputCell setActiveIcon(TextTUI icon) {
    activeIcon = icon;
    return this;
  }
  public InputCell setInactiveIcon(TextTUI icon) {
    inactiveIcon = icon;
    return this;
  }
  public InputCell setActiveBorder(TextTUI border) {
    activeBorder = border;
    return this;
  }
  public InputCell setInactiveBorder(TextTUI border) {
    inactiveBorder = border;
    return this;
  }

  public String getId() {
    return "<Input>";
  }
}
