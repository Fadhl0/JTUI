package automation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import inputForm.TUICursor;
import utils.Colors;
import utils.Component;
import utils.TextTUI;


public class SpinnerCell implements Cell {
  private LinkedList<String> animation;
  private int interval;
  private volatile TextTUI message = new TextTUI();
  private volatile TextTUI text = new TextTUI(); // updatesStatus
  private String spinnerColor;
	private int height = 1;
	
	// private TextTUI activeIcon;
  // private TextTUI activeBorder;
  private TextTUI inactiveIcon;
  private TextTUI inactiveBorder;
	private int buffer;

	public SpinnerCell() {}

  public SpinnerCell setColor(Colors color) {
    spinnerColor = color.getColor();
    return this;
  }
  public SpinnerCell setColor(String color) {
    spinnerColor = color;
    return this;
  }
	
  public SpinnerCell status(TextTUI text){
		text.innerText(text.getText().replace("\n", " "));
		
    update(message, text);

    this.message = text;
    return this;
  }
	public SpinnerCell changeInterval(int interval){
    this.interval = interval;
    return this;
  }
  public SpinnerCell updateStatus(TextTUI text){
    this.text = text;
    return this;
  }
	public String getTitle() {
    return message.toString();
  }

  public SpinnerCell dotsCircle() {
    animation = new LinkedList<>(List.of(
      "⢎⡰",
      "⢎⡡",
      "⢎⡑",
      "⢎⠱",
      "⠎⡱",
      "⢊⡱",
      "⢌⡱",
      "⢆⡱"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell dots() {
    animation = new LinkedList<>(List.of(
      "⠋",
      "⠙",
      "⠹",
      "⠸",
      "⠼",
      "⠴",
      "⠦",
      "⠧",
      "⠇",
      "⠏"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell clock() {
    animation = new LinkedList<>(List.of(
      "🕛",
			"🕐",
			"🕑",
			"🕒",
			"🕓",
			"🕔",
			"🕕",
			"🕖",
			"🕗",
			"🕘",
			"🕙",
			"🕚"
    ));
    interval = 100;
    return this;
  }

  public SpinnerCell toggleSquare() {
    animation = new LinkedList<>(List.of(
      "◻",
			"■"
    ));
    interval = 120;
    return this;
  }

  public SpinnerCell toggleDiamond() {
    animation = new LinkedList<>(List.of(
      "◇",
			"◈"
    ));
    interval = 120;
    return this;
  }

  public SpinnerCell moon() {
    animation = new LinkedList<>(List.of(
      "🌑",
			"🌒",
			"🌓",
			"🌔",
			"🌕",
			"🌖",
			"🌗",
			"🌘"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell triangle() {
    animation = new LinkedList<>(List.of(
      "◢",
			"◣",
			"◤",
			"◥"
    ));
    interval = 50;
    return this;
  }

  public SpinnerCell arc() {
    animation = new LinkedList<>(List.of(
      "◜",
			"◠",
			"◝",
			"◞",
			"◡",
			"◟"
    ));
    interval = 100;
    return this;
  }

  public SpinnerCell pipeline() {
    animation = new LinkedList<>(List.of(
      "⊶",
			"⊷"
    ));
    interval = 250;
    return this;
  }

  public SpinnerCell noise() {
    animation = new LinkedList<>(List.of(
      "▓",
			"▒",
			"░"
    ));
    interval = 100;
    return this;
  }

  public SpinnerCell bounce() {
    animation = new LinkedList<>(List.of(
      "⠁",
			"⠂",
			"⠄",
			"⠂"
    ));
    interval = 120;
    return this;
  }

  public SpinnerCell boxBounce() {
    animation = new LinkedList<>(List.of(
      "▖",
			"▘",
			"▝",
			"▗"
    ));
    interval = 120;
    return this;
  }

  public SpinnerCell bouncingBar() {
    animation = new LinkedList<>(List.of(
      "[    ]",
			"[=   ]",
			"[==  ]",
			"[=== ]",
			"[====]",
			"[ ===]",
			"[  ==]",
			"[   =]",
			"[    ]",
			"[   =]",
			"[  ==]",
			"[ ===]",
			"[====]",
			"[=== ]",
			"[==  ]",
			"[=   ]"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell bouncingBall() {
    animation = new LinkedList<>(List.of(
      "( ●    )",
			"(  ●   )",
			"(   ●  )",
			"(    ● )",
			"(     ●)",
			"(    ● )",
			"(   ●  )",
			"(  ●   )",
			"( ●    )",
			"(●     )"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell star() {
    animation = new LinkedList<>(List.of(
      "✶",
			"✸",
			"✹",
			"✺",
			"✹",
			"✷"
    ));
    interval = 70;
    return this;
  }

  public SpinnerCell aesthetic() {
    animation = new LinkedList<>(List.of(
      "▰▱▱▱▱▱▱",
			"▰▰▱▱▱▱▱",
			"▰▰▰▱▱▱▱",
			"▰▰▰▰▱▱▱",
			"▰▰▰▰▰▱▱",
			"▰▰▰▰▰▰▱",
			"▰▰▰▰▰▰▰",
			"▰▱▱▱▱▱▱"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell pong() {
    animation = new LinkedList<>(List.of(
      "▐⠂       ▌",
			"▐⠈       ▌",
			"▐ ⠂      ▌",
			"▐ ⠠      ▌",
			"▐  ⡀     ▌",
			"▐  ⠠     ▌",
			"▐   ⠂    ▌",
			"▐   ⠈    ▌",
			"▐    ⠂   ▌",
			"▐    ⠠   ▌",
			"▐     ⡀  ▌",
			"▐     ⠠  ▌",
			"▐      ⠂ ▌",
			"▐      ⠈ ▌",
			"▐       ⠂▌",
			"▐       ⠠▌",
			"▐       ⡀▌",
			"▐      ⠠ ▌",
			"▐      ⠂ ▌",
			"▐     ⠈  ▌",
			"▐     ⠂  ▌",
			"▐    ⠠   ▌",
			"▐    ⡀   ▌",
			"▐   ⠠    ▌",
			"▐   ⠂    ▌",
			"▐  ⠈     ▌",
			"▐  ⠂     ▌",
			"▐ ⠠      ▌",
			"▐ ⡀      ▌",
			"▐⠠       ▌"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell dotsLong() {
    animation = new LinkedList<>(List.of(
      "⢀⠀",
			"⡀⠀",
			"⠄⠀",
			"⢂⠀",
			"⡂⠀",
			"⠅⠀",
			"⢃⠀",
			"⡃⠀",
			"⠍⠀",
			"⢋⠀",
			"⡋⠀",
			"⠍⠁",
			"⢋⠁",
			"⡋⠁",
			"⠍⠉",
			"⠋⠉",
			"⠋⠉",
			"⠉⠙",
			"⠉⠙",
			"⠉⠩",
			"⠈⢙",
			"⠈⡙",
			"⢈⠩",
			"⡀⢙",
			"⠄⡙",
			"⢂⠩",
			"⡂⢘",
			"⠅⡘",
			"⢃⠨",
			"⡃⢐",
			"⠍⡐",
			"⢋⠠",
			"⡋⢀",
			"⠍⡁",
			"⢋⠁",
			"⡋⠁",
			"⠍⠉",
			"⠋⠉",
			"⠋⠉",
			"⠉⠙",
			"⠉⠙",
			"⠉⠩",
			"⠈⢙",
			"⠈⡙",
			"⠈⠩",
			"⠀⢙",
			"⠀⡙",
			"⠀⠩",
			"⠀⢘",
			"⠀⡘",
			"⠀⠨",
			"⠀⢐",
			"⠀⡐",
			"⠀⠠",
			"⠀⢀",
			"⠀⡀"
    ));
    interval = 80;
    return this;
  }

  public SpinnerCell material() {
    animation = new LinkedList<>(List.of(
      "█▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"██▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"███▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"██████▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"██████▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"███████▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"████████▁▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"██████████▁▁▁▁▁▁▁▁▁▁",
			"███████████▁▁▁▁▁▁▁▁▁",
			"█████████████▁▁▁▁▁▁▁",
			"██████████████▁▁▁▁▁▁",
			"██████████████▁▁▁▁▁▁",
			"▁██████████████▁▁▁▁▁",
			"▁██████████████▁▁▁▁▁",
			"▁██████████████▁▁▁▁▁",
			"▁▁██████████████▁▁▁▁",
			"▁▁▁██████████████▁▁▁",
			"▁▁▁▁█████████████▁▁▁",
			"▁▁▁▁██████████████▁▁",
			"▁▁▁▁██████████████▁▁",
			"▁▁▁▁▁██████████████▁",
			"▁▁▁▁▁██████████████▁",
			"▁▁▁▁▁██████████████▁",
			"▁▁▁▁▁▁██████████████",
			"▁▁▁▁▁▁██████████████",
			"▁▁▁▁▁▁▁█████████████",
			"▁▁▁▁▁▁▁█████████████",
			"▁▁▁▁▁▁▁▁████████████",
			"▁▁▁▁▁▁▁▁████████████",
			"▁▁▁▁▁▁▁▁▁███████████",
			"▁▁▁▁▁▁▁▁▁███████████",
			"▁▁▁▁▁▁▁▁▁▁██████████",
			"▁▁▁▁▁▁▁▁▁▁██████████",
			"▁▁▁▁▁▁▁▁▁▁▁▁████████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁███████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁██████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████",
			"█▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████",
			"██▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███",
			"██▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███",
			"███▁▁▁▁▁▁▁▁▁▁▁▁▁▁███",
			"████▁▁▁▁▁▁▁▁▁▁▁▁▁▁██",
			"█████▁▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"█████▁▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"██████▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"████████▁▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"█████████▁▁▁▁▁▁▁▁▁▁▁",
			"███████████▁▁▁▁▁▁▁▁▁",
			"████████████▁▁▁▁▁▁▁▁",
			"████████████▁▁▁▁▁▁▁▁",
			"██████████████▁▁▁▁▁▁",
			"██████████████▁▁▁▁▁▁",
			"▁██████████████▁▁▁▁▁",
			"▁██████████████▁▁▁▁▁",
			"▁▁▁█████████████▁▁▁▁",
			"▁▁▁▁▁████████████▁▁▁",
			"▁▁▁▁▁████████████▁▁▁",
			"▁▁▁▁▁▁███████████▁▁▁",
			"▁▁▁▁▁▁▁▁█████████▁▁▁",
			"▁▁▁▁▁▁▁▁█████████▁▁▁",
			"▁▁▁▁▁▁▁▁▁█████████▁▁",
			"▁▁▁▁▁▁▁▁▁█████████▁▁",
			"▁▁▁▁▁▁▁▁▁▁█████████▁",
			"▁▁▁▁▁▁▁▁▁▁▁████████▁",
			"▁▁▁▁▁▁▁▁▁▁▁████████▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁███████▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁███████▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁███████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁███████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁",
			"▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁"
    ));
    interval = 17;
    return this;
  }

  public SpinnerCell watch() {
    animation = new LinkedList<>(List.of(
      "◡◡", "⊙⊙", "⊙⊙", "⊙⊙", "⊙⊙"
    ));
    interval = 150;
    return this;
  }

  public SpinnerCell earth() {
    animation = new LinkedList<>(List.of(
      "🌍",
			"🌎",
			"🌏"
    ));
    interval = 150;
    return this;
  }

  public SpinnerCell circleHalves() {
    animation = new LinkedList<>(List.of(
      "◐",
			"◓",
			"◑",
			"◒"
    ));
    interval = 50;
    return this;
  }

	private Runnable task;
	public SpinnerCell withTask(Runnable task) {
		this.task = task;
    return this;
  }

  private final AtomicInteger index = new AtomicInteger(0);
  private ScheduledExecutorService executor;
  public void run() {
		if (task == null) {
      throw new IllegalStateException("Cannot run Spinner without an assigned task.");
    }
		
		if (animation == null) dotsCircle();

    System.out.print(TUICursor.HIDE_CURSOR);
		Component.enableRawMode();

		final LinkedList<String> animate = animation; // to prevent changing the spinner
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(() -> {

			int currIndex = index.getAndUpdate(i -> (i + 1) % animation.size());
      TextTUI spin = new TextTUI(animate.get(currIndex)).setColor(spinnerColor);
      System.out.print("\r" + spin + " ".repeat(buffer) + message.toString());

    }, 0, interval, TimeUnit.MILLISECONDS);

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

  public void stop() {
		if (executor == null || executor.isShutdown()) return;

		if(!text.isEmpty()) {
			System.out.print("\r" + TUICursor.CLEAR_LINE);
			String[] textArr = text.toString().split("\n", -1);
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

  public void restart() {
    stop();
		index.set(0);
    run();
  }

  public int getHeight(){
    return height;
  }

  public String getId() {
    return "<Spinner>";
  }

	private void update(TextTUI prev, TextTUI curr) {
		int statusLength = Component.visibleLength(prev.toString());
		int msgLen = Component.visibleLength(curr.toString());
		if(statusLength > msgLen) {
			int space = statusLength - msgLen;
			curr.innerText(curr.getText() + " ".repeat(space));
		}
	}

	public SpinnerCell setBuffer(int buffer) {
    this.buffer = buffer;
    return this;
  }

	@Override
	public SpinnerCell setActiveBorder(TextTUI border) {
		// activeBorder = border;
		return this;
	}

	@Override
	public SpinnerCell setInactiveBorder(TextTUI border) {
		inactiveBorder = border;
		return this;
	}

	@Override
	public SpinnerCell setActiveIcon(TextTUI icon) {
		// activeIcon = icon;
		return this;
	}

	@Override
	public SpinnerCell setInactiveIcon(TextTUI icon) {
		inactiveIcon = icon;
		return this;
	}
}
