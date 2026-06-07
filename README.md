# JTUI — Java Terminal UI Framework

> A zero-dependency Java library for building rich, interactive terminal user interfaces.

<img width="400" height="400" alt="1-1" src="https://github.com/user-attachments/assets/ca291c1c-dcb7-425c-b32b-ee02a019ce1f" />


---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Components](#components)
  - [TextTUI](#texttui)
  - [LogoTUI](#logotui)
  - [Gradient](#gradient)
  - [ImageTUI](#imagetui)
  - [BoxTUI](#boxtui)
  - [TableBox](#tablebox)
  - [TableTUI](#tabletui)
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

---

## Overview

**JTUI** is a pure-Java terminal UI framework built from scratch with **zero external dependencies**. It provides a rich set of components, from styled text and ASCII art logos to interactive inputs, selectors, progress bars, and full layout containers, all driven by ANSI escape codes.

Originally developed as a Java design patterns course project.
Not all methods of these classes mentioned in this readme, it is recommend to explore it more in original code.

**Disclaimer**: Automatic terminal resizing is not available on Windows systems at this time.
---

## Features

- **True-color ANSI styling** — foreground, background, bold, italic, underline, strikethrough, invert
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

JTUI has **no external dependencies**. Clone the repository and compile with any standard Java toolchain (Java 11+).

```bash
git clone https://github.com/Fadhl0/JTUI.git
cd JTUI
javac -d bin App.java
java -cp bin App
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

### Colors
Set of best colors for UI design.
#### All Primary Colors:
<img width="834" height="497" alt="primary colors" src="https://github.com/user-attachments/assets/2fe1cdc3-d8bf-4116-8127-cb0ab13fd6a0" />
<br>

#### All Secondary Colors:

<img width="1403" height="820" alt="secondary colors" src="https://github.com/user-attachments/assets/2017142f-9233-4f7d-8f8a-6a3f9e40fb71" />
<img width="860" height="780" alt="secondary colors-1" src="https://github.com/user-attachments/assets/23858d31-a559-41f4-86d5-314d67cc12a8" />



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
        .label(new TextTUI(" Second Box "))
        .innerText(new TextTUI("\n  Content here \n"))
);
```

<img width="911" height="155" alt="Pasted image 20260606130809" src="https://github.com/user-attachments/assets/dd1f5f6a-00ec-4a65-8d68-f2868c55069b" />


---

### TableTUI

A simple columnar table for structured data.

```java
TableTUI table = new TableTUI();
table.addRow(new TextTUI("Student Name").bold(), new TextTUI("Student Number").bold());
table.addRow(new TextTUI("Fadhl Al Fadhili"), new TextTUI("2363825"));
System.out.println(table);
```

Column widths are distributed proportionally using the **largest-remainder method**. ANSI escape codes are stripped from length calculations so styled content is always aligned correctly.

<img width="456" height="67" alt="Pasted image 20260606130925" src="https://github.com/user-attachments/assets/617d3ace-805e-4fca-bda9-651dfe7846dd" />


---

### InputTUI

An interactive text input field with label, placeholder, icon, border color, and chainable validators.

```java
InputTUI input = new InputTUI()
                     .label(new TextTUI(" Enter Your Email ").setColor(Colors.Green600))
                     .placeholder(new TextTUI("example@company.com").setColor(Colors.Gray500))
                     .setIcon(new TextTUI(" > ").bold())
                     .setBorderColor("#8aff0c")
                     .validator(t -> !t.isEmpty(), new TextTUI("⚠  Empty Field!"))
                     .validator(
                         t -> t.contains("@") && t.contains("."),
                         new TextTUI("⚠  Invalid Email!")
                     );

String result = input.build().prompt();
```

<img width="400" height="146" alt="2-1" src="https://github.com/user-attachments/assets/7e8ef33c-91db-43bc-89c8-f093665905f1" />


---

### SelectorTUI

An interactive single-choice selector navigated with arrow keys and confirmed with Enter.

```java
SelectorTUI selector = new SelectorTUI()
                           .setTitle(new TextTUI("What is your favorite color?"))
                           .clearAfterSubmit()
                           .options("Red", "Orange", "Green", "Blue", "Pink", "Other");

int selectedIndex = selector.prompt();
```

Returns the zero-based index of the chosen option. Pass `.clearAfterSubmit()` to wipe the selector from the screen after selection.

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
Container c1 = new Container();

c1.append(title::toString);
c1.append(() -> "\n");
c1.append(table1::toString, Alignment.INLINE_CENTER);
c1.append(inputContainer::prompt);
c1.append(tableBox::toString);

c1.execute();
// ... interactive phase
c1.stop();
```

`Container` uses the **alternate screen buffer** (`\033[?1049h`) so the main scrollback is preserved. Dirty-flag diffing compares lines before repainting, ensuring only changed rows are redrawn.



https://github.com/user-attachments/assets/7d3f2a75-0ff5-4f30-afe1-88657b578f98



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



https://github.com/user-attachments/assets/05f9faf4-2830-490f-bfb7-d464830ebb09



---

## Design Patterns

JTUI was designed as a showcase of classical Gang-of-Four and architectural design patterns in Java:

| Pattern | Where Used |
|---|---|
| **Builder** | `TextTUI`, `BoxTUI`, `InputTUI`, `SelectorTUI`, `BarTUI`, `QuoteTUI`, `Automation` — all expose fluent builder APIs |
| **Strategy** | Border rendering in `BoxTUI` via `boxShape`; alignment strategy in `Container` via `Alignment` enum |
| **Command** | `OnClick` — each key binding wraps a `Runnable` command dispatched on keypress |
| **Observer** | `onChange` / `onSubmit` callbacks in `InputCell`, `SelectorCell`, `MultiSelectorCell` |
| **Template Method** | `Cell` base type defines the step lifecycle; concrete cells override task logic |
| **Facade** | `Automation` facade orchestrates `AutomationRunner`, `AutomationContext`, and all cells behind a simple API |
| **Composite** | `Container` composes heterogeneous `Supplier<String>` blocks into a single renderable layout |
| **Decorator** | `TextTUI.appendText()` chains styled text fragments; `BadgeTUI.append()` chains badge segments |

---

## Project Structure ()

```
src/
├── App.java                  # Demo entry point
├── automation/               # Automation wizard engine
│   ├── Automation.java
│   ├── Cell.java
│   ├── InputCell.java
│   ├── SelectorCell.java
│   ├── MultiSelectorCell.java
│   ├── SpinnerCell.java
│   ├── ScannerCell.java
│   └── TextCell.java
├── inputForm/                # Interactive input components
│   ├── inputTUI/InputTUI.java
│   ├── InputContainer/Input.java
│   ├── InputType.java
│   ├── SelectorTUI.java
│   ├── boxBorder/BoxTUI.java
│   └── boxShape.java
├── Keyhandle/                # Raw mode key event system
│   ├── OnClick.java
│   ├── KeyPress.java
│   ├── KeyHandle.java
│   └── KeyModifier.java
├── Text/                     # ASCII art and image rendering
│   ├── LogoTUI.java
│   └── ImageTUI.java
└── utils/                    # Core utilities
    ├── TextTUI.java
    ├── Colors.java
    ├── Component.java
    ├── Container.java
    ├── Gradient.java
    ├── AlignText.java
    ├── BadgeTUI.java
    ├── BarTUI.java
    ├── QuoteTUI.java
    ├── TableTUI.java
    ├── TableBox.java
    └── Icons.java
```

---

> Built with ❤️ in Java - no dependencies, just escape codes.
