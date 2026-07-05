package automation;

import utils.TextTUI;

public interface Cell {
  int getHeight();
  String getId();
  String getTitle();
  void run();

  Cell setActiveBorder(TextTUI border);
  Cell setInactiveBorder(TextTUI border);
  Cell setActiveIcon(TextTUI icon);
  Cell setInactiveIcon(TextTUI icon);
  Cell setBuffer(int buffer);
  Cell roundCorners(boolean isRound);
}
