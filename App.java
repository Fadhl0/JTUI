import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import Keyhandle.KeyModifier;
import Keyhandle.KeyPress;
import Keyhandle.OnClick;
import Text.ImageTUI;
import Text.LogoTUI;
import Text.LogoTUI.TUIFont;
import automation.Automation;
import automation.InputCell;
import automation.MultiSelectorCell;
import automation.ScannerCell;
import automation.SelectorCell;
import automation.SpinnerCell;
import automation.TextCell;
import inputForm.SelectorTUI;
import inputForm.boxShape;
import inputForm.InputContainer;
import inputForm.InputContainer.Input;
import inputForm.boxBorder.BoxTUI;
import inputForm.inputTUI.InputTUI;
import utils.AlignText;
import utils.BadgeTUI;
import utils.BarTUI;
import utils.Colors;
import utils.Component;
import utils.Container;
import utils.Gradient;
import utils.Icons;
import utils.QuoteTUI;
import utils.SmartTextTUI;
import utils.TableBox;
import utils.TableTUI;
import utils.TextTUI;
import utils.Container.Alignment;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Component.clear();

        SelectorTUI opt = new SelectorTUI()
                          .setTitle(new TextTUI("Explore The Library:"))
                          .clearAfterSubmit()
                          .options("TextTUI", "LogoFonts", "Logo Colored",
                                   "Gradient Color", "Image to Text", "Logo With Image",
                                   "Box", "Responsive Box", "TableBox",
                                   "Input", "Selector", "Quote",
                                   "Click Event", "Tables", "Align Text",
                                   "Automation", "Container");

        int select = opt.prompt();
        // switch (select) {
        //     case 0:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Text Customization").setColor("#3abdff")));
        //         System.out.println();
        //         TextTUIExample();
        //         break;
        //     case 1:
        //         System.out.println(new BadgeTUI().append(new TextTUI("All Logo Fonts").setColor("#57ff3a")));
        //         System.out.println();
        //         LogoFonts();
        //         break;
        //     case 2:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Logo").setColor("#ea3d46"))
        //                                         .append(new TextTUI("Colored").setColor("#ffad3a"))
        //                                         .append(new TextTUI("Bee").setColor("#96ff3a"))
        //                                         .pathway());
        //         System.out.println();
        //         System.out.println(logoColored());
        //         break;
        //     case 3:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Gradient color").setColor("#ea543d"))
        //                                         .append(new TextTUI("Logo").setColor("#ff3aff"))
        //                                         .append(new TextTUI("Bee").setColor("#c73aff"))
        //                                         .pathway());
        //         System.out.println();
        //         gradientExample();
        //         break;
        //     case 4:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Image to Text").setColor("#46d9fe")));
        //         System.out.println();
        //         System.out.println(imageExample());
        //         break;
        //     case 5:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Logo With Image").setColor("#ffffff").setBackgroundColor("#9046fe")));
        //         System.out.println();
        //         logoWithImage();
        //         break;
        //     case 6:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Box").setColor("#46feb1")));
        //         System.out.println();
        //         boxExample();
        //         break;
        //     case 7:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Responsive Box").setColor("#210eb1")));
        //         System.out.println();
        //         responsiveBox();
        //         break;
        //     case 8:
        //         tableBoxExample();
        //         break;
        //     case 9:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Input").setColor("#0b578d")));
        //         System.out.println();
        //         inputExample();
        //         break;
        //     case 10:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Selector").setColor("#8d820b")));
        //         System.out.println();
        //         selectorExample();
        //         break;
        //     case 11:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Quote").setColor("#032e52")));
        //         System.out.println();
        //         quoteExample();
        //         break;
        //     case 12:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Click Event").setColor("#ed4318")));
        //         System.out.println();
        //         clickExample();
        //         break;
        //     case 13:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Tables").setColor("#83bd0d")));
        //         System.out.println();
        //         tableExample();
        //         break;
        //     case 14:
        //         System.out.println(new BadgeTUI().append(new TextTUI("Align Text").setColor("#bd7a0d")));
        //         System.out.println();
        //         AlignTextExample();
        //         break;
        //     case 15:
        //         automationExample();
        //         break;
        //     case 16:
        //         ContainerExample();
        //         break;
        //     default:
        //         break;
        // }


        Component.clear();
        String xml = """
                <p style=\"Italic Bold\">
                Hello
                hi
                <p>
                    <span color=\"#f42a2a\">First Span</span>
                </p>
                <span>
                    <p>
                        <span bg-color=\"#48f42a\">for more Info visit </span>
                        <a href=\"https://github.com/Fadhl0/JTUI\" color=\"#60ffff\">My Github account</a>
                    </p>
                </span>
                hello again
                </p>
                """;
        TextTUI t = new SmartTextTUI(xml);
        System.out.println(t);
        
    }

    private static void ContainerExample() {
        BadgeTUI title = new BadgeTUI()
                              .append(new TextTUI(Icons.Option + " " + "Container")
                              .setColor("#ffa726"))
                              .pathway();
        
        TableTUI table1 = new TableTUI();
        ImageTUI img = new ImageTUI(Paths.get("imgs", "15.png"));
        LogoTUI text = new LogoTUI("\n\nTyper\n   Java", TUIFont.ANSIShadow)
                            .trim()
                            .setColor("#ffa726")
                            .setShadowColor("#ffcc80")
                            .setVersion(new TextTUI("version 1.5.8"));

        table1.addRow(
            new TextTUI(img.toString()),
            new TextTUI(
                Gradient.set(text.toString(), "vertical", "#ff7226", "#ffa726")
            )
        );


        BoxTUI userBox = new BoxTUI()
                .color("#eab308") // Amber/Gold Border
                .label(new TextTUI(" CURRENT SESSION ").bold().setColor("#ca8a04"))
                .innerText(
                        new TextTUI("\n")
                            .appendText(new TextTUI("  User:        admin_dev\n").bold().setColor(Colors.Rose500))
                            .appendText(new TextTUI("  Role:        Superuser\n").setColor(Colors.Rose500))
                            .appendText(new TextTUI("  Environment: Production\n").setColor(Colors.Blue500))
                );

        BoxTUI securityBox = new BoxTUI()
                .color("#ef4444") // Red Border
                .label(new TextTUI(" SECURITY LOGS ").bold().setColor("#dc2626"))
                .innerText(
                        new TextTUI("\n")
                            .appendText(new TextTUI("  Last Login:  2026-06-05 21:04\n").setColor(Colors.Blue500))
                            .appendText(new TextTUI("  IP Address:  192.168.1.45\n").setColor(Colors.Blue500))
                            .appendText(new TextTUI("  Active SSH:  1 Session\n").setColor(Colors.Blue500))
                );

        TableBox table2 = new TableBox(userBox.responsive(), securityBox.responsive());

        Input input = new Input()
                        .label(new TextTUI(" Add Text "))
                        .placeholder(new TextTUI("Type something…").setColor("#5f5f5f"))
                        .maxLength(100)
                        .setBorderColor(Colors.setColor("#65ff70"))
                        .width(-1)
                        .onChange(result -> {
                            System.out.println(AlignText.bottom("Change hook: " + result));
                        })
                        .onSubmit(result -> System.out.println(AlignText.bottom("Submit hook: " + result)))
                        .icon(new TextTUI(" " + Icons.Search.get() + " ").setColor("#62ff54"));

        InputContainer in = input.build();

        Container c1 = new Container();
        
        c1.append(title::toString);
        c1.append(() -> "\n");
        c1.append(table1::toString, Alignment.INLINE_CENTER);
        
        c1.append(() -> "\n");
        c1.append(in::prompt);
        c1.append(table2::toString);
        

        in.setContainer(c1);

        c1.execute();
        in.listen();
        c1.stop();

    }

    private static void automationExample() {
        TextCell cell1 = new TextCell()
                             .setText(new TextTUI("🚀 Welcome to the CLI Developer Setup Wizard!\n").setColor("#169cda"));
        
        ScannerCell cell2 = new ScannerCell("Building your development environment...")
                                .updateStatus(
                                    new TextTUI("Environment configured successfully! 🎉\n")
                                    .setColor(Colors.setColor("#16c60c"))
                                );
        cell2.withTask(() -> {
            try {
                Thread.sleep(5000);
                cell2.status("Please do not close!");
                Thread.sleep(2000);
                cell2.status("Almost done!");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        TextTUI temp = new TextTUI("Generating local environment keys...").bold().setColor("#d164ff");
        SpinnerCell cell3 = new SpinnerCell()
                            .arc()
                            .status(temp)
                            .updateStatus(
                                new TextTUI("Security keys secured! 🔑\n")
                                .setColor(Colors.setColor("#16c60c"))
                            ).withTask(() -> {
                                try {
                                    Thread.sleep(5000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });

        String[] options = {"Turkcell", "Türk Telekom", "Vodafone Turkey"};
        AtomicReference<Integer> result = new AtomicReference<>();
        SelectorCell cell = new SelectorCell()
                            .options(options)
                            .onSubmit(result::set)
                            .setTitle(new TextTUI("Select your primary Telecom provider:\n"));

        List<Integer> result1 = new ArrayList<>();
        MultiSelectorCell cell4 = new MultiSelectorCell()
                            .options(options)
                            .onSubmit(result1::addAll)
                            .preSelect(0, 1)
                            .setTitle(new TextTUI("Select additional telecom provider for backup:"));

        AtomicReference<String> result2 = new AtomicReference<>();
        InputCell cell5 = new InputCell()
                    .setTitle(new TextTUI("Enter your TR Phone Number:\n"))
                    .setPlaceholder(new TextTUI("5xxxxxxxxx").setColor("#696969"))
                    .setLimitInput(10)
                    .setValidator(
                        text -> !text.isEmpty() && text.charAt(0) == '5',
                        "TR Phone numbers must begin with '5'!"
                    )
                    .setValidator(t -> t.matches("\\d+"), "Phone number must contain only numeric digits!")
                    .setValidator(
                        text -> text.length() == 10,
                        "Phone number must be exactly 10 digits long!"
                    )
                    .onSubmit(result2::set);

        Component.clear();
        System.out.println();
        
        Automation.create().appendCell(cell1)
                           .appendCell(cell5)
                           .appendCell(cell)
                           .appendCell(cell4)
                           .appendCell(cell2)
                           .appendCell(cell3)
                           .setBuffer(2)
                           .setTitle(new TextTUI(" JTUI ").setBackgroundColor("#f44336"))
                           .summary(
                                new TextTUI(" Your Info. "),
                                () -> {
                                    return 
                                        new TextTUI("\nPhone Number: " + result2.get() + "\nPrimary Telecom SIM: " + options[result.get()] + "\nOther providers in index: " + result1 + "\n")
                                          .setColor("#66b8f7");
                                }
                           )
                           .start();

        System.out.println();
    }

    private static void AlignTextExample() {
        Component.clear();
        System.out.println(AlignText.centerBottom("Center Bottom"));
        System.out.println(AlignText.at(6, 5, "Custom Location"));
        System.out.println(AlignText.centerTop("Center Top"));
        System.out.println(AlignText.center("Center"));
    }

    private static void tableExample() {
        TableTUI table = new TableTUI();
        TextTUI header1 = new TextTUI("Student Name").bold();
        TextTUI header2 = new TextTUI("Student Number").bold();

        table.addRow(header1, header2);
        table.addRow(new TextTUI("Fadhl Al Fadhili"),
                     new TextTUI("2363825"));

        table.addRow(new TextTUI("Naif Mohammad Siddiq"),
                     new TextTUI("2284351"));
        System.out.println(table);
    }

    private static void clickExample() {
        OnClick.reset();
        OnClick.add(KeyModifier.CTRL.with('c'), () -> {
            System.out.println(new TextTUI("Press Any button to quit!").bold());
            OnClick.add(KeyPress.AnyKey, () -> {
                OnClick.cancel();
            });
        });

        OnClick.add(KeyPress.Escape, () -> {
            OnClick.cancel();
        });

        OnClick.add(KeyPress.Space, () -> {
            System.out.println("Space");
        });

        OnClick.add(KeyPress.Up_Arrow, () -> {
            System.out.println("Up Arrow");
        });
        OnClick.add(KeyPress.Down_Arrow, () -> {
            System.out.println("Down Arrow");
        });
        OnClick.add(KeyPress.Left_Arrow, () -> {
            System.out.println("Left Arrow");
        });
        OnClick.add(KeyPress.Right_Arrow, () -> {
            System.out.println("Right Arrow");
        });

        OnClick.add(KeyPress.Backspace, () -> {
            System.out.println("Backspace");
        });
        OnClick.add(KeyPress.Enter, () -> {
            System.out.println("Enter");
        });
        OnClick.add(KeyPress.Tab, () -> {
            System.out.println("Tab");
        });
        OnClick.add(KeyPress.F1, () -> {
            System.out.println("F1");
        });
        OnClick.add(KeyPress.F2, () -> {
            System.out.println("F2");
        });
        OnClick.add(KeyPress.F3, () -> {
            System.out.println("F3");
        });
        OnClick.add(KeyPress.F4, () -> {
            System.out.println("F4");
        });
        OnClick.add(KeyPress.F5, () -> {
            System.out.println("F5");
        });
        OnClick.add(KeyPress.F10, () -> {
            System.out.println("F10");
        });

        OnClick.add(KeyModifier.CTRL.with('A'), () -> {
            System.out.println("CTRL + A");
        });
        OnClick.add(KeyModifier.CTRL.with('X'), () -> {
            System.out.println("CTRL + X");
        });
        OnClick.add(KeyModifier.ALT.with('A'), () -> {
            System.out.println("ALT + A");
        });

        OnClick.add(KeyPress.Home, () -> {
            System.out.println("Home");
        });
        OnClick.add(KeyPress.End, () -> {
            System.out.println("End");
        });
        OnClick.add(KeyPress.Delete, () -> {
            System.out.println("Delete");
        });

        try {
            OnClick.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void quoteExample() {
        TextTUI quoteText = new TextTUI(
                                "“Removing harmful things from the road is an act of charity”"
                            )
                            .italic();

        TextTUI quoteName = new TextTUI("― Prophet Mohammad (PBUH)")
                                 .setColor("c8c3bc").bold();

        QuoteTUI quote = new QuoteTUI()
                            .setBorderColor("#c8c3bc")
                            .setBackgroundColor("#252627")
                            .breakLine()
                            .append(quoteText)
                            .append(quoteName);

        System.out.println(quote);
    }

    private static void selectorExample() {
        SelectorTUI selector = new SelectorTUI()
                                  .setTitle(new TextTUI("What is your favorite color?"))
                                //   .clearAfterSubmit()
                                  .options("Red", "Orange", "Green", "Blue", "Pink", "Other");

        int select = selector.prompt();
        System.out.println("You select: index " + select);
    }

    private static void inputExample() {
        InputTUI input = new InputTUI()
                             .label(new TextTUI(" Enter Your Email ").setColor(Colors.Green600))
                             .placeholder(new TextTUI("example@company.com").setColor(Colors.Gray500))
                             .setIcon(new TextTUI(" > ").bold())
                             .setBorderColor("#8aff0c")
                             .validator(t -> !t.isEmpty(), new TextTUI("⚠  Empty Field!"))
                             .validator(t -> t.contains("@") && t.contains("."),
                                        new TextTUI("⚠  Invalid Email!")
                             );
            
        String textOutput = input.build().prompt();
        System.out.println("You submit: " + textOutput);
    }

    private static void tableBoxExample() {

        BarTUI bar1 = new BarTUI()
                .setCustomIcons("━", "━")
                .setWidth(20)
                .setActiveColor(Colors.Error500)
                .setPercentage(60);;
                
        BarTUI bar2 = new BarTUI()
                .setCustomIcons("█", "█")
                .setWidth(20)
                .showPercentage(false)
                .setPercentage(100);
        
        BarTUI bar3 = new BarTUI()
                .setWidth(5)
                .setBuffer(1)
                .showPercentage(false)
                .setPercentage(26.1);

        TableTUI boxes = new TableTUI()
                              .addRow(new TextTUI("  Level"), new TextTUI(bar2.toString()), new TextTUI("100.0%"))
                              .addRow(new TextTUI("  Health"), new TextTUI(bar3.toString()), new TextTUI("26.1%"))
                              ;
        
        BoxTUI box = new BoxTUI()
                        .color("#a4f94f")
                        .label(new TextTUI(" Overall Progress ").bold().setColor("#d73030"))
                        .innerText(
                                new TextTUI("\n\n")
                                    .appendText(
                                        new TextTUI("   All Jobs " + bar1).appendText(new TextTUI(" 0:00:12   ").setColor("657364"))
                                    )
                                    .appendText(new TextTUI("\n\n"))
                        );
        TableBox tb = new TableBox(
                                box.responsive(),
                                new BoxTUI()
                                    .label(new TextTUI(" " + Icons.OCR.get() + " Power ").setColor("#a4f94f"))
                                    .innerText(
                                        new TextTUI("\n")
                                        .appendText(new TextTUI(boxes.toString()))
                                        .appendText(new TextTUI("\n"))
                                        .appendText(new TextTUI("  Charged " + Icons.Bullet.get() + " 0:00 " + Icons.Bullet.get() + " 22W " ))
                                        .appendText(new TextTUI("\n"))
                                    ).responsive()
                                    .color("#d73030")
                            );
        
        Container c1 = new Container();
        c1.append(tb::build);
        
        c1.execute();
    }

    private static void responsiveBox() {
        BoxTUI box = new BoxTUI()
                        .color("#a4f94f")
                        .label(new TextTUI(" Our Label ").bold().setColor("#bc3030"))
                        .innerText(
                                new TextTUI("\n")
                                    .appendText(
                                        new TextTUI("  Hello everyone! nice to see me! \n")
                                        .setColor(Colors.Rose500)
                                    )
                        );
        System.out.println(box.responsive().build());
    }

    private static void boxExample() {
        BoxTUI box = new BoxTUI()
                        .color("#a4f94f")
                        
                        .innerText(
                                new TextTUI("\n")
                                    .appendText(
                                        new TextTUI("  Hello everyone! nice to see me! \n")
                                        .setColor(Colors.Rose500)
                                    )
                        );

        System.out.println(box.shape(boxShape.SingleLine).label(new TextTUI(" SingleLine ").bold().setColor("#bc3030")));
        System.out.println();
        System.out.println(box.shape(boxShape.SharpCornerLine).label(new TextTUI(" SharpCornerLine ").bold().setColor("#bc3030")));
        System.out.println();
        System.out.println(box.shape(boxShape.MultiLine).label(new TextTUI(" MultiLine ").bold().setColor("#bc3030")));
        System.out.println();
        System.out.println(box.shape(boxShape.QuantaLine).label(new TextTUI(" QuantaLine ").bold().setColor("#bc3030")));
        System.out.println();
        System.out.println(box.shape(boxShape.SideLine).label(new TextTUI(" SideLine ").bold().setColor("#bc3030")));
    }

    private static void logoWithImage() {
        TableTUI logoWithImage = new TableTUI()
                                    .addRow(
                                        new TextTUI(logoColored()),
                                        new TextTUI(imageExample())
                                    );
        
        System.out.println(logoWithImage);
    }

    private static String imageExample() {
        ImageTUI image = new ImageTUI(Paths.get("imgs", "bee.png"))
                            //   .setSymbol(Icons.CircleSolid.get())
                            //   .replaceColor("#343341", "#a0a0a0")
                              ;
        return image.toString();
    }

    private static String logoColored() {
        LogoTUI logo = new LogoTUI("Bee", TUIFont.ANSIShadow)
                            .setColor("#ffe800")
                            .setShadowColor("#fff9bc")
                            // .trim()
                            ;
        
        return logo.toString();
    }

    private static void gradientExample(){ 
        System.out.println(new BadgeTUI().append(new TextTUI("Vertical direction").setColor("#f63e00")).pathway());
        System.out.println(
            Gradient.set(logoColored(), "vertical", "#d1f600", "#f63e00")
        );

        System.out.println(new BadgeTUI().append(new TextTUI("Horizontal direction").setColor("#f63e00")).pathway());
        System.out.println(
            Gradient.set(logoColored(), "horizontal", "#d1f600", "#f63e00")
        );
        
        System.out.println(new BadgeTUI().append(new TextTUI("Diagonal direction").setColor("#f63e00")).pathway());
        System.out.println(
            Gradient.set(logoColored(), "diagonal", "#d1f600", "#f63e00")
        );
    }

    private static void LogoFonts() {
        for(TUIFont i : TUIFont.values()) {
            System.out.println(new LogoTUI("Bee", i).trim() + "\n\n");
        }
    }

    private static void TextTUIExample() {
        System.out.print(new TextTUI("Red Color\n").setColor(Colors.Error600));
        System.out.print(new TextTUI("Red Background Color\n").setBackgroundColor(Colors.Error600));
        System.out.print(new TextTUI("Bold\n").bold());
        System.out.print(new TextTUI("Italic\n").italic());
        System.out.print(new TextTUI("Underline\n").underline());
        System.out.print(new TextTUI("Strikethrough\n").strikethrough());
        System.out.print(new TextTUI("Invert\n").invert());

        System.out.println();
    }

}
