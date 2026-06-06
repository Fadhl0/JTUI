package utils;

/**
 * Return an icon.
 */
public enum Icons {
  PrintScreen("⎙"), 
  Network("🖧"),
  Printer("🖶"),
  FloppyDisk("🖫"),
  HardDisk("🖴"),

  BookOpened("🕮"),
  FolderSolid("🖿"),
  Folder("🗀"),
  FolderOpen("🗁"),
  DocumentEmpty("🗋"),
  Document("🗎"),
  PagesEmpty("🗍"),
  Page("🗏"),

  Erase("⌫"),
  OCR("⑆"),
  Minimize("🗕"),
  Maximize("🗖"),
  Hamburger("≡"),
  Bullet("∙"),

  StarSolid("★"),
  Star("☆"),
  StarSm("⋆"),
  Sparkle("✦"),
  SrarTriangle("🟀"),
  StarPinweel("✵"),
  SnowFlake("❆"),

  Tringle("▲"),
  TriangleUpSideDown("▼"),
  TriangleRightSmSolid("▸"),
  DescendingSolid("▾"),
  TriangleLeftSmSolid("◂"),
  Ascending("△"),
  TriangleRight("▷"),
  Descending("▽"),
  TriangleLeft("◁"),
  TriangleUpSm("▵"),
  TriangleRightSm("▹"),
  TriangleDownSm("▿"),
  TriangleLeftSm("◃"),
  TriangleDown("⛛"),
  TriangleRightSolid("▶"),
  TriangleUpper1("◤"),
  TriangleLower1("◢"),
  TriangleUpper2("◥"),
  TriangleLower2("◣"),

  DiamondSolid("◆"),
  DiamondInDiamond("◈"),
  DiamondX("❖"),
  Diamond("◇"),
  Lozenge("◊"),

  Hexagon("⬢"),
  HexagonHorizontal("⯃"),
  Octagon("⯄"),

  SquereSolid("◼"),
  SquereInSquere("▣"),
  Squere("◻"),
  SquereRounded("▢"),
  SquereCornerRounded("⛶"),
  SquereCornerDotted("⛚"),

  Bullseye("◉"),
  BullseyeAlt("◎"),
  CircleDotted("◌"),
  Circle("⭘"),
  CircleLg("⬤"),
  CircleSm("○"),
  CircleSolid("●"),
  Fisheye1("🞇"),
  Fisheye2("🞇"),
  OpticalDisc("🖸"),
  PieSlice("◔"),
  PieSliceAlt("◷"),

  SmilingFace("☻"),

  ButtonSolid("⚉"),
  Button("⚇"),

  Refresh("⟳"),
  RefreshAlt("↻"),
  RefreshReverse("↺"),

  ArrowLeft("←"),
  ArrowUp("↑"),
  ArrowRight("→"),
  ArrowDown("↓"),

  ArrowWaveLeft("↜"),
  ArrowWaveRight("↝"),
  ArrowWaveBoth("↭"),
  Back("↪"),
  Forword("↩"),

  ArrowCircledLeft("⮈"),
  ArrowCircledRigth("➲"),
  ArrowCircledUp("⮉"),
  ArrowCircledDown("⮋"),

  ArrowheadRight("⮞"),
  ArrowheadLeft("⮜"),
  ArrowheadUp("⮝"),
  ArrowheadDown("⮟"),
  ArrowheadRightBold("➤"),

  Next("❱"),
  Previous("❰"),
  NextLight("❯"),
  PreviousLight("❮"),

  ArrowRightAlt("➔"),
  ArrowRightRounded("➜"),

  ArrowCurvedUp("⤴"),
  ArrowCurvedLeft("⤶"),
  ArrowCurvedDown("⤵"),
  ArrowCurvedRight("⤷"),

  Tab("⭾"),
  ArrowVerticalwards1("⇅"),
  ArrowVerticalwards2("⮃"),
  BarredHarpoonArrow1("⥂"),
  BarredHarpoonArrow2("⥃"),
  BarredHarpoonArrow3("⥄"),
  ArrowHorizontalwards1("⇆"),
  ArrowHorizontalwards2("⇄"),

  Line("𝆹𝅥"),
  LineSolid("𝆺𝅥"),
  LineMid("⍿"),
  Partnership("⚯"),
  CurvePointer("☇"),
  Node("☊"),
  Scope("⯐"),
  App("𓃑"),
  Burger1("𝄘"),
  Burger2("𝄙"),

  CircledBoldX("⮿"),
  CircledX("⮾"),
  BallotX("✘"),
  Cross("⨯"),

  HeartTip("🎔"),

  Florette("✿"),

  SingleQuoteS("❛"),
  SingleQuoteE("❜"),
  DoubleQuoteS("❝"),
  DoubleQuoteE("❞"),

  ShadeLight("░"),
  Shade("▒"),
  ShadeDark("▓"),
  QuadrantUpLeft("▟"),
  QuadrantUpRight("▙"),
  QuadrantDownLeft("▜"),
  QuadrantDownRight("▛"),
  SlashSquere("▞"),
  BackSlashSquere("▚"),

  Search("⌕"),
  
  Stereo("📾"),
  Pennant("🏲"),

  SpeechBubbles("🗩"),
  TwoSpeechBubbles("🗪"),
  ThreeSpeechBubbles("🗫"),

  Check("✓"),
  Info("ⓘ"),

  Chart("🗠"),

  SpeakerHigh("🕪"),
  SpeakerMedium("🕩"),
  SpeakerLow("🕩"),

  Bullhorn("🕫"),
  BullhornWaves("🕬"),

  SunRays("☼"),
  Rain("⛆"),
  Fog("🌫"),

  Truck("⛟"),

  SquiggleLeftArrow("⇜"),
  SquiggleRightArrow("⇝"),

  DisabledCar("⛍"),
  CarSliding("⛐"),
  Traffic("⛗"),
  TrafficSolid("⛖"),

  Diameter("⌀"),
  Headstone("⛼"),
  HistoricSite("⛬"),
  Cup("⛾"),
  Skull("🕱"),
  Sharp("♯"),
  Option("⌘");

  private String icon;
  private Icons(String icon) {
    this.icon = icon;
  }

  public String toString() {
    return this.icon;
  }
  /**
   * - Important method when requesting an icon in string format
   */
  public String get() {
    return this.icon;
  }
}
