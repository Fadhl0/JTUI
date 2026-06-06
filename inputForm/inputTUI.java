package inputForm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import utils.ANSI;
import utils.Colors;
import utils.Component;
import utils.TextTUI;

// @Deprecated
public class inputTUI {

    // Configuration
    private TextTUI   label;
    private TextTUI   placeholder;
    private int       visibleWidth;     // visible chars inside the box
    private int       maxLength;
    private InputType type;             // Type (Text, Password, Hidden)
    private TextTUI   icon;
    private Consumer<String> onSubmitCallback;
    private Supplier<TextTUI> prepend;
    private Supplier<TextTUI> append;
    private List<Runnable> liveListeners;

    // Runtime state
    private final StringBuilder buffer = new StringBuilder();
    private int cursor    = 0;   // logical cursor position in buffer
    private int scrollOff = 0;   // horizontal scroll offset for long input
    private boolean cancelled = false;
    private boxBorder box;
    private boxShape shape;

    private volatile TextTUI errorMessage;
    private boolean isInvalid = false;


    private Map<Predicate<String>, TextTUI> exceptions = new LinkedHashMap<>();

    // InputTUI
    public static class InputTUI {
        private TextTUI   label        = new TextTUI("");
        private TextTUI   placeholder  = new TextTUI(""); // Type something…
        private int       visibleWidth = 32;
        private int       maxLength    = 256;                   // TODO: -1 for unlimited charcters
        private InputType type         = InputType.Text;        // Type (Text, Password, Hidden)
        private boxShape  shape        = boxShape.SingleLine;   // to be removed
        private TextTUI   icon         = new TextTUI("");
        private String    borderColor  = "";
        private Supplier<TextTUI> append           = null;
        private Supplier<TextTUI> prepend          = null;
        private Consumer<String>  onSubmitCallback = null;

        // private Predicate<String> validator = text -> true; // Default allows everything
        private TextTUI errorMessage = new TextTUI("Invalid input.");
        private List<Runnable> liveListeners = new ArrayList<>();

        private Map<Predicate<String>, TextTUI> exceptions = new LinkedHashMap<>();
        
        // public InputTUI() {
        //     exceptions.put(validator, errorMessage);
        // }

        // public InputTUI validator(Predicate<String> validator, TextTUI errorMessage) {
        //     this.validator = validator;
        //     errorMessage.innerText(errorMessage.getText().replaceAll("\n", ""));
        //     this.errorMessage = errorMessage;
        //     return this;
        // }
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

        public InputTUI width(int width) { this.visibleWidth = width; return this; }
        public InputTUI setIcon(TextTUI icon) { this.icon = icon; return this; }
        public InputTUI maxLength(int max) { this.maxLength = max; return this; }
        public InputTUI type(InputType type) { this.type = type; return this; }
        public InputTUI shape(boxShape shape) { this.shape = shape; return this; }
        public InputTUI setBorderColor(String borderColor) { this.borderColor = borderColor; return this; }
        public InputTUI setBorderColor(Colors borderColor) { this.borderColor = borderColor.getColor(); return this; }
        public inputTUI build() {
            return new inputTUI(this);
        }
        public String toString() {
            return build().prompt();
        }
    }

    private inputTUI(InputTUI b) {
        this.label         = b.label;
        this.placeholder   = b.placeholder;
        this.visibleWidth  = b.visibleWidth;
        this.maxLength     = b.maxLength;
        this.type          = b.type;
        this.icon          = b.icon;
        onSubmitCallback   = b.onSubmitCallback;
        this.prepend       = b.prepend;
        this.append        = b.append;
        this.shape         = b.shape;
        this.errorMessage  = b.errorMessage;
        this.liveListeners = b.liveListeners;
        this.exceptions    = b.exceptions;
        box                = new boxBorder(b.shape, b.borderColor);
    }

    /**
     * Renders the input box and blocks until the user presses Enter or Escape.
     *
     * @return the entered text, or null if the user pressed Escape / Ctrl+C.
     */
    public String prompt(){
        OnClick.reset();
        
        System.out.print(TUICursor.HIDE_CURSOR);
        System.out.flush();

        printInitialLayout();
        OnClick.addLive(() -> { 
            try {
                Thread.sleep(16);
                render();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        });

        OnClick.add(KeyPress.Left_Arrow, () -> moveCursor(-1));
        OnClick.add(KeyPress.Right_Arrow, () -> moveCursor(+1));

        OnClick.add(KeyPress.Home, () -> {
            cursor = 0;
            adjustScroll();
        });
        OnClick.add(KeyPress.End, () -> {
            cursor = buffer.length();
            adjustScroll();
        });

        OnClick.add(KeyPress.Delete, () -> handleDelete());

        OnClick.add(KeyModifier.CTRL.with('c'), () -> {
            liveListeners.forEach((e) -> e.run());
            cancelled = true;
            OnClick.cancel();
        });

        OnClick.add(KeyPress.Enter, () -> {
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
                }
                OnClick.cancel();
            }
        });

