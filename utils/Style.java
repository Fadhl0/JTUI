package utils;
/*
Code, Effect
0, Regular
1, Bold
3, Italic (Not supported by all terminals)
4, Underline
7, Invert (Swaps text and background color)
8, Hidden
9, Strike

*/

public enum Style {
    Regular(0),
    Bold(1),
    Italic(3),
    Underline(4),
    Invert(7),
    Hidden(8),
    Strikethrough(9);

    private final int style;

    private Style (int style) {
        this.style = style;
    }

    public int getStyle() {
        return this.style;
    }
}
