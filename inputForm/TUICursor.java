package inputForm;

public enum TUICursor {
  CLEAR_LINE("\033[2K\r"),
  HIDE_CURSOR("\033[?25l"),
  SHOW_CURSOR("\033[?25h"),
  CURSOR_UP("\033[1A");

  private String ansi;
  private TUICursor(String ansi){
    this.ansi = ansi;
  }

  public String toString() {
    return this.ansi;
  }
}

// below usiful code for advanced Table

// In Container, track a scroll offset
// private int scrollOffset = 0;
// private int totalLines = 0; // count rendered lines

// // In OnClick, add scroll keys
// OnClick.add(KeyPress.ArrowUp,   () -> container.scroll(-1));
// OnClick.add(KeyPress.ArrowDown, () -> container.scroll(+1));

// // In Container.scroll()
// public void scroll(int delta) {
//     scrollOffset = Math.max(0, Math.min(scrollOffset + delta, totalLines - terminalHeight));
// }