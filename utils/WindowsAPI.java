package utils;

import java.util.concurrent.atomic.AtomicBoolean;

// Singleton
public final class WindowsAPI {
  private static final AtomicBoolean applied = new AtomicBoolean(false);

  private WindowsAPI() {} 

  public static void apply() {
    if (applied.compareAndSet(false, true)) {
      try {
          if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd.exe", "/c", "chcp 65001 >NUL 2>&1")
                  .inheritIO()
                  .start()
                  .waitFor();
          }
      } catch (Exception e) {
        throw new RuntimeException("Failed to apply Windows API settings", e);
      }
    }
  }
}
