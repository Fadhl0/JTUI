package automation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import inputForm.TUICursor;
import utils.Component;
import utils.TextTUI;

/**
 * shiny text effect loading
 */
public class ScannerCell implements Cell {
  private volatile String text;
  private TextTUI msg;
  private String mainColor = "#b5b5b5";
  private String altColor = "#FFFFFF";
  private volatile int interval = 70;
  private Runnable task;
  private ScheduledExecutorService executor;

  private TextTUI activeIcon;
  // private TextTUI activeBorder;
  private TextTUI inactiveIcon;
  private TextTUI inactiveBorder;

  private int buffer;

  private int height = 1;
	
  public ScannerCell(String text) {
    text = text.replace("\n", " ");

    if(text.length() < 3) {
      throw new IllegalArgumentException("text length must be more than 2 characters.");
    }

    this.text = text + " ".repeat(10);
  }

  public String getTitle() {
    return text.toString();
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public String getId() {
    return "<Scanner>";
  }

  private final AtomicInteger index = new AtomicInteger(0);
  @Override
  public void run() {
    Component.enableRawMode();
    System.out.print(TUICursor.HIDE_CURSOR);

    executor = Executors.newSingleThreadScheduledExecutor();

    executor.scheduleAtFixedRate(() -> {
      
      int len = Component.visibleLength(text);
      int currIndex = index.getAndUpdate(i -> (i + 1) % len);
      
      String[] textArr = getArr(text, currIndex);

      TextTUI spin1 = new TextTUI(textArr[0]).setColor(mainColor);
      TextTUI spin2 = new TextTUI(textArr[1]).setColor(altColor);
      TextTUI spin3 = new TextTUI(textArr[2]).setColor(mainColor);
      TextTUI finalText = new TextTUI()
                            .appendText(spin1)
                            .appendText(spin2)
                            .appendText(spin3);
      System.out.print("\r" + activeIcon + " ".repeat(buffer) + finalText);
      // OnClick.add(KeyModifier.CTRL.with('c'), error);

    }, 0, this.interval, TimeUnit.MILLISECONDS);
    
    try {
      task.run();
      stop();
    } finally {
			forceStop();
    }
  }
	public void forceStop() {
		if (executor == null || executor.isShutdown()) return;
    executor.shutdown();
	}

  public ScannerCell updateStatus(TextTUI text) {
    this.msg = text;
    return this;
  }
  public ScannerCell changeInterval(int interval){
    this.interval = interval;
    return this;
  }
  public ScannerCell status(String text){
		text = text.replace("\n", " ");
    index.set(0);
    System.out.print(TUICursor.CLEAR_LINE);
    this.text = text + " ".repeat(10);
    return this;
  }

  public ScannerCell withTask(Runnable task) {
		this.task = task;
    return this;
  }

  public void forceShutdown() {
    if (executor == null || executor.isShutdown()) return;
    executor.shutdown();
  }

  public void stop() {
		if (executor == null || executor.isShutdown()) return;

		if(!text.isEmpty()) {
			System.out.print("\r" + TUICursor.CLEAR_LINE);
			String[] textArr = msg.toString().split("\n", -1);
			height = textArr.length;
			
			StringBuilder sb = new StringBuilder();
			sb.append(inactiveIcon.toString());

			for (int i = 0; i < textArr.length; i++) {
				if(i != 0) sb.append(inactiveBorder.toString());
				sb.append(" ".repeat(buffer))
					.append(textArr[i])
					.append("\n");
			}
			System.out.print("\r" + sb.toString());
		}
    executor.shutdown();
  }

  private String[] getArr(String text, int index) {
    if (text == null) throw new IllegalArgumentException("Text must not be null.");
    int len = Component.visibleLength(text);
    int windowSize = 3;
    if (index < 0 || index >= len) {
      throw new IllegalArgumentException("Index is out of bounds for the given text.");
    }

    int end = Math.min(index + windowSize, len);

    return new String[] {
      text.substring(0, index),
      text.substring(index, end),
      text.substring(end)
    };
  }

  @Override
  public ScannerCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }

  @Override
  public ScannerCell setActiveBorder(TextTUI border) {
    // activeBorder = border;
    return this;
  }

  @Override
  public ScannerCell setInactiveBorder(TextTUI border) {
    inactiveBorder = border;
    return this;
  }

  @Override
  public ScannerCell setActiveIcon(TextTUI icon) {
    activeIcon = icon;
    return this;
  }

  @Override
  public ScannerCell setInactiveIcon(TextTUI icon) {
    inactiveIcon = icon;
    return this;
  }
  
}
