package inputForm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import Keyhandle.KeyHandle;
import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import utils.Colors;
import utils.Component;
import utils.TextTUI;

public class Input implements TUIComponent {

    // Configuration
    private TextTUI   label;
    private TextTUI   placeholder;
    private int       maxVisibleWidth;
    private int       maxLength;
    private InputType type;             // Type (Text, Password, Hidden)
    private TextTUI   icon;
    private Consumer<String> onSubmitCallback;
    private Consumer<String> onChangeCallback;
    private Supplier<TextTUI> prepend;
    private Supplier<TextTUI> append;
    private List<Runnable> liveListeners;

    // Runtime state
    private final StringBuilder buffer = new StringBuilder();
    private int cursor    = 0;   // logical cursor position in buffer
    private int scrollOff = 0;   // horizontal scroll offset for long input
    private boolean cancelled = false;
    private boxBorder activeBox;
    private boxBorder inactiveBox;
    private boxShape shape;
    private boolean disableActivation;
    
    private volatile TextTUI errorMessage;
    private boolean isInvalid = false;
    
    private KeyHandle start;
    private KeyHandle stop;
    private volatile boolean active = true;

    private Map<Predicate<String>, TextTUI> exceptions = new LinkedHashMap<>();

    // InputTUI
    public static class InputTUI {
        private TextTUI   label        = new TextTUI("");
        private TextTUI   placeholder  = new TextTUI(""); // Type something…
        private int       visibleWidth = 32;
        private int       maxLength    = 256;                   // -1 for unlimited charcters
        private InputType type         = InputType.Text;        // Type (Text, Password, Hidden)
        private boxShape  shape        = boxShape.SingleLine;
        private TextTUI   icon         = new TextTUI("");
        private String    activeBorderColor  = "";
        private String    inactiveBorderColor  = "";
        private Supplier<TextTUI> append           = null;
        private Supplier<TextTUI> prepend          = null;
        private Consumer<String>  onSubmitCallback = null;
        private Consumer<String>  onChangeCallback = null;

        private KeyHandle start      = KeyPress.Slash;
        private KeyHandle stop       = KeyPress.Escape;

        private TextTUI errorMessage = new TextTUI("Invalid input.");
        private List<Runnable> liveListeners = new ArrayList<>();
        private boolean disableActivation = false;

        private Map<Predicate<String>, TextTUI> exceptions = new LinkedHashMap<>();
        
        public InputTUI validator(Predicate<String> validator, TextTUI errorMessage) {
            errorMessage.innerText(errorMessage.getText().replaceAll("\n", ""));
            exceptions.put(validator, errorMessage);
            return this;
        }
        public InputTUI label(TextTUI label) {
            label.innerText(label.getText().replaceAll("\n", ""));
            this.label = label;
            return this; 
        }
        public InputTUI placeholder(TextTUI placeholder) {
            placeholder.innerText(placeholder.getText().replaceAll("\n", ""));
            this.placeholder = placeholder;
            return this;
        }
        public InputTUI onSubmit(Consumer<String> callback) {
            onSubmitCallback = callback;
            return this;
        }
        public InputTUI onChange(Consumer<String> cb) { this.onChangeCallback = cb; return this; }
        public InputTUI setPrependText(Supplier<TextTUI> text) {
            prepend = text;
            return this;
        }
        public InputTUI setAppendText(Supplier<TextTUI> text) {
            append = text;
            return this;
        }
        public InputTUI handleCancellation(Runnable runnable) {
            liveListeners.add(runnable);
            return this;
        }

        public TextTUI getPlaceholder() {
            return placeholder;
        }
        public InputTUI startKey(KeyHandle key) {
            this.start = key;
            return this;
        }
        public InputTUI stopKey(KeyHandle key) {
            this.stop = key;
            return this;
        }

        public InputTUI width(int width) { this.visibleWidth = width; return this; }
        public InputTUI setIcon(TextTUI icon) { this.icon = icon; return this; }
        public InputTUI maxLength(int max) { this.maxLength = max; return this; }
        public InputTUI type(InputType type) { this.type = type; return this; }
        public InputTUI shape(boxShape shape) { this.shape = shape; return this; }
        public InputTUI setActiveBorderColor(String borderColor) { this.activeBorderColor = borderColor; return this; }
        public InputTUI setActiveBorderColor(Colors borderColor) { this.activeBorderColor = borderColor.getColor(); return this; }
        public InputTUI setInactiveBorderColor(String borderColor) { this.inactiveBorderColor = borderColor; return this; }
        public InputTUI setInactiveBorderColor(Colors borderColor) { this.inactiveBorderColor = borderColor.getColor(); return this; }
        /**
         * Turn on/off stopping key
         * @param status
         */
        public InputTUI turnOffSwitch(boolean status) {
            disableActivation = status;
            return this;
        }
        public Input build() {
            return new Input(this);
        }
        public String toString() {
            return build().execute();
        }
    }

