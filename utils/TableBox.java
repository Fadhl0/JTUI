package utils;

import java.util.Arrays;
import java.util.stream.IntStream;

import inputForm.boxBorder.BoxTUI;

public class TableBox {
  private TableTUI table;
  private BoxTUI[] rowData; // to apply update while watch
  private int dataLength = 0;

  public TableBox(BoxTUI... rowData) {
    dataLength = rowData.length;
    if(dataLength == 0) return;

    this.rowData = rowData;
  }

  // public TableBox addBox(TUIBox box) {
  //  
  //  return this;
  // }

  private int[] getlength() {
    int[] allEle  = new int[dataLength];

    for (int i=0; i<dataLength; i++) {
      allEle[i] = Component.visibleLengthNoBreak(rowData[i].build());
    }
    
    return allEle;
  }

  private int[] distribute() {
    int[] values = getlength();
    int terminalW = Component.getTerminalSize()[1];
    int currentSum = Arrays.stream(values).sum();

    if (currentSum == 0) return new int[dataLength];
    
    // FIX: If it's equal OR less than terminal width, keep original sizes
    if (currentSum <= terminalW) return values.clone(); 

    double scale = (double) terminalW / currentSum;

    int[]    result     = new int[dataLength];
    double[] remainders = new double[dataLength];
    int      allocated  = 0;

    // Step 1: floor each scaled value, track remainders
    for (int i = 0; i < dataLength; i++) {
      double exact = values[i] * scale;
      result[i]     = (int) exact;
      remainders[i] = exact - result[i];
      allocated    += result[i];
    }

    // Step 2: distribute leftover units to largest-remainder slots
    int leftover = terminalW - allocated;
    Integer[] indices = IntStream.range(0, dataLength).boxed().toArray(Integer[]::new);
    Arrays.sort(indices, (a, b) -> Double.compare(remainders[b], remainders[a]));

    for (int i = 0; i < leftover; i++) {
      result[indices[i]]++;
    }

    return result;
  }

  private TextTUI[] converter() {
    int[] elementSize = distribute();
    TextTUI[] arr = new TextTUI[dataLength];

    for (int i = 0; i < dataLength; i++) {
      StringBuilder sb = new StringBuilder();

      String text = rowData[i].build();
      String[] split = text.split("\n");
      int index = elementSize[i];

      for (int j = 0; j < split.length; j++) {
        String line = split[j];
        int lineLength = Component.visibleLength(line);

        String sub1 = Component.visibleSubstring(line, 0, index - 1);
        String sub2 = Component.visibleSubstring(line, lineLength - 1, lineLength);

        sb.append(sub1)
          .append(sub2)
          .append(ANSI.Reset)
          .append("\n");
      }
      
      arr[i] = new TextTUI(sb.toString());
    }
    
    return arr;
  }

  public String build() {
    table = new TableTUI();
    TextTUI[] arr = converter();
    table.addRow(arr);
    return table.setBuffer(0).toString();
  }
  public String toString() {
    return build();
  }
}
