package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import inputForm.TUIComponent;
import inputForm.TUICursor;

public class Container {

    public Container() {
        OnClick.reset();
    }

    private record Entry(Supplier<String> renderer, boolean isText, Alignment alignment) {}
    private final CopyOnWriteArrayList<Entry> listeners  = new CopyOnWriteArrayList<>();
    private final HashMap<KeyHandle, TUIComponent> startingKeys = new HashMap<>();

    private final List<TUIComponent> focusables = new ArrayList<>();
    private int focusedIndex = -1;
    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    // Dirty flag — set by markDirty() or detected via size change
    private volatile boolean dirty        = true;
    // private          String[] prevFrame   = new String[0];
    private          Map<Integer, String> prevFrame = new HashMap<>();
    private final    int[]    prevSize    = {0, 0};


    public enum Alignment {
        TOP,            // row 0,              col 1
        BOTTOM,         // row termH-n,        col 1
        CENTER,         // row (termH-n)/2,    col centered
        CENTER_TOP,     // row 0,              col centered
        CENTER_BOTTOM,  // row termH-n,        col centered
        INLINE_CENTER   // same row but center
    }

    /**
     * Append components e.g., LogoTUI, ImageTUI, BarTUI, BadgeTUI etc.
     * @param supplier String
     */
    public void append(Supplier<String> supplier) {
        listeners.add(new Entry(supplier, false, null));
        markDirty();
    }

    /**
     * Append components (e.g., LogoTUI, ImageTUI etc.) But with Alignment position (e.g., TOP, BOTTOM, CENTER etc.).
     * @param supplier
     * @param alignment
     */
    public void append(Supplier<String> supplier, Alignment alignment) {
        listeners.add(new Entry(supplier, false, alignment));
        markDirty();
    }

    /**
     * Append normal TextTUI text without reference (If it changes, it won't update).
     * @param text
     */
    public void appendText(TextTUI text) {
        listeners.add(new Entry(text::toString, true, null));
        markDirty();
    }

    /**
     * Append components that inhernt from TUIComponent interface e.g., Input and SelectorTUI.
     * @param component TUIComponent
     */
    public void appendComponent(TUIComponent component) {
        listeners.add(new Entry(component::fire, false, null));

        if (component.isFocusable()) {
            KeyHandle startKey = component.getStartKey();
            boolean duplicate = startingKeys.keySet().stream()
                .anyMatch(k -> k.press().equals(startKey.press()));
            if (duplicate) {
                throw new IllegalArgumentException(
                    "Duplicate start key: " + startKey);
            }
            component.startActive(false);
            focusables.add(component);
            startingKeys.put(startKey, component);
        }

        markDirty();
    }

    // enable key handling by pressing Tab key
    private void focusComponent(int index) {
        if (index < 0 || index >= focusables.size()) return;
        if (focusedIndex >= 0 && focusedIndex != index) {
            focusables.get(focusedIndex).onBlur();
        }
        focusedIndex = index;
        TUIComponent comp = focusables.get(index);
        comp.onFocus();

        OnClick.add(comp.getStopKey(), () -> {
            comp.onBlur();
            OnClick.reset();
            containerKeyHandler();
            markDirty();
        });

        markDirty();
    }

    // next Component pressing Tab key
    private void focusNext() {
        if (focusables.isEmpty()) return;
        focusComponent((focusedIndex + 1) % focusables.size());
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
            Entry entry = listeners.get(i);
            Alignment alignment = entry.alignment();
            
            // Skip absolutely positioned components in Pass 1
            if (alignment != null && alignment != Alignment.INLINE_CENTER) continue;

            String[] lines = renderElement(entry.renderer().get(), entry.isText(), termCols);
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
        for (int i = 0; i < listeners.size(); i++) {
            Entry entry = listeners.get(i);
            Alignment alignment = entry.alignment();

            // Skip non-aligned or inline elements in Pass 2
            if (alignment == null || alignment == Alignment.INLINE_CENTER) continue;

            String[] lines     = renderElement(entry.renderer().get(), entry.isText(), termCols);
            int      lineCount = lines.length;

            int startRow = switch (alignment) {
                case BOTTOM, CENTER_BOTTOM -> termRows - lineCount;
                case CENTER                -> Math.max(0, (termRows - lineCount) / 2);
                default -> 0;
            };

            boolean centered = alignment == Alignment.CENTER
                            || alignment == Alignment.CENTER_TOP
                            || alignment == Alignment.CENTER_BOTTOM;

            for (int j = 0; j < lineCount; j++) {
                int targetRow = Math.max(0, Math.min(termRows - 1, startRow + j));
                String line = centered ? centerLine(lines[j], termCols) : lines[j];
                frame.put(targetRow, line);
            }
        }

        return frame;
    }

    /** Call this from any component whenever its state changes. */
    public void markDirty() { dirty = true; }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Abort the Container
     */
    public void stop() {
        System.out.print(TUICursor.SHOW_CURSOR);
        executor.shutdown();
    }

    /**
     * Run the Container
     */
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
    
        // New
        containerKeyHandler();
        // OnClick.add(KeyModifier.CTRL.with('c'), OnClick::cancel);
        try {
            OnClick.execute();   // blocks here — this is now the single input loop for the whole app
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    private void containerKeyHandler() {
        OnClick.add(KeyPress.Tab, this::focusNext);
        OnClick.add(KeyModifier.CTRL.with('c'), this::shutdownFromKey);

        for (Map.Entry<KeyHandle, TUIComponent> entry : startingKeys.entrySet()) {
            TUIComponent component = entry.getValue();
            int index = focusables.indexOf(component);
            OnClick.add(entry.getKey(), () -> focusComponent(index));
        }
    }

    private void shutdownFromKey() {
        stop();
        if (focusedIndex >= 0) {
            focusables.get(focusedIndex).onBlur();
        }
        OnClick.reset();
        OnClick.cancel();
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