    private Input(InputTUI b) {
        label            = b.label;
        placeholder      = b.placeholder;
        // visibleWidth     = b.visibleWidth;
        maxLength        = b.maxLength;
        type             = b.type;
        icon             = b.icon;
        onSubmitCallback = b.onSubmitCallback;
        onChangeCallback = b.onChangeCallback;
        prepend          = b.prepend;
        append           = b.append;
        shape            = b.shape;
        errorMessage     = b.errorMessage;
        liveListeners    = b.liveListeners;
        exceptions       = b.exceptions;
        maxVisibleWidth  = b.visibleWidth;
        activeBox        = new boxBorder(b.shape, b.activeBorderColor);
        inactiveBox      = new boxBorder(b.shape, b.inactiveBorderColor);
        stop             = b.stop;
        start            = b.start;
        disableActivation= b.disableActivation;
    }

    // private static final int WIDGET_HEIGHT = 3;
    private int linesPrintedLastFrame = 0;
    private static volatile boolean isWarned = false;

    public String execute(){
        OnClick.reset();
        
        System.out.print(TUICursor.HIDE_CURSOR);
        System.out.flush();

        setupHandlers();
        printInitialLayout();
        OnClick.addLive(() -> { 
            try {
                Thread.sleep(16);
                System.out.print(render());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        });

        try {
            OnClick.execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.print(TUICursor.SHOW_CURSOR);
        System.out.flush();

        return cancelled ? null : buffer.toString();
    }

    public void clearInput() {
        buffer.setLength(0);
        cursor = 0;
        scrollOff = 0;
    }

    private void printInitialLayout() {
        // for (int i = 0; i < WIDGET_HEIGHT; i++) System.out.println();
        linesPrintedLastFrame = 0;
    }

    /** Move cursor up WIDGET_HEIGHT lines so we can repaint in-place. */
    private void moveToTop() {
        System.out.print("\r");
        for (int i = 0; i < linesPrintedLastFrame; i++) {
            System.out.print(TUICursor.CURSOR_UP);
        }
    }
    
    // used for normal use (not Container)
    private String render() {
        moveToTop();
        String input = renderNative();
        linesPrintedLastFrame = (int) input.chars().filter(ch -> ch == '\n').count();
        return input;
    }

    // for general use
    public String renderNative() {
        StringBuilder sb = new StringBuilder();
        int innerWidth = currentInnerWidth();
        int iconLen = Component.visibleLength(icon.toString());
        int totalInner = iconLen + innerWidth;

        adjustScroll(innerWidth);

        boolean none = shape.equals(boxShape.None);
        boxBorder box = active ? activeBox : inactiveBox;

        if (!label.isEmpty()) {
            int labelVisible = Component.visibleLength(label.toString());
            sb.append(box.getTop(label.toString(), totalInner - labelVisible)).append("\n");
        } else {
            sb.append(box.getTop(label.toString(), totalInner)).append("\n");
        }

        if (prepend != null) sb.append(prepend.get());

        sb.append(box.getSide().isEmpty() ? " " : box.getSide());

        if (active) {
            sb.append(icon);
        } else {
            sb.append(new TextTUI(icon.getText()).setColor(placeholder.getColor()));
        }

        String boxContent = buildBoxContent(innerWidth);
        int cursorCol = cursor - scrollOff;

        if (buffer.isEmpty()) {
            int phLen = placeholder.length();
            if (active) {
                String phText = placeholder.getText();
                
                if (!phText.isEmpty()) {
                    TextTUI firstChar = new TextTUI(phText.substring(0, 1)).invert().underline();
                    TextTUI remaining = new TextTUI(phText.substring(1)).setColor(placeholder.getColor());
                    
                    sb.append(firstChar).append(remaining);
                    sb.append(" ".repeat(Math.max(0, innerWidth - phLen)));
                } else {
                    sb.append(new TextTUI(" ").invert().underline());
                    sb.append(" ".repeat(Math.max(0, innerWidth - 1)));
                }
            } else {
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
                sb.append(new TextTUI(" ").invert().underline());
                sb.append(" ".repeat(Math.max(0, innerWidth - boxContent.length() - 1)));
            } else {
                sb.append(" ".repeat(Math.max(0, innerWidth - boxContent.length())));
            }
        }

        if (!none) {
            sb.append(box.getSide().isEmpty() ? " " : box.getSide());
            sb.append("\n").append(box.getBottom(totalInner)).append("\n");
        }

        if (append != null) {
            sb.append(append.get());
        }

        if (isInvalid) {
            isWarned = true;
            sb.append(errorMessage);
        } else {
            if (isWarned) {
                sb.append("\r\033[K");
                isWarned = false;
            }
        }

        return sb.toString();
    }
    
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
            if (active && !disableActivation) {
                buffer.setLength(0);
                cursor    = 0;
                scrollOff = 0;
                active    = false;
                notifyChange();
            }
        });

