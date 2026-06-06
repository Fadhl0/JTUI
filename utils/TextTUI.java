package utils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

final public class TextTUI {
    private StringBuffer text = new StringBuffer();
    private String color = "";
    private String backgroundColor = "";

    public TextTUI() {}
    public TextTUI(String text) {
        this.text.append(text);
    }
    private String text(boolean reset) {
        return ANSIformat.format(text.toString(), this.color, getStyleList(), this.backgroundColor, reset);
    }

    public String getText() {
        return this.text.toString();
    }
    public String getStyle() {
        return ANSIformat.format("", this.color, getStyleList(), this.backgroundColor, false);
    }

    public TextTUI appendText(TextTUI text) {
        this.text.append(text.toStringWithoutReset());
        return this;
    }

    public TextTUI setColor(String hexadesimal) {
        this.color = Colors.setColor(hexadesimal);
        return this;
    }
    public TextTUI setColor(Colors hexadesimal) {
        this.color = Colors.setColor(hexadesimal.toString());
        return this;
    }

    public TextTUI setBackgroundColor(String hexadesimal) {
        this.backgroundColor = Colors.setColor(hexadesimal);
        return this;
    }
    public TextTUI setBackgroundColor(Colors hexadesimal) {
        this.backgroundColor = Colors.setColor(hexadesimal.toString());
        return this;
    }

    public void innerText(String text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    @Override
    public String toString() {
        return text(true);
    }

    public String toStringWithoutReset() {
        return text(false);
    }

    public int length() {
        return Component.visibleLength(text.toString());
    }

    /**
     * This method responsible to find whether the text contains another TextTUI varible that cotains styled.
     */
    public boolean isStyled() {
        return Pattern.compile(Component.ANSI_PATTERN).matcher(this.text.toString()).find();
    }

    public boolean isEmpty() {
        return Component.visibleLength(text.toString()) == 0;
    }

    public String getColor() {
        return this.color;
    }
    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    // Styling

    private List<Integer> styleList = new ArrayList<>();
    
    public void clearStyles() {
        this.styleList.clear();
    }

    public TextTUI regular() {
        this.styleList.add(Style.Regular.getStyle());
        return this;
    }
    public TextTUI bold() {
        this.styleList.add(Style.Bold.getStyle());
        return this;
    }
    public TextTUI italic() {
        this.styleList.add(Style.Italic.getStyle());
        return this;
    }
    public TextTUI underline() {
        this.styleList.add(Style.Underline.getStyle());
        return this;
    }
    public TextTUI invert() {
        this.styleList.add(Style.Invert.getStyle());
        return this;
    }
    public TextTUI hidden() {
        this.styleList.add(Style.Hidden.getStyle());
        return this;
    }
    public TextTUI strikethrough() {
        this.styleList.add(Style.Strikethrough.getStyle());
        return this;
    }

    private List<Integer> getStyleList() {
        return new ArrayList<>(this.styleList.stream().distinct().sorted().toList());
    }
}
