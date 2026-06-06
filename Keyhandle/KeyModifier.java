package Keyhandle;

public enum KeyModifier implements KeyHandle {
  CTRL {
    @Override
    public KeyHandle with(char c) {
      int value = Character.toUpperCase(c);
      if (value < 'A' || value > 'Z') throw new IllegalArgumentException("CTRL only supports A-Z");
      int code = value - 64;
      return () -> String.format("0x%02X", code);
    }

  },
  ALT {
    @Override
    public KeyHandle with(char c) {
      return () -> "ALT:" + c;
    }
  };
  //ALT
  
  @Override
  public String press() {
    throw new UnsupportedOperationException(
      "KeyModifier has no standalone press value — use " + name() + ".with('X') instead"
    );
  }

  public abstract KeyHandle  with(char c);
}