        OnClick.add(start, () -> {
            if (!active) {
                active = true;
                // onStateChange();
            }
        });

        OnClick.add(KeyPress.Enter, () -> {
            if (active) {
                String currentText = buffer.toString();
                
                isInvalid = false;
                errorMessage = null;
                for (Map.Entry<Predicate<String>, TextTUI> entry : exceptions.entrySet()) {
                    try {
                        if (!entry.getKey().test(currentText)) {
                            isInvalid = true;
                            errorMessage = entry.getValue();
                            break;
                        }
                    } catch (Exception e) {
                        OnClick.cancel();
                        // throw new RuntimeException("Validation rule failed unexpectedly");
                    }
                }
                if (!isInvalid) {
                    if (onSubmitCallback != null) {
                        onSubmitCallback.accept(currentText);
                        active = false;          // deactivate after submit
                        clearInput();
                        // onStateChange();         // re-render in inactive state
                    }
                    if(!isContainer) OnClick.cancel();
                }
            }
        });


        OnClick.add(KeyModifier.CTRL.with('c'), () -> {
            if(!isContainer) {
                liveListeners.forEach((e) -> e.run());
                cancelled = true;
                OnClick.cancel();
            }
        });

        OnClick.TypingReturn((c) -> {
            if(active) handleCharacter(c);
        });
    }

    private void handleCharacter(Character c) {
        isInvalid = false;
        if (buffer.length() >= maxLength) return;
        buffer.insert(cursor, c.charValue());
        cursor++;
        adjustScroll(currentInnerWidth());
        notifyChange();
    }

    private void handleBackspace() {
        isInvalid = false;
        if (cursor > 0) {
            buffer.deleteCharAt(cursor - 1);
            cursor--;
            adjustScroll(currentInnerWidth());
            notifyChange();
        }
    }

    private void handleDelete() {
        isInvalid = false;
        if (cursor < buffer.length()) {
            buffer.deleteCharAt(cursor);
            adjustScroll(currentInnerWidth());
            notifyChange();
        }
    }

    private void moveCursor(int delta) {
        cursor = Math.max(0, Math.min(buffer.length(), cursor + delta));
        adjustScroll(currentInnerWidth());
    }

    // onChangeCallback
    private void notifyChange() {
        if (onChangeCallback != null) onChangeCallback.accept(buffer.toString());
    }

    // TUIComponent methods
    private boolean isContainer = false;

    /**
     * <ul>
     * <li>Used Only in Container Class</li>
     * <li>Do not use it; it will be called by default in the Container.</li>
     * </ul>
    */
    @Override public void onFocus() { setupHandlers(); active = true; }
    /**
     * <ul>
     * <li>Used Only in Container Class</li>
     * <li>Do not use it; it will be called by default in the Container.</li>
     * </ul>
    */
    @Override public void onBlur()  { active = false; }
    @Override public boolean isFocusable() { return true; }

    /**
     * <ul>
     * <li>Used Only in Container Class</li>
     * <li>Do not use it; it will be called by default in the Container.</li>
     * </ul>
    */
    @Override
    public String fire() {
        isContainer = true;
        return renderNative();
    }
    @Override
    public KeyHandle getStartKey() {
        return start;
    }
    @Override
    public KeyHandle getStopKey() {
        return stop;
    }
    @Override
    public Input startActive(boolean startActive) { 
        active = startActive;
        return this; 
    }

}
