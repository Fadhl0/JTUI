package utils;

import java.util.ArrayList;
import java.util.List;

public class TableTUI {
    private List<TextTUI[]> rows = new ArrayList<>();
    private int buffer = 3;
    private int maxCol = 0;

    public TableTUI setBuffer(int buffer) {
      this.buffer = buffer;
      return this;
    }

    public TableTUI addRow(TextTUI... rowData) {
      rows.add(rowData);
      return this;
    }

    private int calcMaxCol() {
      rows.forEach(row -> {
        this.maxCol = Math.max(maxCol, row.length);
      });
      return maxCol;
    }

    // add empty values for missing columns to avoid errors.
    private void standrizeCol() {
      int len = calcMaxCol();
      List<TextTUI[]> newValue = new ArrayList<>();

      rows.forEach(row -> {
        TextTUI[] standardizedRow = new TextTUI[len];
        for (int i = 0; i < len; i++) {
          standardizedRow[i] = (i < row.length && row[i] != null) ? row[i] : new TextTUI();
        }
        newValue.add(standardizedRow);
      });
      this.rows = newValue;
    }

    // to add spaces
    private int[] calcMaxWidths() {
      standrizeCol();

      int len = maxCol;
      int[] widths = new int[len];
      
      for (int i = 0; i < len; i++) {
        int max = 0;
        for (TextTUI[] row : rows) {
          max = Math.max(max, Component.visibleLengthNoBreak(row[i].toString()));
        }
        widths[i] = max + this.buffer;
      }
      return widths;
    }

    private String refreashTUI() {
      int[] widths = calcMaxWidths();

      StringBuffer sb = new StringBuffer();

      // Data Rows
      for (TextTUI[] row : rows) {
        String[][] cellLines = new String[row.length][];
        int maxHeight = 0;

        //handle text contains "\n"
        for (int i = 0; i < row.length; i++) {
          cellLines[i] = row[i].toString().split("\n", -1);
          maxHeight = Math.max(maxHeight, cellLines[i].length);
        }
        
        for (int h = 0; h < maxHeight; h++) {
          for (int i = 0; i < row.length; i++) {
            String lineSegment = (h < cellLines[i].length) ? cellLines[i][h] : "";
            sb.append(spacing(lineSegment, widths[i]));
          }
          sb.append("\n");
        }
      }

      if (sb.length() > 0) {
        sb.deleteCharAt(sb.length() - 1); // remove last "\n"
      }
      return sb.toString();
    }

    private String spacing(String text, int length) {
      int visibleLen = Component.visibleLength(text);
      int pad = length - visibleLen;
      return text + " ".repeat(Math.max(0, pad));
    }

    public void replaceRow(int index, TextTUI[] update) {
      rows.set(index, update);
    }

    public void removeRow(int index) {
      rows.remove(index);
    }

    public int indexOf(TextTUI[] row) {
      return rows.indexOf(row);
    }

    public TextTUI[] getRow(int index) {
      return rows.get(index);
    }

    // public String build() {
    //   return refreashTUI();
    // }

    @Override
    public String toString() {
      return refreashTUI();
    }
}
