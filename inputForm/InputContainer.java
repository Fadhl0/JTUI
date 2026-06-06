package inputForm;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import utils.Colors;
import utils.Component;
import utils.Container;
import utils.TextTUI;
import java.util.function.Consumer;

public class InputContainer {

    private TextTUI   label;
    private TextTUI   placeholder;
    private int       maxVisibleWidth;
    private int       maxLength;
    private InputType type;
    private TextTUI   icon;

    private char start;
    private KeyHandle stop;

    private final StringBuilder buffer = new StringBuilder();
    private int     cursor    = 0;
    private int     scrollOff = 0;
    private boolean cancelled = false;
    private boolean active    = false;
    private boxBorder box;

    private Consumer<String> onChange;
    private Consumer<String> onSubmit;

    public static class Input {
        private TextTUI   label        = new TextTUI("");
        private TextTUI   placeholder  = new TextTUI("Press \"/\" to activate…");
        private int       visibleWidth = 32;
        private int       maxLength    = 256;
        private InputType type         = InputType.Text;
        private boxShape  shape        = boxShape.SingleLine;
        private String    borderColor  = "";
        private boolean   startActive  = true;
        private TextTUI   icon         = new TextTUI(" ❯ ");
        private char      start        = '/';
        private KeyHandle stop         = KeyPress.Escape;

        private Consumer<String> onChange = null;
        private Consumer<String> onSubmit = null;

        public Input label(TextTUI label) {
            label.innerText(label.getText().replaceAll("\n", ""));
            this.label = label;
            return this;
        }
        public Input placeholder(TextTUI placeholder) {
            placeholder.innerText(placeholder.getText().replaceAll("\n", ""));
            this.placeholder = placeholder;
            return this;
        }
        public Input width(int width) { 
            this.visibleWidth  = width;
            return this; 
        }
        public Input icon(TextTUI icon) {
            this.icon = icon;
            return this;
        }
        public Input startKey(char key) {
            this.start = key;
            return this;
        }
        public Input stopKey(KeyHandle key) {
            this.stop = key;
            return this;
        }
        public Input maxLength(int max)            { this.maxLength     = max;    return this; }
        public Input type(InputType type)          { this.type          = type;   return this; }
        public Input shape(boxShape shape)         { this.shape         = shape;  return this; }
        public Input setBorderColor(String color)  { this.borderColor   = color;  return this; }
        public Input setBorderColor(Colors color)  { this.borderColor   = color.getColor();  return this; }
        public Input startActive(boolean active)   { this.startActive   = active; return this; }

        public Input onChange(Consumer<String> cb) { this.onChange = cb; return this; }
        public Input onSubmit(Consumer<String> cb) { this.onSubmit = cb; return this; }

        public InputContainer build() { return new InputContainer(this); }
    }

    private InputContainer(Input b) {
        this.label           = b.label;
        this.placeholder     = b.placeholder;
        this.maxVisibleWidth = b.visibleWidth;
        this.maxLength       = b.maxLength;
        this.type            = b.type;
        this.onChange        = b.onChange;
        this.active          = b.startActive;
        this.box             = new boxBorder(b.shape, b.borderColor);
        this.icon            = b.icon;
        this.start           = b.start;
        this.stop            = b.stop;
        this.onSubmit        = b.onSubmit;
    }

    /**
     * Renders the InputContainer box and blocks until Enter or Ctrl+C.
     * "/" activates the InputContainer; ESC clears the buffer and deactivates.
     *
     * @return the entered text, or {@code null} if cancelled (Ctrl+C).
     */
    private void setupHandlers() {

        OnClick.add(KeyPress.Left_Arrow,  () -> { if (active) moveCursor(-1); });
        OnClick.add(KeyPress.Right_Arrow, () -> { if (active) moveCursor(+1); });
        OnClick.add(KeyPress.Home, () -> {
            if (active) { cursor = 0; adjustScroll(currentInnerWidth()); }
        });
        OnClick.add(KeyPress.End, () -> {
            if (active) { cursor = buffer.length(); adjustScroll(currentInnerWidth()); }
        });
        OnClick.add(KeyPress.Delete,    () -> { if (active) handleDelete(); });
        OnClick.add(KeyPress.Backspace, () -> { if (active) handleBackspace(); });

        OnClick.add(stop, () -> {
            if (active) {
                buffer.setLength(0);
                cursor    = 0;
                scrollOff = 0;
                active    = false;
                notifyChange();
            }
        });

        OnClick.add(KeyPress.Enter, () -> {
            if (active) {
                if (onSubmit != null) onSubmit.accept(buffer.toString());
                active = false;          // deactivate after submit
                onStateChange();         // re-render in inactive state
            }
        });

        OnClick.add(KeyModifier.CTRL.with('c'), () -> {
            cancelled = true;
            OnClick.cancel();
        });

        OnClick.TypingReturn((c) -> {
            if (!active) {
                if (c == start) {
                    active = true;
                    onStateChange();
                }
            } else {
                handleCharacter(c);
            }
        });
    }