        OnClick.add(KeyPress.Backspace, () -> handleBackspace());

        OnClick.TypingReturn((c) -> handleCharacter(c));
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

    private void handleCharacter(Character c) {
        isInvalid = false;
        if (buffer.length() >= maxLength) return;
        buffer.insert(cursor, c.charValue());
        cursor++;
        adjustScroll();
    }

    private void handleBackspace() {
        isInvalid = false;
        if (cursor > 0) {
            buffer.deleteCharAt(cursor - 1);
            cursor--;
            adjustScroll();
        }
    }

    private void handleDelete() {
        isInvalid = false;
        if (cursor < buffer.length()) {
            buffer.deleteCharAt(cursor);
            adjustScroll();
        }
    }

    private void moveCursor(int delta) {
        cursor = Math.max(0, Math.min(buffer.length(), cursor + delta));
        adjustScroll();
    }

    /** Keeps the cursor visible inside the visible window. */
    private void adjustScroll() {
        if (cursor < scrollOff) {
            scrollOff = cursor;
        } else if (cursor >= scrollOff + visibleWidth) {
            scrollOff = cursor - visibleWidth + 1;
        }
    }

    // private static final int WIDGET_HEIGHT = 3;
    private int linesPrintedLastFrame = 0;
    private static volatile boolean isWarned = false;

    private void printInitialLayout() {
        // for (int i = 0; i < WIDGET_HEIGHT; i++) System.out.println();
        linesPrintedLastFrame = 0;
    }

    /** Move cursor up WIDGET_HEIGHT lines so we can repaint in-place. */
    private void moveToTop() {
        System.out.print("\r");
        // for (int i = 0; i < WIDGET_HEIGHT; i++) System.out.print(TUICursor.CURSOR_UP);
        for (int i = 0; i < linesPrintedLastFrame; i++) {
            System.out.print(TUICursor.CURSOR_UP);
        }
    }
    

    private void render() {
        moveToTop();
        StringBuilder sb = new StringBuilder();

        int iconLen = Component.visibleLength(icon.toString());

        boolean none = shape.equals(boxShape.None);

        if (!label.isEmpty()) {
            sb.append(box.getTop(label.toString(), (visibleWidth + iconLen) - label.length()))
              .append("\n");
        }

        if(prepend != null) sb.append(prepend.get());
        
        // Left border
        sb.append(box.getSide().isEmpty() ? " " : box.getSide() + icon);

        String boxContent = buildBoxContent();   // visibleWidth chars, cursor-aware
        int    cursorCol  = cursor - scrollOff;  // visual cursor column inside the box

        // Render characters one-by-one, highlighting the cursor position
        for (int i = 0; i < boxContent.length(); i++) {
            char ch = boxContent.charAt(i);
            if (i == cursorCol && !cancelled) {
                TextTUI t1 = new TextTUI(String.valueOf(ch));
                t1.invert(); t1.underline();
                sb.append(t1);
            } else {
                if (buffer.length() == 0) {
                    sb.append(this.placeholder.getStyle() + this.placeholder.getText().substring(1) + ANSI.Reset);
                    break;
                }
                TextTUI t2 = new TextTUI(String.valueOf(ch));
                sb.append(t2);
            }
        }

        // Cursor at the very end
        if (cursorCol == boxContent.length() && !cancelled) {
            sb.append(
                new TextTUI("▏").setColor("#fbfbfb")
            );
            // Pad remaining space minus the cursor indicator
            sb.append(
                new TextTUI(" ".repeat(Math.max(0, visibleWidth - boxContent.length() - 1)))
            );
        } else {
            // Pad remaining empty space
            sb.append(
                new TextTUI(" ".repeat(Math.max(0, visibleWidth - boxContent.length())))
            );
        }

        if(!none) {
            sb.append(box.getSide());
            sb.append("\n" + box.getBottom(visibleWidth + iconLen))
              .append("\n");
        }

        if (append != null) {
            sb.append(append.get());
        }

        if (isInvalid) {
            isWarned = true;
            sb.append(errorMessage);

        } else {
            if(isWarned) {
                sb.append("\r\033[K");
                // int i = append.get().toString().split("\n", -1).length;
                // clearLines(i);
                // System.out.print(append.get());
                isWarned = false;
            }
        }

        String sbString = sb.toString();
        System.out.print(sbString);
        System.out.flush();

        linesPrintedLastFrame = (int) sbString.chars().filter(ch -> ch == '\n').count();
    }


    private String buildBoxContent() {
        if (buffer.isEmpty()) {
            return placeholder.getText().substring(0, Math.min(placeholder.length(), visibleWidth));
        }

        String text = type == InputType.Password
                ? "•".repeat(buffer.length())
                : type == InputType.Hidden
                ? ""
                : buffer.toString();

        int end = Math.min(scrollOff + visibleWidth, text.length());
        return text.substring(scrollOff, end);
    }

}