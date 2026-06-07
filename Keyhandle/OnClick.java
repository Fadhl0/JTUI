package Keyhandle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import inputForm.TUICursor;
import utils.Component;
import utils.WindowsAPI;

public class OnClick {
  private static final LinkedHashMap<KeyHandle, Runnable> listeners = new LinkedHashMap<>();
  private static final List<Runnable> liveListeners = new ArrayList<>();
  private static final List<Runnable> onClose = new ArrayList<>();
  private static Consumer<Character> runnable;

  private static boolean cancelled = false;
  private static StringBuffer sb = new StringBuffer();

  public static void add(KeyHandle type, Runnable runnable) {
    listeners.put(type, runnable);
  }

  public static void addLive(Runnable runnable){
    liveListeners.add(runnable);
  }
  public static void OnClose(Runnable runnable){
    onClose.add(runnable);
  }

  public static void TypingReturn(Consumer<Character> run) {
    runnable = run;
  }

  public static void cancel() {
    cancelled = true;
  }

  public static String userInput() {
    return sb.toString();
  }
  
  public static void execute() throws Exception {
    WindowsAPI.apply();
    cancelled = false;
    System.out.print(TUICursor.HIDE_CURSOR);
    System.out.flush();
    Component.enableRawMode();

    Thread.sleep(80);

    try {
      InputStream in = System.in;
      while (!cancelled) {

        liveListeners.forEach((e) -> e.run());

        int key = in.read();
        if (key == -1) continue;

        if (key == '\r') key = '\n';

        if (hasListener(KeyPress.AnyKey.press())){
          fireListener(KeyPress.AnyKey);
          break;
        }

        // CTRL+C handling (Decimal 3)
        if (key == 3) {
          String ctrlC = toHEX(3); // "0x03"
          if (!hasListener(ctrlC)) {
            cancelled = true;
            break;
          }
        }

        if (key == 0x1B) {
          handleEscape(in); // escape sequence
        } else {
          dispatch(toHEX(key)); // normal key + KeyModifer
        }

        if (key >= 0x20 && key <= 0x7E) {
          sb.append((char)key);
          if(runnable != null) runnable.accept((char)key);
        }
      }
    } catch(Exception e) {
        e.printStackTrace();

    } finally {
        onClose.forEach((e) -> e.run());
        Component.disableRawMode();
        System.out.print(TUICursor.SHOW_CURSOR);
        System.out.flush();
    }
  }

  private static void handleEscape(InputStream in) throws IOException {
    try { Thread.sleep(20); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

    if (in.available() == 0) { // only ESC
      fireListener(KeyPress.Escape);
      return;
    }

    int key2 = in.read(); // next sequence

    if (key2 == '[') { // CSI sequence
      StringBuilder seq = new StringBuilder();
      while (in.available() > 0) {
        int next = in.read();
        seq.append((char) next);
        if ((next >= 'A' && next <= 'Z') || (next >= 'a' && next <= 'z') || next == '~') {
          break;
        }
      }
      dispatchSequence(seq.toString());

    } else if (key2 == 'O') { // SS3 sequence
      if (in.available() > 0) {
        int key3 = in.read();
        dispatchSequence(String.valueOf((char) key3));
      }
    } else {
      dispatchSequence("ALT:" + (char) key2);
    }
  }

  public static void reset() {
    listeners.clear();
    liveListeners.clear();
    sb.setLength(0);
    runnable = null;
    cancelled = false;
  }

  private static void dispatch(String hex) {
    for (Map.Entry<KeyHandle, Runnable> entry : listeners.entrySet()) {
      if (entry.getKey().press().equals(hex)) {
        entry.getValue().run();
        return;
      }
    }
  }

  private static void dispatchSequence(String seq) {
    for (Map.Entry<KeyHandle, Runnable> entry : listeners.entrySet()) {
      if (entry.getKey().press().equals(seq)) {
        entry.getValue().run();
        return;
      }
    }
  }

  private static void fireListener(KeyHandle key) {
    Runnable r = listeners.get(key);
    if (r != null) r.run();
  }

  private static boolean hasListener(String pressValue) {
    for (KeyHandle k : listeners.keySet()) {
      if (k.press().equals(pressValue)) return true;
    }
    return false;
  }

  private static String toHEX(int key) {
    return String.format("0x%02X", key);
  }
}
