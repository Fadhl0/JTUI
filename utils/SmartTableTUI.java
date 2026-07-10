package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import inputForm.SelectorTUI;
import inputForm.TUIComponent;
import inputForm.boxBorder.BoxTUI;
import inputForm.boxShape;

public class SmartTableTUI implements TUIComponent {
  private List<String> head = new ArrayList<>();
  private List<String[]> rows = new ArrayList<>();
  private SelectorTUI selectors = new SelectorTUI();
  private KeyHandle startkey = KeyModifier.CTRL.with('t');
  private KeyHandle stopKey = KeyPress.Escape;
  private volatile boolean activte = true;
  private boxShape shape = boxShape.SingleLine;
  private String activeColor = "#ffffff";
  private String inactiveColor = "#969696";
  
  private int columns = -1;

  public SmartTableTUI(String ...header){
    if (header.length == 0) {
      throw new IllegalArgumentException("SmartTableTUI requires at least one header column");
    }

    for (int i = 0; i < header.length; i++) {
      String newVal = header[i].replaceAll("\n", "");
      this.head.add(newVal);
    }
    columns = this.head.size();

    selectors.startKey(startkey)
             .stopKey(stopKey)
             .startActive(activte)
             .setBuffer(0)
             .preSelect(0)
             .invertActiveSelectors()
             .setActiveSelectorIcon("", "")
             .setInactiveSelectorIcon("", "");
  }

  public SmartTableTUI activeColor(String color){ 
    this.activeColor = color;
    return this;
  }
  public SmartTableTUI inactiveColor(String color){ 
    this.inactiveColor = color;
    return this;
  }
  public SmartTableTUI activeColor(Colors color){ 
    this.activeColor = color.getColor();
    return this;
  }
  public SmartTableTUI inactiveColor(Colors color){ 
    this.inactiveColor = color.getColor();
    return this;
  }
  public SmartTableTUI startKey(KeyHandle key) {
    selectors.startKey(key);
    startkey = key;
    return this;
  }
  public SmartTableTUI stopKey(KeyHandle key) {
    selectors.stopKey(key);
    stopKey = key;
    return this;
  }

  /**
   * Adding a varible that returns when submit.
   * @param set
   */
  public SmartTableTUI onSubmit(Consumer<Integer> set) {
    selectors.onSubmit(set);
    return this;
  }

  public SmartTableTUI tableRows(String ...rows) {
    String[] newRow = new String[columns];
    int limit = Math.min(rows.length, columns);
    for (int i = 0; i < limit; i++) {
        newRow[i] = rows[i].replaceAll("\n", "");
    }
    this.rows.add(newRow);
    return this;
  }

  private String[] allRows;
  private int addedRowCount = 0;

  private String render() {
    BoxTUI box = new BoxTUI().shape(shape);
    StringBuilder sb = new StringBuilder();

    allRows = options();
    sb.append("  " + allRows[0]);

    while (addedRowCount < rows.size() && (addedRowCount + 1) < allRows.length) {
      selectors.add(Component.visibleText(allRows[addedRowCount + 1]));
      addedRowCount++;
    }

    sb.append(horizontalLine(allRows[0].length()));

    if (selectors.isActive()) box.color(activeColor);
    else box.color(inactiveColor);

    String result = selectors.fire();
    if (result != null && result.length() > 1) {
      String sub = result.substring(1, result.length() - 1);
      sb.append(sub);
    } else sb.append("No Data To Display!");

    box.innerText(new TextTUI(sb.toString()));
    return new TableBox(box).build();
  }

  private String horizontalLine(int length) {
    TextTUI line = new TextTUI("─".repeat(length)).setColor("#4b4d5c");
    return "\n" + line + "\n";
  } 

  private String[] options() {
    TableTUI box = new TableTUI();
    TextTUI[] allHead = new TextTUI[columns];
    String color = selectors.isActive() ? activeColor : inactiveColor ;

    // head
    for (int i = 0; i < head.size(); i++) {
      allHead[i] = new TextTUI(head.get(i)).setColor(color);
    }
    box.addRow(allHead);

    // rows
    for (String[] row : rows) {
      TextTUI[] singleRow = Arrays.stream(row)
                                  .map(TextTUI::new)
                                  .toArray(TextTUI[]::new);
      box.addRow(singleRow);
    }
    
    return box.toString().split("\n", -1);
  }

  @Override
  public String fire() {
    return render();
  }

  @Override
  public KeyHandle getStartKey() {
    return startkey;
  }

  @Override
  public KeyHandle getStopKey() {
    return stopKey;
  }

  @Override public boolean isFocusable() { return true; }

  @Override public void onFocus() {
    activte = true;
    selectors.onFocus();
  }
  @Override public void onBlur() {
    activte = false;
    selectors.onBlur();
  }
  @Override public SmartTableTUI startActive(boolean startActive) {
    selectors.startActive(startActive);
    activte = startActive;
    return this;
  }

}
