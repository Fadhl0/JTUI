package utils;
/*
S: Style
\u001B[S;38;2;R;G;Bm

22: Bold off
23: Italic off
24: Underline off
25: Blink off
29: Strikethrough off

256-Color Backgrounds:
\u001B[48;5;Indexm
Ex: SoftOrange text on a white background:
\u001B[38;5;208;48;5;15m

Ex: Bold + Underline + SoftOrange
\u001B[1;4;38;5;208m


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

    public int getStyle() { // sealed
        return this.style;
    }
}
