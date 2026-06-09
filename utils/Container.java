package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import inputForm.TUICursor;

public class Container {
    private final CopyOnWriteArrayList<Supplier<String>> listeners = new CopyOnWriteArrayList<>();
    private final ArrayList<Boolean>                     newLine   = new ArrayList<>();
    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    // Dirty flag — set by markDirty() or detected via size change
    private volatile boolean dirty        = true;
    // private          String[] prevFrame   = new String[0];
    private          Map<Integer, String> prevFrame = new HashMap<>();
    private final    int[]    prevSize    = {0, 0};

    // ── Registration ────────────────────────────────────────────────────────

    public void append(Supplier<String> supplier) {
        listeners.add(supplier);
        newLine.add(false);
        markDirty();
    }


    public enum Alignment {
        TOP,            // row 0,              col 1
        BOTTOM,         // row termH-n,        col 1
        CENTER,         // row (termH-n)/2,    col centered
        CENTER_TOP,     // row 0,              col centered
        CENTER_BOTTOM,  // row termH-n,        col centered
        INLINE_CENTER   // same row but center
    }

    private final Map<Integer, Alignment> alignMap = new LinkedHashMap<>();
    public void append(Supplier<String> supplier, Alignment alignment) {
        int idx = listeners.size();
        listeners.add(supplier);
        newLine.add(false);
        alignMap.put(idx, alignment);
        markDirty();
    }
    private String centerLine(String line, int termCols) {
        int visibleLen = ANSI_PATTERN.matcher(line).replaceAll("").length();
        int pad = Math.max(0, (termCols - visibleLen) / 2);
        return " ".repeat(pad) + line;
    }
    private Map<Integer, String> buildFrame(int[] size) {
        Map<Integer, String> frame = new LinkedHashMap<>();
        int termRows = size[0];
        int termCols = size[1];
        int currentRow = 0;

        // Pass 1 — sequential elements fill top-down
        for (int i = 0; i < listeners.size(); i++) {
            Alignment alignment = alignMap.get(i);
            if (alignment != null && alignment != Alignment.INLINE_CENTER) continue;

            String[] lines = renderElement(listeners.get(i).get(), newLine.get(i), termCols);
            for (String line : lines) {
                if (currentRow < termRows) {
                    frame.put(currentRow++,
                        alignment == Alignment.INLINE_CENTER
                            ? centerLine(line, termCols)
                            : line
                    );
                }
            }
        }

        // Pass 2 — aligned elements placed at computed absolute rows
        for (Map.Entry<Integer, Alignment> entry : alignMap.entrySet()) {
            if (entry.getValue() == Alignment.INLINE_CENTER) continue;

            int       idx       = entry.getKey();
            Alignment alignment = entry.getValue();

            String[] lines     = renderElement(listeners.get(idx).get(), newLine.get(idx), termCols);
            int      lineCount = lines.length;

            int startRow = switch (alignment) {
                case BOTTOM, CENTER_BOTTOM -> termRows - lineCount;
                case CENTER                -> Math.max(0, (termRows - lineCount) / 2);
                // case TOP, CENTER_TOP       -> 0;
                default -> 0;
            };

            boolean centered = alignment == Alignment.CENTER
                            || alignment == Alignment.CENTER_TOP
                            || alignment == Alignment.CENTER_BOTTOM;

            for (int i = 0; i < lineCount; i++) {
                int targetRow = Math.max(0, Math.min(termRows - 1, startRow + i));
                String line = centered ? centerLine(lines[i], termCols) : lines[i];
                frame.put(targetRow, line);
            }
        }

        return frame;
    }

    public void appendText(TextTUI text) {
        listeners.add(text::toString);
        newLine.add(true);
        markDirty();
    }

    /** Call this from any component whenever its state changes. */
    public void markDirty() { dirty = true; }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    public void stop() {
        System.out.print(TUICursor.SHOW_CURSOR);
        executor.shutdown();
    }

    public void execute() {
        WindowsAPI.apply();
        System.out.print(TUICursor.HIDE_CURSOR);
        // Component.enableRawMode();
        System.out.flush();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Component.clear();
            System.out.print(TUICursor.SHOW_CURSOR);
            Component.disableRawMode();
            System.out.flush();
        }));

        executor.scheduleAtFixedRate(() -> {
            int[] size = Component.getTerminalSize();

            boolean sizeChanged = prevSize[0] != size[0] || prevSize[1] != size[1];

            if (sizeChanged) {
                Component.clear();
                // prevFrame = new String[0];
                prevFrame = new HashMap<>();
                prevSize[0] = size[0];
                prevSize[1] = size[1];
                dirty = true;
            }

            // if (!dirty) return;
            // dirty = false;

            Map<Integer, String> frame = buildFrame(size);

            // ── Diff ──────────────────────────────────────────────────────
            StringBuilder out = new StringBuilder();

            // Union of rows that existed before OR exist now
            Set<Integer> allRows = new TreeSet<>();
            allRows.addAll(frame.keySet());
            allRows.addAll(prevFrame.keySet());

            for (int row : allRows) {
                String next = frame.getOrDefault(row, "");
                String prev = prevFrame.getOrDefault(row, "");
                if (!next.equals(prev)) {
                    out.append("\033[").append(row + 1).append(";1H")
                       .append("\033[2K")
                       .append(next);
                }
            }

            // Keep cursor hidden after every render pass
            out.append(TUICursor.HIDE_CURSOR);
            System.out.print(out);
            System.out.flush();

            prevFrame = frame;

        }, 0, 16, TimeUnit.MILLISECONDS); // ~60 fps, renders only when dirty
    }

    // ── Rendering helper ─────────────────────────────────────────────────────

    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    /**
     * Converts one element's raw string into screen lines,
     * truncating visible width to termWidth.
     */
    private String[] renderElement(String raw, boolean isText, int termWidth) {
        if (isText) {
            // TextTUI elements are already formatted; just split on newlines
            return (raw + "\n").split("\n", -1);
        }

        String[] parts     = raw.split("\n", -1);
        String   stripped  = ANSI_PATTERN.matcher(raw).replaceAll("");
        String[] plainParts = stripped.split("\n", -1);

        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (plainParts[i].length() > termWidth) {
                result[i] = Component.visibleSubstring(parts[i], 0, termWidth) + ANSI.Reset;
            } else {
                result[i] = parts[i];
            }
        }
        return result;
    }
}