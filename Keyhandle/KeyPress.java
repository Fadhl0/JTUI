package Keyhandle;

// https://gist.github.com/ConnerWill/d4b6c776b509add763e17f9f113fd25b

// F1-F4, Home, End, Backspace, Enter may not work in Win
public enum KeyPress implements KeyHandle {

  Slash("0x2F"),
  Dot("0x2E"),
  Minus("0x2D"),
  Plus("0x2B"),
  Equal("0x3D"),
  Colon("0x3A"),
  SemiColon("0x3B"),
  Exclamation("0x21"),
  AtSymbol("0x40"),
  OpenBracket("0x5B"),
  CloseBracket("0x5D"),
  OpenBrace("0x7B"),
  CloseBrace("0x7D"),
  Quote("0x22"),
  Hash("0x23"),
  Underscore("0x5F"),
  Question("0x3F"),
  Dollar("0x24"),
  Percent("0x25"),
  Caret("0x5E"),
  Tilde("0x7E"),
  Pipe("0x7C"),
  Asterisk("0x2A"),
  Apostrophe("0x27"),
  Ampersand("0x26"),
  OpenParen("0x28"),
  CloseParen("0x29"),
  LessThan("0x3C"),
  GreaterThan("0x3E"),
  Comma("0x2C"),
  BackTick("0x60"),
  BackSlash("0x5C"),
  
  AnyKey("All"),
  Enter("0x0A"),      // Or 0x0D
  Backspace("0x7F"),  // Or 0x08
  Space("0x20"),
  Tab("0x09"),
  Escape("0x1B"),

  Up_Arrow("A"),
  Down_Arrow("B"),
  Right_Arrow("C"),
  Left_Arrow("D"),
  Delete("3~"),
  Home("H"),      // Or 1~
  End("F"),       // Or 4~
  F1("P"),
  F2("Q"),
  F3("R"),
  F4("S"),
  // F1("11~"),      // Or P (SS3)
  // F2("12~"),      // Or Q (SS3)
  // F3("13~"),      // Or R (SS3)
  // F4("14~"),      // Or S (SS3)
  F5("15~"),
  F6("17~"),
  F7("18~"),
  F8("19~"),
  F9("20~"),
  F10("21~"),
  F11("23~"),
  F12("24~");
  
  private final String ansi;
  KeyPress(String ansi) {
    this.ansi = ansi;
  }

  public String press() {
    return this.ansi;
  }
}
