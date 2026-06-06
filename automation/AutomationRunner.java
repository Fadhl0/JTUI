package automation;

import java.util.List;

import inputForm.TUICursor;
import utils.Component;
import utils.TextTUI;

class AutomationRunner {
    private static String title;
    private static int height;
    private static Cell c;

    protected static void run(List<Cell> cells, Automation instance) {

        try {
            for (Cell cell : cells) {
                if(Automation.isCancelled()) {
                    throw new Exception("--Cancelled");
                }

                cell.setActiveBorder(instance.getBorder()[0])
                    .setInactiveBorder(instance.getBorder()[1])
                    .setActiveIcon(instance.getIcon()[0])
                    .setInactiveIcon(instance.getIcon()[1])
                    .setBuffer(instance.getBuffer());
                
                c = cell;
                cell.run();   // blocking method
            }

        } catch (Exception e) {
            Automation.cancel();
            height = c.getHeight();
            title = c.getTitle();
            TextTUI err = "--Cancelled".equals(e.getMessage()) 
                          ? instance.getCancelMsg()
                          : instance.getErrorMsg();
            errorMsg(instance, title, err, height);
        } finally {
            System.out.print(TUICursor.SHOW_CURSOR);
            Component.disableRawMode();
        }
    }


    private static void errorMsg(Automation instance, String text, TextTUI err, int clear) {
        // clear last cell
        for (int i = 0; i < clear; i++){
            if(i != clear - 1) System.out.print(TUICursor.CURSOR_UP);
            System.out.print(TUICursor.CLEAR_LINE + "\r");
        }


        // error message
        TextTUI border = instance.getBorder()[1];
        TextTUI icon = instance.getErrorIcon();
        String space = " ".repeat(instance.getBuffer());

        TextCell title = new TextCell()
                             .setText(new TextTUI(text + "\n"))
                             .setInactiveBorder(new TextTUI(TUICursor.CLEAR_LINE + "\r").appendText(border))
                             .setInactiveIcon(icon)
                             .setBuffer(instance.getBuffer());

        System.out.print(title.textAutomation());

        System.out.println(
            new TextTUI("└").setColor(border.getColor())
            + space
            + err.toString()
        );
        System.out.print("\033[J"); // clear all text bottom

    }
}
