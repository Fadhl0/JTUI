package utils;
import java.util.ArrayList;
import java.util.List;

public class QuoteTUI {
  private List<TextTUI> text = new ArrayList<>();
  private StringBuffer allText = new StringBuffer();
  private String backgroundColor = "#1d1d1d";
  private String borderColor = "#cccccc";
  private TextTUI border = new TextTUI("▐");

  public QuoteTUI(TextTUI[] text) {
    for (TextTUI t : text) {
      this.text.add(t);
    }
  }

  public QuoteTUI append(TextTUI text) {
    text = new TextTUI(text.toString().replaceAll("\n", ""));
    this.text.add(text);
    return this;
  }

  public QuoteTUI() {}

  public QuoteTUI setBackgroundColor(Colors background) {
    this.backgroundColor = background.getColor();
    return this;

  }
  public QuoteTUI setBackgroundColor(String background) {
    this.backgroundColor = background;
    return this;

  }

  public QuoteTUI setBorderColor(Colors background) {
    this.borderColor = background.getColor();
    return this;

  }
  public QuoteTUI setBorderColor(String background) {
    this.borderColor = background;
    return this;

  }

  private String display() {
    border.setColor(this.borderColor);

    int max = findMax();

    allText.append("\n");
    breakLine(max);
    for (TextTUI t : text) {
      int spaceNumber = max - t.length();
      TextTUI spaces = new TextTUI(" ".repeat(spaceNumber + 2));
      spaces.setBackgroundColor(this.backgroundColor);

      TextTUI initSpaces = new TextTUI("  ");
      initSpaces.setBackgroundColor(this.backgroundColor);

      String row;
      if (t.isStyled()) {
        String ansiRegex = "\u001B\\[[;\\d]*m";
        String style = ANSI.Start.toString() + ANSIformat.formatBG(backgroundColor) + "m";
        String result = t.toString().replaceAll("(" + ansiRegex + ")", "$1" + style);
        row = result;
      } else {
        t.setBackgroundColor(this.backgroundColor);
        row = t.toString();
      }

      allText.append(ANSI.Reset)
        .append("\n").append(border).append(initSpaces)
        .append(row).append(spaces);
    }
    breakLine(max);
    allText.append("\n");

    return allText.toString();
  }

  private void breakLine(int max) {
    TextTUI maxSpaces = new TextTUI(" ".repeat(max + 4));
    maxSpaces.setBackgroundColor(this.backgroundColor);
    allText.append("\n").append(border).append(maxSpaces);

  }

  public QuoteTUI breakLine() {
    append(new TextTUI());
    return this;

  }

  private int findMax() {
    int max = text.get(0).length();
    for (TextTUI t : text) {
      if(t.length() > max) {
        max = t.length();
      }
    }

    return max;
  }

  public String toString() {
    return display();
  }

  // private static int getIndex(String sentence, String word) {
  //   Pattern pattern = Pattern.compile(word);
  //   Matcher matcher = pattern.matcher(sentence);
  //   if (matcher.find()) return matcher.end();
  //   return 0;
  // }

  // private static String insertBG(String text, int index, String toInsert) {
  //   return text.replaceAll("^(.{" + index + "})", "$1" + toInsert);
  // }

}