    public String prompt() {
        StringBuilder sb = new StringBuilder();
        int innerWidth = currentInnerWidth();
        int iconLen = Component.visibleLength(icon.toString());
        int totalInner = iconLen + innerWidth;

        adjustScroll(innerWidth);

        int labelVisible = Component.visibleLength(label.toString());
        sb.append(box.getTop(label.toString(), totalInner - labelVisible)).append("\n");

        sb.append(box.getSide().isEmpty() ? " " : box.getSide());
        
        if (active) {
            sb.append(icon);
        } else {
            sb.append(new TextTUI(icon.getText()).setColor(placeholder.getColor()));
        }

        String boxContent = buildBoxContent(innerWidth);
        int cursorCol = cursor - scrollOff;

        if (buffer.isEmpty()) {
            if (active) {
                TextTUI cur = new TextTUI("▏").setColor("#fbfbfb");
                int lenCursor = Component.visibleLength(cur.toString());
                sb.append(cur);

                int phLen = Component.visibleLength(placeholder.toString());
                sb.append(placeholder);
                sb.append(" ".repeat(Math.max(0, innerWidth - phLen - lenCursor)));
            } else {
                int phLen = Component.visibleLength(placeholder.toString());
                sb.append(placeholder);
                sb.append(" ".repeat(Math.max(0, innerWidth - phLen)));
            }
        } else {
            for (int i = 0; i < boxContent.length(); i++) {
                char ch = boxContent.charAt(i);
                if (i == cursorCol && active && !cancelled) {
                    TextTUI t = new TextTUI(String.valueOf(ch))
                                    .invert()
                                    .underline();
                    sb.append(t);
                } else {
                    sb.append(ch);
                }
            }
            
            if (cursorCol == boxContent.length() && active && !cancelled) {
                sb.append(new TextTUI("▏").setColor("#fbfbfb"));
                sb.append(" ".repeat(Math.max(0, innerWidth - boxContent.length() - 1)));
            } else {
                sb.append(" ".repeat(Math.max(0, innerWidth - boxContent.length())));
            }
        }
        sb.append(box.getSide().isEmpty() ? " " : box.getSide()).append("\n");

        sb.append(box.getBottom(totalInner));

        return sb.toString();
    }

    public String listen() {
        try {
            OnClick.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cancelled ? null : buffer.toString();
    }

    private void handleCharacter(Character c) {
        if (buffer.length() >= maxLength) return;
        buffer.insert(cursor, c.charValue());
        cursor++;
        adjustScroll(currentInnerWidth());
        notifyChange();
    }

    private void handleBackspace() {
        if (cursor > 0) {
            buffer.deleteCharAt(cursor - 1);
            cursor--;
            adjustScroll(currentInnerWidth());
            notifyChange();
        }
    }

    private void handleDelete() {
        if (cursor < buffer.length()) {
            buffer.deleteCharAt(cursor);
            adjustScroll(currentInnerWidth());
            notifyChange();
        }
    }

    private void moveCursor(int delta) {
        cursor = Math.max(0, Math.min(buffer.length(), cursor + delta));
        adjustScroll(currentInnerWidth());
        onStateChange();
    }

    private void notifyChange() {
        if (onChange != null) onChange.accept(buffer.toString());
        onStateChange();
    }

    // Scroll

    /**
     * Computes the actual inner text-area width for the current terminal size.
     * Clamps {@link #maxVisibleWidth} so the full widget always fits in one line.
     */
    private int currentInnerWidth() {
        int iconLen   = Component.visibleLength(icon.toString());
        // terminal width − 2 borders − icon
        int termWidth = Component.getTerminalSize()[1];
        int available = termWidth - 2 - iconLen;

        if(maxVisibleWidth == -1) return available;
        return Math.min(maxVisibleWidth, Math.max(4, available));
    }

    /** Keeps the cursor visible inside the given content window. */
    private void adjustScroll(int innerWidth) {
        if (cursor < scrollOff) {
            scrollOff = cursor;
        } else if (cursor >= scrollOff + innerWidth) {
            scrollOff = cursor - innerWidth + 1;
        }
    }

    /**
     * Returns the visible slice of the buffer (or masked text) that fits
     * within the given {@code width}, starting at {@link #scrollOff}.
     */
    private String buildBoxContent(int width) {
        if (buffer.isEmpty()) return "";

        String text = switch (type) {
            case Password -> "•".repeat(buffer.length());
            case Hidden   -> "";
            default       -> buffer.toString();
        };

        int end = Math.min(scrollOff + width, text.length());
        return text.substring(scrollOff, end);
    }

    // Hook
    private Container container;
    public InputContainer setContainer(Container c) {
        setupHandlers();
        this.container = c;
        return this;
    }
    private void onStateChange() {
        if (container != null) container.markDirty();
    }
}