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
import automation.BoxCell;
import automation.InputCell;
import automation.MultiSelectorCell;
import automation.ScannerCell;
import automation.SelectorCell;
import automation.SpinnerCell;
import automation.TextCell;
import inputForm.SelectorTUI;
import inputForm.boxShape;
import inputForm.Input;
import inputForm.boxBorder.BoxTUI;
import inputForm.Input.InputTUI;
import utils.AlignText;
import utils.BadgeTUI;
import utils.BarTUI;
import utils.Colors;
import utils.Component;
import utils.Container;
import utils.Gradient;
import utils.Icons;
import utils.QuoteTUI;
import utils.SmartTableTUI;
import utils.SmartTextTUI;
import utils.TableBox;
import utils.TableTUI;
import utils.TextTUI;
import utils.Container.Alignment;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Component.clear();
        
        AtomicReference<Integer> select = new AtomicReference<>();
        SelectorTUI opt = new SelectorTUI()
                          .setTitle(new TextTUI("Explore The Library:"))
                          .clearAfterSubmit()
                          .limitDisplay(6)
                          .onSubmit(select::set)
                          .add("TextTUI")
                          .add("Smart TextTUI")
                          .add("LogoFonts")
                          .add("Logo Colored")
                          .add("Gradient Color")
                          .add("Image to Text")
                          .add("Logo With Image")
                          .add("Box")
                          .add("Responsive Box")
                          .add("TableBox")
                          .add("Input")
                          .add("Selector")
                          .add("Quote")
                          .add("Click Event")
                          .add("Tables")
                          .add("Align Text")
                          .add("Container", "replaceable of GUI")
                          .add("Automation", "Scanner | Selector | Input | Spinner and more")
                          ;
                          
        opt.execute();

        Component.clear();

        switch (select.get()) {
            case 0:
                System.out.println(new BadgeTUI().append(new TextTUI("Text Customization").setColor("#3abdff")));
                System.out.println();
                TextTUIExample();
                break;
            case 1:
                smartTextTUI();
                break;
            case 2:
                System.out.println(new BadgeTUI().append(new TextTUI("All Logo Fonts").setColor("#57ff3a")));
                System.out.println();
                LogoFonts();
                break;
            case 3:
                System.out.println(new BadgeTUI().append(new TextTUI("Logo").setColor("#ea3d46"))
                                                .append(new TextTUI("Colored").setColor("#ffad3a"))
                                                .append(new TextTUI("Bee").setColor("#96ff3a"))
                                                .pathway());
                System.out.println();
                System.out.println(logoColored());
                break;
            case 4:
                System.out.println(new BadgeTUI().append(new TextTUI("Gradient color").setColor("#ea543d"))
                                                .append(new TextTUI("Logo").setColor("#ff3aff"))
                                                .append(new TextTUI("Bee").setColor("#c73aff"))
                                                .pathway());
                System.out.println();
                gradientExample();
                break;
            case 5:
                System.out.println(new BadgeTUI().append(new TextTUI("Image to Text").setColor("#46d9fe")));
                System.out.println();
                System.out.println(imageExample());
                break;
            case 6:
                System.out.println(new BadgeTUI().append(new TextTUI("Logo With Image").setColor("#ffffff").setBackgroundColor("#9046fe")));
                System.out.println();
                logoWithImage();
                break;
            case 7:
                System.out.println(new BadgeTUI().append(new TextTUI("Box").setColor("#46feb1")));
                System.out.println();
                boxExample();
                break;
            case 8:
                System.out.println(new BadgeTUI().append(new TextTUI("Responsive Box").setColor("#210eb1")));
                System.out.println();
                responsiveBox();
                break;
            case 9:
                tableBoxExample();
                break;
            case 10:
                System.out.println(new BadgeTUI().append(new TextTUI("Input").setColor("#0b578d")));
                System.out.println();
                inputExample();
                break;
            case 11:
                System.out.println(new BadgeTUI().append(new TextTUI("Selector").setColor("#8d820b")));
                System.out.println();
                selectorExample();
                break;
            case 12:
                System.out.println(new BadgeTUI().append(new TextTUI("Quote").setColor("#032e52")));
                System.out.println();
                quoteExample();
                break;
            case 13:
                System.out.println(new BadgeTUI().append(new TextTUI("Click Event").setColor("#ed4318")));
                System.out.println();
                clickExample();
                break;
            case 14:
                System.out.println(new BadgeTUI().append(new TextTUI("Tables").setColor("#83bd0d")));
                System.out.println();
                tableExample();
                break;
            case 15:
                System.out.println(new BadgeTUI().append(new TextTUI("Align Text").setColor("#bd7a0d")));
                System.out.println();
                AlignTextExample();
                break;
            case 16:
                ContainerExample();
                break;
            case 17:
                automationExample();
                break;
            default:
                break;
        }
        
    }

    private static void ContainerExample() {
        BadgeTUI title = new BadgeTUI()
                              .append(new TextTUI(Icons.Option + " " + "Container")
                              .setColor("#ffa726"))
                              .pathway();
        
        TableTUI table1 = new TableTUI();
        ImageTUI img = new ImageTUI(Paths.get("imgs", "11.png"));
        LogoTUI text = new LogoTUI("\n\nArtificial\n Stupidity", TUIFont.ANSIShadow)
                            .trim()
                            .setColor("#ffa726")
                            .setShadowColor("#ffcc80")
                            .setVersion(new TextTUI("version 2.0.5"));

        table1.addRow(
            new TextTUI(img.toString()),
            new TextTUI(
                Gradient.set(text.toString(), "vertical", "#ff7226", "#ffa726")
            )
        );

        String funny = """
                <root>
                    <p style="bold">Beep boop! 🤖</p>
                    <br>
                    <p>Thank you for your inquiry. As an AI powered by the latest Artificial Stupidity algorithms,</p>
                    <p>I have processed your complex query through my single remaining data granule and concluded that thinking is hard.</p>
                    <br>
                    <p>Therefore, my official answer to your question is: <span style="bold">Yes, no, maybe, and 42.</span></p>
                    <br>
                    <p>If this response did not solve your problem, please try asking a human,</p>
                    <p>or just stare blankly at a wall for five minutes. We will achieve the same results.</p>
                    <br>
                    <p style="italic">Have a mathematically average day!</p>
                </root>
                """;
        SmartTextTUI funniestAI = new SmartTextTUI(funny);

        Container c1 = new Container();

        String descTags = """
                <root>
                    <br><br><br>
                    <p>ensures humanity stays safely at the top of the food chain.</p>
                    <p>It combines the processing power of millions of servers with</p>
                    <p>the uncanny ability to confidently lose an argument against a brick wall.</p>
                    <p style="bold" color="#ffa726">Saves energy by choosing not to think at all.</p>
                </root>
                """;
        SmartTextTUI desc = new SmartTextTUI(descTags);

        // append to Container
        c1.append(title::toString);
        c1.append(() -> "\n");
        Object li = c1.append(table1::toString, Alignment.INLINE_CENTER);
        Object d = c1.append(() -> desc.toString(), Alignment.INLINE_CENTER);

        AtomicReference<String> res3 = new AtomicReference<>();
        InputTUI input = new InputTUI()
                        .placeholder(new TextTUI("Type something…").setColor("#5f5f5f"))
                        .maxLength(100)
                        .setActiveBorderColor(Colors.setColor("#65ff70"))
                        .setInactiveBorderColor(Colors.setColor("#696969"))
                        .width(-1)
                        .startKey(KeyPress.Space)
                        // .onChange(result -> {
                        //     System.out.println(AlignText.bottom("Change hook: " + result));
                        // })
                        .onSubmit((s) -> {
                            if(res3.get() == null) {
                                c1.remove(li);
                                c1.remove(d);
                                c1.append(funniestAI::toString, Alignment.INLINE_CENTER);
                            }
                            res3.set(s);
                        })
                        .clearAfterSubmit()
                        .setIcon(new TextTUI(" > ").setColor("#62ff54"));

        Input in = input.build();
        
        c1.appendComponent(in, Alignment.BOTTOM);
        c1.append(() -> msg(), Alignment.CENTER_BOTTOM);

        OnClick.add(KeyModifier.CTRL.with('r'), () -> {
            c1.stop();
            Container2();
        });
        
        c1.execute();

    }

    private static void Container2() {

        Container c = new Container();

        String[] opt1 = new String[]{"Stupid-Llama", "Dumb-E", "Qwen2.5-0.5B", "TinyLlama-1.1B", "Xenomorph", "Civilian"};
        SelectorTUI selector = new SelectorTUI()
                                  .setTitle(new TextTUI(" Select the Artificial Stupidity Model ").setColor("#006511"))
                                  .onSubmit((res) -> System.out.println(AlignText.at(25, 0, "You select: " + opt1[res])))
                                  .limitDisplay(4)
                                  .wrapInBorder(boxShape.SingleLine, "#00b14a", "#696969", true)
                                  .add("Stupid-Llama").add("Dumb-E")
                                  .add("Qwen2.5-0.5B").add("TinyLlama-1.1B")
                                  .add("Xenomorph").add("Civilians")
                                  .setInactiveSelectorIcon(" ", "")
                                  .setActiveSelectorIcon(">", "#00b14a")
                                  .startKey(null);
        
        String[] opt2 = new String[]{"Low", "Medium", "High", "Max"};
        SelectorTUI selector2 = new SelectorTUI()
                                  .setTitle(new TextTUI(" Effort ").setColor("#751600"))
                                  .onSubmit((res) -> System.out.println(AlignText.at(25, 0, "Effort: " + opt2[res])))
                                  .wrapInBorder(boxShape.SingleLine, "#ff3f3f", "#696969", true)
                                  .add("Low").add("Medium")
                                  .add("High").add("Max")
                                  .startKey(null);
                                  

        SmartTableTUI ta = new SmartTableTUI("Name", "Version", "Tap", "Description", "Installs")
                                .tableRows("grid-commander", "1.4.2", "fadhl/games", "Grid-based real-time strategy game", "1,558")
                                .tableRows("void-craft", "0.9.5", "fadhl/games", "Space-themed real-time strategy game", "412")
                                .tableRows("op-vault-sync", "2.1.0", "fadhl/tools", "Command-line helper for 1Password workflows", "3,401")
                                .tableRows("kube-quick", "0.11.2", "fadhl/tools", "Lightweight cluster management helper", "12,558")
                                .onSubmit((res) -> System.out.println(AlignText.at(25, 0, "You select index "+res)))
                                .activeColor("#d9ba7d")
                                .startKey(null);

        c.appendComponent(selector);
        c.appendComponent(selector2);
        c.appendComponent(ta);
        c.append(() -> msg(), Alignment.CENTER_BOTTOM);

        OnClick.add(KeyModifier.CTRL.with('r'), () -> {
            c.stop();
            ContainerExample();
        });
        c.execute();

    }

    private static String msg() {
        String keys = """
                <root>
                    <span style="bold" color="#b00000"> CTRL+C</span>
                    <span> Exit ∙</span>
                    <span style="bold" color="#82b900"> Tab</span>
                    <span> Navigate b/w Components ∙</span>
                    <span style="bold" color="#00b5bf"> Escape</span>
                    <span> Deactivate Components ∙</span>
                    <span style="bold" color="#c98600"> Space</span>
                    <span> Activate Input ∙</span>
                    <span style="bold" color="#0040c9"> CTRL+R</span>
                    <span> Navigate b/w Tabs</span>
                </root>
                """;
        SmartTextTUI hotkeys = new SmartTextTUI(keys);
        return hotkeys.toString();
    }

    private static void smartTextTUI() {
        String xml = """
            <root>
                <p style="bold" color="#60ffff">=== JTUI Text Rendering Engine Showcase ===</p>
                
                <p>
                    This is a standard paragraph element. Text can be styled easily. 
                    <br>
                    You can apply <span style="bold">Bold</span>, <span style="italic">Italic</span>, 
                    <span style="underline">Underline</span>, <span style="strikethrough">Strikethrough</span>, 
                    and even <span style="invert">Inverted Text Colors</span>.
                </p>

                <p>
                    <span color="#f42a2a" style="bold">Vibrant Foreground Colors</span> can be mixed with 
                    <span bg-color="#48f42a" color="#000000">Custom Background Badges</span> to make important terminal text pop out.
                </p>

                <p>
                    <span style="bold">Interactive Links: </span> 
                    <a href="https://github.com/Fadhl0/JTUI" color="#60ffff" style="underline">Explore JTUI on GitHub</a>
                    <span color="#ff5353" style="italic"> (CTRL + Right-click to open)</span>
                </p>

                <br>

                <p style="bold" color="#f9f948">--- Layout Component: Custom Lists ---</p>
                <ul>
                    <li>Standard Bullet Item (Coffee)</li>
                    <li>Customizable Nested Hierarchies:
                        <ul gap="4" icon="◌" color="#60ffff">
                            <li>First Level Nesting (Black tea)</li>
                            <li>Second Level Nesting (Green tea)
                                <ul gap="8" icon="-" color="#f42a2a">
                                    <li>Deep Origin: China</li>
                                    <li>Deep Origin: Africa</li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li>Standard Bullet Item (Milk)</li>
                </ul>

                <br>
                <br>

                <p style="bold" color="#f9f948">--- Layout Component: Structured Grid Tables ---</p>

                <br>

                <table gap="6">
                    <tr style="bold">
                        <td color="#f9f948" style="underline">Month</td>
                        <td bg-color="#a3a3a3" color="#000000">  Financial Savings  </td>
                        <td color="#60ffff">Status</td>
                    </tr>
                    <tr color="#f95748">
                        <td>January</td>
                        <td>$100.00</td>
                        <td>Pending</td>
                    </tr>
                    <tr color="#48f42a">
                        <td>February</td>
                        <td>$2,000.00</td>
                        <td>Cleared</td>
                    </tr>
                    <tr style="bold">
                        <td> </td>
                        <td style="underline">Total: $2,100.00</td>
                        <td>Done</td>
                    </tr>
                </table>
                
                <br>
                <p color="#a3a3a3" style="italic">End of JTUI Terminal Engine Demonstration.</p>
            </root>
            """;
        SmartTextTUI t = new SmartTextTUI(xml);
        System.out.println(t);
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
                            .add("Turkcell", "This is the BEST!")
                            .add("Türk Telekom")
                            .add("Vodafone Turkey", "This is the worst!")
                            .add("Vodafo4ne Turkey", "This is the worst!")
                            .add("Vodafon3e Turkey", "This is the worst!")
                            .add("Vodafone3 Turkey", "This is the worst!")
                            // .limitDisplay(4)
                            .onSubmit(result::set)
                            .setTitle(new TextTUI("Select your primary Telecom provider:\n"));

        List<Integer> result1 = new ArrayList<>();
        MultiSelectorCell cell4 = new MultiSelectorCell()
                            .add("Turkcell", "This is the BEST!")
                            .add("Türk Telekom")
                            .add("Vodafone Turkey", "This is the worst!")
                            .add("Vodafo4ne Turkey", "This is the worst!")
                            .add("Vodafon3e Turkey", "This is the worst!")
                            .add("Vodafone3 Turkey", "This is the worst!")
                            // .limitDisplay(5)
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

        BoxCell summery = new BoxCell()
                            .label(new TextTUI(" Your Info. "))
                            // .roundCorners(true)
                            .setText(() -> {
                                    return 
                                        new TextTUI("\nPhone Number: " + result2.get() + "\nPrimary Telecom SIM: " + options[result.get()] + "\nOther providers in index: " + result1 + "\n")
                                          .setColor("#66b8f7");
                                    });
        
        Automation.create()
        .appendCell(cell1)
                           .appendCell(cell5)
                           .appendCell(cell)
                           .appendCell(cell4)
                           .appendCell(cell2)
                           .appendCell(cell3)
                           .appendCell(summery)
                           .roundCorner()
                           .setBuffer(2)
                           .setTitle(new TextTUI(" JTUI ").setBackgroundColor("#f44336"))
                           .start();

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

        TextTUI h0 = new TextTUI("#").setColor("#ff49b6");
        TextTUI h1 = new TextTUI("SERVICE NAME").setColor("#8c49ff");
        TextTUI h2 = new TextTUI("PID").setColor("#494cff");;
        TextTUI h3 = new TextTUI("CPU %").setColor("#49ff98");;
        TextTUI h4 = new TextTUI("MEMORY USE").setColor("#f0ff49");;
        TextTUI h5 = new TextTUI("HEALTH STATUS").bold().setColor("#ff8049");

        table.addRow(h0, h1, h2, h3, h4, h5);

        table.addRow(
            new TextTUI("1"),
            new TextTUI("Gateway Router"), 
            new TextTUI("1042"), 
            new TextTUI("1.2%"), 
            new TextTUI("142 MB"), 
            new TextTUI("[ONLINE]")
        );

        table.addRow(
            new TextTUI("2"),
            new TextTUI("Authentication API"), 
            new TextTUI("3109"), 
            new TextTUI("14.5%"), 
            new TextTUI("894 MB"), 
            new TextTUI("[STABLE]")
        );

        table.addRow(
            new TextTUI("3"),
            new TextTUI("PostgreDB Cluster"), 
            new TextTUI("0822"), 
            new TextTUI("42.1%"), 
            new TextTUI("4102 MB"), 
            new TextTUI("[HEAVY LOAD]")
        );

        table.addRow(
            new TextTUI("4"),
            new TextTUI("Cache Layer (Redis)"), 
            new TextTUI("1145"), 
            new TextTUI("0.0%"), 
            new TextTUI("64 MB"), 
            new TextTUI("[IDLE]")
        );

        table.addRow(
            new TextTUI(""),
            new TextTUI("Total Core Services"), 
            new TextTUI("[4]"), 
            new TextTUI("57.8%"), 
            new TextTUI("5202 MB"), 
            new TextTUI("NOMINAL SYSTEM")
        );

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
        AtomicReference<Integer> result = new AtomicReference<>();
        SelectorTUI selector = new SelectorTUI()
                                  .setTitle(new TextTUI("What is your favorite color?"))
                                  .onSubmit(result::set)
                                  .clearAfterSubmit()
                                  .add("Red").add("Orange")
                                  .add("Green").add("Blue")
                                  .add("Pink").add("Other");

        selector.execute();
        System.out.println("You select: index " + result.get());
    }

    private static void inputExample() {
        InputTUI input = new InputTUI()
                             .label(new TextTUI(" Enter Your Email ").setColor(Colors.Green600))
                             .placeholder(new TextTUI("example@company.com").setColor(Colors.Gray500))
                             .setIcon(new TextTUI(" > ").bold())
                             .setActiveBorderColor("#8aff0c")
                             .setInactiveBorderColor(Colors.Gray500)
                             .validator(t -> !t.isEmpty(), new TextTUI("⚠  Empty Field!"))
                             .validator(t -> t.contains("@") && t.contains("."),
                                        new TextTUI("⚠  Invalid Email!")
                             );
            
        String textOutput = input.build().execute();
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
        TableTUI table = new TableTUI();
        TextTUI[] all = new TextTUI[3];

        all[0] = new TextTUI(
                new BadgeTUI().append(new TextTUI("Vertical direction").setColor("#f63e00")).pathway()
                + "\n"
                + Gradient.set(logoColored(), "vertical", "#d1f600", "#f63e00")
                + "\n"
            );

        all[1] = new TextTUI(
                new BadgeTUI().append(new TextTUI("Horizontal direction").setColor("#f63e00")).pathway()
                + "\n"
                + Gradient.set(logoColored(), "horizontal", "#d1f600", "#f63e00")
                + "\n"
            );
        
        all[2] = new TextTUI(
                new BadgeTUI().append(new TextTUI("Diagonal direction").setColor("#f63e00")).pathway()
                + "\n"
                + Gradient.set(logoColored(), "diagonal", "#d1f600", "#f63e00")
                + "\n"
            );
        
        table.addRow(all);

        System.out.println(table);
    }

    private static void LogoFonts() {
        TableTUI table = new TableTUI();
        String[] logos = new String[TUIFont.values().length];
        int index = 0;
        for(TUIFont i : TUIFont.values()) {
            logos[index] = new LogoTUI("Bee", i).trim() + "\n\n";
            index++;
        }

        table.addRow(new TextTUI(logos[0]), new TextTUI(logos[1]), new TextTUI(logos[3]));
        table.addRow(new TextTUI(logos[4]), new TextTUI(logos[5]), new TextTUI(logos[6]));
        table.addRow(new TextTUI(logos[2]), new TextTUI(logos[9]), new TextTUI(logos[7]));
        table.addRow(new TextTUI(logos[8]));
        System.out.println(table);
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
