package utils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inputForm.TUICursor;

@Deprecated
public class ContainerOld {
  private final CopyOnWriteArrayList<Supplier<String>> listeners = new CopyOnWriteArrayList<>();
  private final LinkedList<String> listenerValues = new LinkedList<>();
  private final ArrayList<Boolean> newLine = new ArrayList<>();
  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private final int[] preValue = new int[2];

  public void append(Supplier<String> methodReference) {
    listeners.add(methodReference);
    listenerValues.add(methodReference.get());
    newLine.add(false);
  }
  public void appendText(TextTUI text) {
    listeners.add(text::toString);
    listenerValues.add(text.toString());
    newLine.add(true);
  }

  public void stop() {
    System.out.print(TUICursor.SHOW_CURSOR);
    executor.shutdown();
    Component.setLive(false);
  }

  public void execute() {
    // System.out.print("\033[?1049h"); // enter alternate screen and hide scroll
    System.out.print(TUICursor.HIDE_CURSOR);
    System.out.flush();

    preValue[0] = 0; preValue[1] = 0;
    Component.setLive(true);
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      Component.clear();
      // System.out.print("\033[?1049l");
      System.out.println(TUICursor.SHOW_CURSOR);
      System.out.flush();
    }));
    
    executor.scheduleAtFixedRate(() -> {
      int[] size = Component.getTerminalSize();
      Component.setTerminalWidth(size[1]);
      
      boolean dataChanged = false;
      for (int i = 0; i < listeners.size(); i++) {
        String target = listeners.get(i).get();
        String init = listenerValues.get(i);
        if (!Objects.equals(target, init)) {
          listenerValues.set(i, target);
          dataChanged = true;
          break;
        }
      }

      if (preValue[0] != size[0] || preValue[1] != size[1] || dataChanged) {
        Component.clear();
        
        listeners.forEach(method -> {
          String element = method.get();
          StringBuilder allText = new StringBuilder();

          if (Boolean.TRUE.equals(newLine.get(listeners.indexOf(method)))) {
            allText.append(element).append("\n");
            System.out.println(allText.toString());
            return;
          }

          Pattern ansiRegex = Pattern.compile("\u001B\\[[;\\d]*m");
          String output = String.valueOf(element);
          Matcher matcher = ansiRegex.matcher(output);

          String[] parts = output.split("\n");
          String[] partsANSI = matcher.replaceAll("").split("\n");

          for (int i = 0; i < partsANSI.length; i++) {
            if (partsANSI[i].length() > size[1] && !matcher.find()) {
              String p = Component.visibleSubstring(parts[i], 0, size[1]);
              allText.append(p).append(ANSI.Reset).append("\n");
            } else allText.append(parts[i]).append("\n");
          }
          System.out.println(allText.toString());
        });
        System.out.flush();

        preValue[0] = size[0]; preValue[1] = size[1];
        dataChanged = false;
      }
      System.out.print(TUICursor.HIDE_CURSOR);
    }, 0, 100, TimeUnit.MILLISECONDS);
  }
}
