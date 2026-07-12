package inputForm;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionSet {
  private final Map<String, String> list = new LinkedHashMap<>();
  private String[] options = new String[0];
  private String[] descs = new String[0];
  private int descSpacing;
  private int limit = -1;

  public void add(String option, String description) {
    list.put(option.replaceAll("\n", ""), description.replaceAll("\n", ""));
  }
  public void limitDisplay(int limit) { if (limit > 3) this.limit = limit; }
  public void init() {
    if (!list.isEmpty()) {
      options = list.keySet().toArray(new String[0]);
      descs = list.values().toArray(new String[0]);
      descSpacing = list.keySet().stream()
              .max(Comparator.comparingInt(String::length)).orElse("").length() + 2;
    }
  }
  public String[] options() { return options; }
  public String[] descs()   { return descs; }
  public int size()          { return options.length; }
  public int descSpacing()   { return descSpacing; }

  private int windowStart = 0;

  public int[] window(int pivot) {
    int total = options.length;

    if (limit == -1 || total <= limit) {
      return new int[]{ 0, total };
    }

    int maxStart = total - limit;

    if (pivot < windowStart) {
      windowStart = pivot;
    } else if (pivot >= windowStart + limit - 2) {
      windowStart = pivot - (limit - 2);
    }

    windowStart = Math.max(0, Math.min(windowStart, maxStart));
    return new int[]{ windowStart, windowStart + limit };
  }

  public int getRowsLength() {
    return limit > 3 ? limit : list.size();
  }

  public void clearLines(int n) {
    for (int i = 0; i < n; i++) System.out.print(TUICursor.CURSOR_UP.toString() + TUICursor.CLEAR_LINE.toString());
  }
  
  public void clear() {
    list.clear();
    options = new String[0];
    descs = new String[0];
    descSpacing = 0;
    windowStart = 0;
  }
}
