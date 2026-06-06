package utils;
// █ █
// This class created by AI (Sonnet 4.6) and I made modification to it.

import java.util.function.Supplier;

/**
 * BarTUI — A fluent progress/fill bar component for the TUI framework.
 *
 * <pre>
 * Usage:
 *   String bar = new BarTUI()
 *       .setWidth(10)
 *       .setPercentage(72.5)
 *       .setActiveColor(Colors.Gray600)
 *       .setInactiveColor(Colors.Black)
 *       .render();
 *
 *   // ▮▮▮▮▮▮▮▯▯▯ 72%
 *
 *   // Or from a value/max pair:
 *   new BarTUI().setValue(3, 10).setActiveColor(Colors.Cyan400).render();
 *
 * </pre>
 */
public class BarTUI {
    private String activeIcon = "▮";
    private String inactiveIcon = "▯";
    private String prepend = null;
    private String append = null;
    private String activeColor = "#657364";
    private String inactiveColor = "#222423";
    private int buffer = 0;

    public BarTUI() {}

    /**
     * - Active Icon is ▮
     * <br>
     * - Inactive Icon is ▯
     */
    public BarTUI defaultIcon() {
      activeIcon = "▮";
      inactiveIcon = "▯";
      return this;
    }

    /**
     * - Active Icon will be ▰
     * <br>
     * - Inactive Icon will be ▱
     */
    public BarTUI italicIcon() {
      activeIcon = "▰";
      inactiveIcon = "▱";
      return this;
    }

    public BarTUI prepend(TextTUI content) {
      prepend = content.toString().replaceAll("\n", "");
      return this;
    }
    public BarTUI append(TextTUI content) {
      append = content.toString().replaceAll("\n", "");
      return this;
    }
    public BarTUI setCustomIcons(String inactive, String active) {
      activeIcon = inactive.replaceAll("\n", "");
      inactiveIcon = active.replaceAll("\n", "");
      return this;
    }
    public BarTUI setActiveColor(Colors color) {
      activeColor = color.getColor();
      return this;
    }
    public BarTUI setActiveColor(String color) {
      activeColor = color;
      return this;
    }
    public BarTUI setInactiveColor(Colors color) {
      inactiveColor = color.getColor();
      return this;
    }
    public BarTUI setInactiveColor(String color) {
      inactiveColor = color;
      return this;
    }

    /**
     * @param number of space to add between each bar.
     */
    public BarTUI setBuffer(int number) {
      buffer = number;
      return this;
    }

    /** Total number of bar segments (columns). */
    private int    width           = 10;

    /** Fill level expressed as a percentage [0.0 – 100.0]. */
    private Supplier<Double> percentageSupplier = () -> 0.0;

    /** Whether to append the numeric percentage after the bar. */
    private boolean showPercentage = true;

    /**
     * Convenience factory — creates a bar pre-set to the given percentage.
     *
     * @param percentage fill level [0 – 100]
     */
    // public static BarTUI of(double percentage) {
    //     return new BarTUI().setPercentage(percentage);
    // }

    /**
     * Sets the total number of block characters in the bar.
     *
     * @param width ≥ 1
     */
    public BarTUI setWidth(int width) {
        if (width < 1) throw new IllegalArgumentException("Width must be ≥ 1, got: " + width);
        this.width = width;
        return this;
    }

    /**
     * Sets the fill level directly as a percentage.
     * Values are clamped to [0, 100].
     *
     * @param percentage 0.0 to 100.0
     */
    // public BarTUI setPercentage(Consumer<Double> percentage) {
    //     this.percentage = Math.max(0.0, Math.min(100.0, percentage));
    //     return this;
    // }
    public BarTUI setPercentage(Supplier<Double> supplier) {
      if (supplier != null) {
        this.percentageSupplier = supplier;
      }
      return this;
    }
    public BarTUI setPercentage(double staticPercentage) {
        this.percentageSupplier = () -> staticPercentage;
        return this;
    }

    /**
     * Sets the fill level from a value/max pair.
     * Equivalent to {@code setPercentage((value / max) * 100)}.
     *
     * @param value current value (e.g. 3)
     * @param max   maximum value (e.g. 10)
     */
    public BarTUI setValue(double value, double max) {
        if (max == 0) throw new IllegalArgumentException("max must not be 0");
        return setPercentage((value / max) * 100.0);
    }

    /**
     * Whether to append the numeric percentage (e.g. {@code  72%}) after the bar.
     * Default: {@code true}.
     */
    public BarTUI showPercentage(boolean show) {
        this.showPercentage = show;
        return this;
    }

    /**
     * Builds and returns the rendered bar string.
     *
     * <pre>
     * Example outputs:
     *   ▮▮▮▮▮▮▮▮▮▮ 100%
     *   ▮▮▮▮▮▯▯▯▯▯  50%
     *   ▯▯▯▯▯▯▯▯▯▯   0%
     * </pre>
     */
    public String render() {
      double percentage = Math.max(0.0, Math.min(100.0, percentageSupplier.get()));
      int filledBars = (int) Math.round((percentage / 100.0) * width);
      
      StringBuilder sb = new StringBuilder();
      if (prepend != null) sb.append(prepend);

      String space = " ".repeat(buffer);

      // Bar segments
      for (int i = 0; i < width; i++) {
        sb.append(
            i < filledBars
            ? new TextTUI(activeIcon).setColor(activeColor)
            : new TextTUI(inactiveIcon).setColor(inactiveColor));
        
        if(i != width - 1) sb.append(space);
      }

      if (append != null) sb.append(append);
      if (showPercentage) sb.append(String.format(" %3.0f%%", percentage));

      return sb.toString();
    }

    /** Delegates to {@link #render()} so the bar can be used directly in string contexts. */
    @Override
    public String toString() {
        return render();
    }
}
