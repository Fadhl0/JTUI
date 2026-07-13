# JTUI — Java Terminal UI Framework

> A zero-dependency Java library for building rich, interactive terminal user interfaces.

https://github.com/user-attachments/assets/4bb77c7f-633b-4046-a34b-59c43e5fb444


---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Components](#components)
  - [TextTUI](#texttui)
  - [SmartTextTUI](#smarttexttui)
  - [LogoTUI](#logotui)
  - [Gradient](#gradient)
  - [ImageTUI](#imagetui)
  - [BoxTUI](#boxtui)
  - [TableBox](#tablebox)
  - [TableTUI](#tabletui)
  - [SmartTableTUI](#smarttabletui)
  - [InputTUI](#inputtui)
  - [SelectorTUI](#selectortui)
  - [QuoteTUI](#quotetui)
  - [BadgeTUI](#badgetui)
  - [BarTUI](#bartui)
  - [AlignText](#aligntext)
  - [Container](#container)
  - [OnClick](#onclick)
  - [Automation](#automation)
- [Design Patterns](#design-patterns)
- [Project Structure](#project-structure)


<img width="400" height="400" alt="1-1" src="https://github.com/user-attachments/assets/ca291c1c-dcb7-425c-b32b-ee02a019ce1f" />

---

## Overview

**JTUI** is a pure-Java terminal UI framework built from scratch with **zero external dependencies**. It provides a rich set of components, from styled text and ASCII art logos to interactive inputs, selectors, progress bars, and full layout containers, all driven by ANSI escape codes.

Originally developed as a Java design patterns course project.
Not all methods of these classes mentioned in this readme, it is recommend to explore it more in original code.

**Disclaimer**: Windows terminal width detection is supported. The `Container` layout engine is not yet supported on Windows.

---

## Features

- **True-color ANSI styling** — foreground, background, bold, italic, underline, strikethrough, invert
- **XML-based rich text** — write `<p>`, `<span>`, `<a>`, `<ul>`/`<li>`, `<table>`/`<tr>`/`<td>`, and `<br>` markup and render it as styled `TextTUI` output via `SmartTextTUI`
- **Interactive data tables** — arrow-key-navigable, selectable tables built from raw row data via `SmartTableTUI`
- **ASCII art logos** — multiple FIGlet-style fonts via `LogoTUI`
- **Gradient rendering** — vertical, horizontal, and diagonal color gradients over any text
- **Image to ASCII** — convert PNG images to colored terminal art via `ImageTUI`
- **Box components** — five border styles, responsive width, labels
- **Interactive inputs** — text input with validators, placeholder, icon, and border color
- **Selectors** — single and multi-select menus with keyboard navigation
- **Progress bar** — Unicode block-character bar with live updates
- **Spinner animations** — animated status indicators with async task support
- **Key event system** — `OnClick` with full support for arrows, F-keys, CTRL/ALT modifiers
- **Layout containers** — `Container` and `TableBox` for side-by-side and stacked layouts
- **Automation wizard** — multi-step CLI workflow engine with summary support
- **Quote display** — styled block quotes with custom border and background
- **Collections** — `icons` contains set of icons. `Colors` contains set of best colors for UI design.

---

## Getting Started

JTUI has **no external dependencies**. Clone the repository and compile with a standard Java toolchain.

> **Requires Java 22+.** Java 25 is recommended.

Since the project now spans multiple source directories, compile it by collecting all `.java` files first, then building from that list.

**Windows (cmd):**

```bat
git clone https://github.com/Fadhl0/JTUI.git
cd JTUI
dir /s /B *.java > sources.txt
javac -d bin @sources.txt
java --enable-native-access=ALL-UNNAMED -cp bin App
```

**Bash / Zsh:**

```bash
git clone https://github.com/Fadhl0/JTUI.git
cd JTUI
find . -name "*.java" > sources.txt
javac -d bin @sources.txt
java --enable-native-access=ALL-UNNAMED -cp bin App
```

---

## Components

### TextTUI

The foundation of all text rendering. Supports full ANSI styling via a fluent API.

```java
System.out.println(new TextTUI("Red Color").setColor(Colors.Error600));
System.out.println(new TextTUI("Red Background").setBackgroundColor(Colors.Error600));
System.out.println(new TextTUI("Bold").bold());
System.out.println(new TextTUI("Italic").italic());
System.out.println(new TextTUI("Underline").underline());
System.out.println(new TextTUI("Strikethrough").strikethrough());
System.out.println(new TextTUI("Invert").invert());
```

<img width="267" height="210" alt="Pasted image 20260606121627" src="https://github.com/user-attachments/assets/d33d7787-6a45-4444-b58b-80f017329b3b" />


---

### SmartTextTUI

Converts XML-style markup into styled `TextTUI` output, so rich text (headings, links, lists, tables) can be authored declaratively instead of chained builder calls. Tags must be wrapped in a single root element.

**Supported tags:**

| Tag | Description |
|---|---|
| `<span>` | Inline container used to mark up part of a text |
| `<p>` | Paragraph; automatically adds a single blank line after each element |
| `<a>` | Hyperlink, using the `href` attribute |
| `<ul>` | Unordered list container; supports custom `icon` and `gap` attributes (`icon` sets the bullet, defaults to `•`; `gap` sets left padding, e.g. `gap="2"`) |
| `<li>` | Unordered list item |
| `<table>` | Structured grid table; supports a `gap` attribute for column spacing, e.g. `gap="2"` |
| `<tr>` | Table row container |
| `<td>` | Table cell |
| `<br>` | Single line break |

**Supported attributes** (available across all tags): `color` and `bg-color` (hex, e.g. `color="#00a919"`), and `style` (`bold`, `italic`, `underline`, `strikethrough`, `hidden`, `invert` — space separated, e.g. `style="bold italic"`).

```java
String xml = """
    <root>
        <p style="bold" color="#60ffff">=== JTUI Text Rendering Engine ===</p>
        <p>
            You can apply <span style="bold">Bold</span>,
            <span style="italic">Italic</span>, and
            <span color="#f42a2a">Colored</span> text inline.
        </p>
        <a href="https://github.com/Fadhl0/JTUI" color="#60ffff" style="underline">Explore JTUI on GitHub</a>
        <br>
        <ul icon="◌" gap="2">
            <li>First item</li>
            <li>Second item</li>
        </ul>
        <table gap="6">
            <tr style="bold">
                <td>Month</td>
                <td>Status</td>
            </tr>
            <tr color="#48f42a">
                <td>February</td>
                <td>Cleared</td>
            </tr>
        </table>
    </root>
    """;

SmartTextTUI t = new SmartTextTUI(xml);
System.out.println(t);
```

<img width="1229" height="601" alt="image" src="https://github.com/user-attachments/assets/6b91cad4-e520-489f-bdfd-5b7dda191eb4" />


---

### Colors
Set of best colors for UI design.
#### All Primary Colors:

<img width="2560" height="1440" alt="89a4d209-8882-4d80-bce3-7ca0b8b20d44" src="https://github.com/user-attachments/assets/258c963d-25e5-4e33-973b-91120f76e248" />




---

### LogoTUI

Renders ASCII art text using FIGlet-style fonts. Supports color, shadow color, trimming, and version labels.

```java
LogoTUI logo = new LogoTUI("Bee", TUIFont.ANSIShadow)
                   .setColor("#ffe800")
                   .setShadowColor("#fff9bc")
                   .trim()
                   .setVersion(new TextTUI("version 1.5.8"));

System.out.println(logo);
```

Available fonts are all values of the `TUIFont` enum. You can iterate over them:

```java
for (TUIFont font : TUIFont.values()) {
    System.out.println(new LogoTUI("Bee", font).trim() + "\n\n");
}
```

<img width="1280" height="720" alt="fonts" src="https://github.com/user-attachments/assets/43dcf3a2-5159-4702-849c-db0e739b322f" />


---

### Gradient

Applies a smooth color gradient (2 colors or more) over any multi-line string. Supports three directions.

```java
// Vertical gradient
System.out.println(Gradient.set(logoColored(), "vertical", "#d1f600", "#f63e00"));

// Horizontal gradient
System.out.println(Gradient.set(logoColored(), "horizontal", "#d1f600", "#f63e00"));

// Diagonal gradient
System.out.println(Gradient.set(logoColored(), "diagonal", "#d1f600", "#f63e00"));
```

<img width="1006" height="276" alt="gradiant" src="https://github.com/user-attachments/assets/6a57caba-ed2d-4fb7-9c2e-d408585d37be" />


---

### ImageTUI

Converts a PNG image into a colored Unicode character art rendering in the terminal.

```java
ImageTUI image = new ImageTUI(Paths.get("imgs", "bee.png"));
System.out.println(image);
```

<img width="239" height="213" alt="Pasted image 20260606125044" src="https://github.com/user-attachments/assets/d4d3b4d9-ded3-4284-891f-74a9d34d2af2" />


You can customize the symbol used for pixels and replace specific colors:

```java
ImageTUI image = new ImageTUI(Paths.get("imgs", "bee.png"))
                     .setSymbol(Icons.CircleSolid.get())
                     .replaceColor("#343341", "#a0a0a0");
```
<img width="221" height="190" alt="image" src="https://github.com/user-attachments/assets/f947bc0c-825c-4fb6-bfd6-edd4eaa423d0" />

---

### BoxTUI

Draws a styled border box around content. Five border shapes are available, with support for labels, colors, and responsive width.

```java
BoxTUI box = new BoxTUI()
                .color("#a4f94f")
                .label(new TextTUI(" Our Label ").bold().setColor("#bc3030"))
                .innerText(
                    new TextTUI("\n")
                        .appendText(
		                        new TextTUI("  Hello everyone!\n")
				                        .setColor(Colors.Rose500)
				        )
                );

System.out.println(box.shape(boxShape.SingleLine));
System.out.println(box.shape(boxShape.SharpCornerLine));
System.out.println(box.shape(boxShape.MultiLine));
System.out.println(box.shape(boxShape.QuantaLine));
System.out.println(box.shape(boxShape.SideLine));
```

<img width="978" height="476" alt="box" src="https://github.com/user-attachments/assets/21277c39-751c-40c7-abcd-24dd00c94cda" />

Use `.responsive()` to auto-fit the box to the current terminal width:

```java
System.out.println(box.responsive().build());
```

<img width="863" height="137" alt="Pasted image 20260606130245" src="https://github.com/user-attachments/assets/d2852071-e6b4-4dcf-9e27-a23bc3cd5edc" />


---

### TableBox

Places two `BoxTUI` components side by side, splitting the terminal width proportionally.

```java
TableBox tb = new TableBox(
				box.responsive(),
				new BoxTUI()
					.label(new TextTUI(" " + Icons.OCR.get() + " Power ").setColor("#a4f94f"))
					.innerText(new SmartTextTUI(tags))
					.responsive()
					.color("#d73030")
			);
```

<img width="1078" height="191" alt="image" src="https://github.com/user-attachments/assets/af2691c3-d826-452b-b698-d99b0f0ef9fc" />

---

### TableTUI

A simple columnar table for structured data.

```java
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
...
```

Column widths are distributed proportionally using the **largest-remainder method**. ANSI escape codes are stripped from length calculations so styled content is always aligned correctly.

<img width="804" height="171" alt="image" src="https://github.com/user-attachments/assets/90dee5f0-32ba-4a15-9b90-968adfdd8c7d" />


---

### SmartTableTUI

An interactive, arrow-key-navigable table: rows are added as raw string data and the whole table doubles as a selector, returning the index of the chosen row on submit.

```java
SmartTableTUI table = new SmartTableTUI("Name", "Version", "Tap", "Description", "Installs")
                          .tableRows("grid-commander", "1.4.2", "fadhl/games", "Grid-based real-time strategy game", "1,558")
                          .tableRows("void-craft", "0.9.5", "fadhl/games", "Space-themed real-time strategy game", "412")
                          .tableRows("op-vault-sync", "2.1.0", "fadhl/tools", "Command-line helper for 1Password workflows", "3,401")
                          .onSubmit(index -> System.out.println("You selected index " + index))
                          .activeColor("#d9ba7d")
                          .limitDisplay(4);

table.fire();
```

<img width="1000" height="406" alt="SmartTableTUI" src="https://github.com/user-attachments/assets/51571f06-afc9-43bc-92e3-bdf3f9027113" />


Defaults to starting on `CTRL+T` and stopping on `Escape` — pass `null` to `.startKey()` if the table is being driven by a `Container` instead of activated on its own. `.activeColor()` / `.inactiveColor()` control the header and row color depending on focus state.

---

### InputTUI

An interactive text input field with label, placeholder, icon, border color, and chainable validators.

```java
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

String result = input.build().execute();
```

<img width="400" height="146" alt="2-1" src="https://github.com/user-attachments/assets/7e8ef33c-91db-43bc-89c8-f093665905f1" />


---

### SelectorTUI

An interactive single-choice selector navigated with arrow keys and confirmed with Enter.

```java
AtomicReference<Integer> result = new AtomicReference<>();
SelectorTUI selector = new SelectorTUI()
							.setTitle(new TextTUI("What is your favorite color?"))
							.clearAfterSubmit()
							.add("Red").add("Orange")
							.add("Green").add("Blue")
							.add("Pink").add("Other");

selector.execute();
System.out.println("You select: index " + result.get());
```

Returns the zero-based index of the chosen option.

<img width="400" height="270" alt="3-1" src="https://github.com/user-attachments/assets/a0c7245e-9184-45c4-8d8c-e488994a37bc" />


---

### QuoteTUI

Renders a styled block quote with customizable border and background colors.

```java
QuoteTUI quote = new QuoteTUI()
                    .setBorderColor("#c8c3bc")
                    .setBackgroundColor("#252627")
                    .breakLine()
                    .append(new TextTUI(""Some inspiring quote"").italic())
                    .append(new TextTUI("― Author Name").setColor("c8c3bc").bold());

System.out.println(quote);
```

<img width="760" height="176" alt="Pasted image 20260606131015" src="https://github.com/user-attachments/assets/2e58d086-a22b-4673-ba11-cd79dca0b396" />


---

### BadgeTUI

Builds inline badge-style labels. Supports the `.pathway()` style for breadcrumb-like displays.

```java
System.out.println(
    new BadgeTUI()
        .append(new TextTUI("Logo").setColor("#ea3d46"))
        .append(new TextTUI("Colored").setColor("#ffad3a"))
        .append(new TextTUI("Bee").setColor("#96ff3a"))
        .pathway()
);
```

<img width="287" height="79" alt="Pasted image 20260606152137" src="https://github.com/user-attachments/assets/3b56f2fb-b697-48b4-b39c-18e611db7086" />


---

### BarTUI

A live progress bar using Unicode block characters (`█` / `▒`) with configurable width and a percentage supplier for real-time updates.

```java
AtomicReference<Double> progress = new AtomicReference<>(0.0);

BarTUI bar = new BarTUI()
                .setCustomIcons("█", "█")
                .setWidth(20)
                .setPercentage(progress::get);

// Update progress from anywhere, e.g. a scheduled task
progress.set(72.5);
```

<img width="315" height="59" alt="Pasted image 20260606152506" src="https://github.com/user-attachments/assets/dd312d41-e0b3-4163-ace9-99f6262ca303" />
<br>

<img width="201" height="69" alt="Pasted image 20260606152415" src="https://github.com/user-attachments/assets/5e849f1a-92ba-4023-8cc1-0022a2f0ca0a" />



---

### AlignText

Positions text at specific locations in the terminal relative to the viewport.

```java
System.out.println(AlignText.center("Center"));
System.out.println(AlignText.centerBottom("Center Bottom"));
System.out.println(AlignText.centerTop("Center Top"));
System.out.println(AlignText.at(6, 2, "Custom Location"));
```

<img width="500" height="386" alt="align" src="https://github.com/user-attachments/assets/7bb818f3-0a5e-4d5e-9f42-7d891fa8d3aa" />


---

### Container

A full-screen layout engine with diff-based re-rendering to eliminate flicker. Supports inline content appended with optional alignment.

```java
Container c = new Container();

InputTUI input = new InputTUI();
Input in = input.build();

c.append(title::toString);
c.append(() -> "\n");
c.appendComponent(in, Alignment.BOTTOM);
c.append(() -> msg(), Alignment.CENTER_BOTTOM);

c.execute();

```

`Container` uses the **alternate screen buffer** (`\033[?1049h`) so the main scrollback is preserved. Dirty-flag diffing compares lines before repainting, ensuring only changed rows are redrawn.



https://github.com/user-attachments/assets/4b935fe4-9a66-4920-9251-28671f8f0fed




---

### OnClick

A key-event dispatcher supporting individual `KeyPress` values, CTRL/ALT modifier combos, and a catch-all `AnyKey` listener.

```java
OnClick.reset();

OnClick.add(KeyPress.Up_Arrow,   () -> System.out.println("Up"));
OnClick.add(KeyPress.Down_Arrow, () -> System.out.println("Down"));
OnClick.add(KeyPress.Enter,      () -> System.out.println("Enter"));
OnClick.add(KeyPress.F1,         () -> System.out.println("F1"));

OnClick.add(KeyModifier.CTRL.with('c'), () -> {
    System.out.println("Ctrl+C pressed — press any key to quit");
    OnClick.add(KeyPress.AnyKey, OnClick::cancel);
});

OnClick.add(KeyPress.Escape, OnClick::cancel);

OnClick.execute(); // blocks until cancelled
```

Supported keys include: Arrow keys, Enter, Backspace, Tab, Escape, Space, Home, End, Delete, F1–F12, and any CTRL or ALT combination.



https://github.com/user-attachments/assets/b1a4b13e-e7d2-404f-ad71-fa9935e5df01



---

### Automation

A multi-step CLI wizard engine. Chain `Cell` instances into a sequential workflow, complete with a final summary panel.

```java
InputCell phoneCell = new InputCell()
    .setTitle(new TextTUI("Enter your phone number:\n"))
    .setPlaceholder(new TextTUI("5xxxxxxxxx").setColor("#696969"))
    .setLimitInput(10)
    .setValidator(t -> t.matches("\\d+"), "Digits only!")
    .setValidator(t -> t.length() == 10, "Must be 10 digits!")
    .onSubmit(result::set);

SelectorCell providerCell = new SelectorCell()
    .options("Turkcell", "Türk Telekom", "Vodafone Turkey")
    .onSubmit(selectedIndex::set)
    .setTitle(new TextTUI("Select your telecom provider:\n"));

SpinnerCell spinnerCell = new SpinnerCell()
    .arc()
    .status(new TextTUI("Generating keys...").setColor("#d164ff"))
    .updateStatus(new TextTUI("Keys secured! 🔑\n")
				    .setColor(Colors.setColor("#16c60c"))
	)
    .withTask(() -> Thread.sleep(5000));

Automation.create()
    .appendCell(phoneCell)
    .appendCell(providerCell)
    .appendCell(spinnerCell)
    .setBuffer(2)
    .setTitle(new TextTUI(" JTUI ").setBackgroundColor("#f44336"))
    .summary(
        new TextTUI(" Your Info. "),
        () -> new TextTUI("\nPhone: " + result.get() + "\nProvider: " + options[selectedIndex.get()] + "\n")
                  .setColor("#66b8f7")
    )
    .start();
```

**Available Cell types:**

| Cell                | Description                                                       |
| ------------------- | ----------------------------------------------------------------- |
| `TextCell`          | Displays a static text block                                      |
| `InputCell`         | Interactive text input with validators                            |
| `SelectorCell`      | Single-choice selector                                            |
| `MultiSelectorCell` | Multi-choice selector with pre-selection                          |
| `SpinnerCell`       | Animated spinner running an async task with more than 20 spinners |
| `ScannerCell`       | Vertical scanning animation to text with a task and status update |
| `BoxCell`	    	  | Renders text dynamically wrapped inside a customizable border     |



https://github.com/user-attachments/assets/05f9faf4-2830-490f-bfb7-d464830ebb09



---

## Design Patterns

JTUI was designed as a showcase of classical Gang-of-Four and architectural design patterns in Java:

| Pattern | Where Used |
|---|---|
| **Builder** | `TextTUI`, `BoxTUI`, `InputTUI`, `SelectorTUI`, `SmartTableTUI`, `BarTUI`, `QuoteTUI`, `Automation` — all expose fluent builder APIs |
| **Singleton** | `WindowsAPI` adds ANSI color and UTF-8 support for Windows machines |
| **Command** | `OnClick` — each key binding wraps a `Runnable` command dispatched on keypress |
| **Observer** | `onChange` / `onSubmit` callbacks in `InputCell`, `SelectorCell`, `MultiSelectorCell` |
| **Template Method** | `Cell` base type defines the step lifecycle; concrete cells override task logic |
| **Facade** | `Automation` facade orchestrates `AutomationRunner`, `AutomationContext`, and all cells behind a simple API |
| **Composite** | `Container` composes heterogeneous `Supplier<String>` blocks into a single renderable layout |
| **Decorator** | `TextTUI.appendText()` chains styled text fragments; `BadgeTUI.append()` chains badge segments |

---

## Project Structure

```
src/
├── App
├── Keyhandle
│   ├── KeyHandle
│   ├── KeyModifier
│   ├── KeyPress
│   └── OnClick
│
├── Text
│   ├── ImageTUI
│   ├── LogoTUI
│   └── fonts
│       ├── ANSICompact
│       ├── ANSIRegular
│       ├── ANSIShadow
│       ├── ANSISingle
│       ├── DOS Rebel
│       ├── Kban
│       ├── Rectangles
│       ├── Sub-Zero
│       ├── Terrace
│       └── pagga
│
├── automation
│   ├── Automation
│   ├── AutomationRunner
│   ├── BoxCell
│   ├── Cell
│   ├── InputCell
│   ├── MultiSelectorCell
│   ├── ScannerCell
│   ├── SelectorCell
│   ├── SpinnerCell
│   └── TextCell
│
├── inputForm
│   ├── BaseSelector
│   ├── Input
│   ├── InputType
│   ├── OptionSet
│   ├── SelectorTUI
│   ├── TUIComponent
│   ├── TUICursor
│   ├── boxBorder
│   └── boxShape
│
└── utils
    ├── ANSI
    ├── ANSIformat
    ├── AlignText
    ├── BadgeTUI
    ├── BarTUI
    ├── Colors
    ├── Component
    ├── Container
    ├── Gradient
    ├── Icons
    ├── QuoteTUI
    ├── SmartTableTUI
    ├── SmartTextTUI
    ├── Style
    ├── TableBox
    ├── TableTUI
    ├── Terminal
    │   ├── RawMode
    │   └── TerminalSize
    ├── TextTUI
    └── WindowsAPI
```

---

> Built with ❤️ in Java - no dependencies, just escape codes.
