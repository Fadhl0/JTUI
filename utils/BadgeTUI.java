package utils;

import java.util.ArrayList;
import java.util.List;

public class BadgeTUI {
  private List<TextTUI> banner = new ArrayList<>();
  private boolean pathway = false;
  public BadgeTUI(){}
  
  public BadgeTUI append(TextTUI title) {
    if (title.isEmpty()) return this;
    title.innerText(" " + title.getText() + " ");
    this.banner.add(title);
    return this;
  }
  public BadgeTUI pathway() {
    this.pathway = true;
    return this;
  }

  public String toString() {
    return banner();
  }

  private String banner() {
    StringBuilder sb = new StringBuilder();
    int len = banner.size();

    for (int i = 0; i < len; i++) {
      TextTUI e = banner.get(i);
      TextTUI arrow = new TextTUI(pathway ? "\uE0B0" : "");

      if (!e.getBackgroundColor().isEmpty()) {
        arrow.setColor(e.getBackgroundColor());
      } else {
        e.invert();
        arrow.setColor(e.getColor());
      }
      
      if (i + 1 < len) {
        TextTUI temp = banner.get(i + 1);
        String color = temp.getBackgroundColor().length() == 0 
                       ? banner.get(i+1).getColor()
                       : temp.getBackgroundColor();
        
        arrow.setBackgroundColor(color);
      }
      e.bold();
      if (e.getBackgroundColor().isEmpty()) {
        e.invert();
      }
        
      sb.append(e.toString()).append(arrow.toString());
    }

    return sb.toString();
  }
}
