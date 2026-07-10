package utils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Desktop;

public class Component {
    public final static String ANSI_PATTERN = "\u001B\\[[;\\d]*m";
    private static boolean isLive = false;
    private static int getTerminalWidth = 0;

    @Deprecated
    public static void setLive(boolean enable) {
        isLive = enable;
    }

    @Deprecated
    public static boolean getLive() {
        return isLive;
    }
    @Deprecated
    public static int getTerminalWidth() {
        return getTerminalWidth;
    }
    @Deprecated
    public static void setTerminalWidth(int width) {
        getTerminalWidth = width;
    }

    public static void clear() {
        try {
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    private static Path getBinaryPath(String name) {
        WindowsAPI.apply();

        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        boolean isWindows = os.contains("win");
        String binaryName = isWindows ? name + ".exe" : name;

        Path resolved = null;

        try {
            java.net.URL resource = Component.class.getResource("subprocessTerminal/" + binaryName);
            if (resource != null) {
                resolved = Path.of(resource.toURI()).normalize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resolved == null) {
            resolved = Path.of("utils", "subprocessTerminal", binaryName).toAbsolutePath();
        }

        resolved.toFile().setExecutable(true);   // always called now
        return resolved;
    }

    public static int[] getTerminalSize() {
        try {
            Process p = new ProcessBuilder(getBinaryPath("termsize").toString()).start();

            String err = new String(p.getErrorStream().readAllBytes()).trim();
            String out = new String(p.getInputStream().readAllBytes()).trim();
            p.waitFor();

            if (!err.isEmpty()) {
                System.err.println("[termsize] " + err);
            }

            String[] parts = out.split(" ");
            return new int[]{
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
            };
        } catch (Exception e) {
            return new int[]{24, 80};
        }
    }

    public static void enableRawMode() {
        if (isLive) return;

        try {
            int exitCode = new ProcessBuilder(getBinaryPath("rowmode").toString(), "-s")
                .inheritIO()
                .start()
                .waitFor();

            isLive = true;
            
            if (exitCode != 0) {
                System.err.println("Row mode enable failed with exit code: " + exitCode);
            }

        } catch (IOException e) {
            System.err.println("Failed to launch rawmode binary: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Row mode enable interrupted: " + e.getMessage());
        }
    }

    public static void disableRawMode() {
        if (!isLive) return;

        try {
            int exitCode = new ProcessBuilder(getBinaryPath("rowmode").toString(), "-q")
                .inheritIO()
                .start()
                .waitFor();
            
            isLive = false;

            if (exitCode != 0) {
                System.err.println("Row mode disable failed with exit code: " + exitCode);
            }

        } catch (IOException e) {
            System.err.println("Failed to launch rawmode binary: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Row mode disable interrupted: " + e.getMessage());
        }
    }

    public static int visibleLength(String s) {
        String text = removeStyle(s);
        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(text);
        
        int visualCharacterCount = 0;
        while (iterator.next() != BreakIterator.DONE) {
            visualCharacterCount++;
        }
        return visualCharacterCount;
    }
    public static int visibleLengthNoBreak(String s) {
        String[] ss = s.split("\n", -1);
        int max = 0;
        for (int i = 0; i < ss.length; i++) {
            max = Math.max(max, visibleLength(ss[i]));
        }
        return max;
    }

    public static String visibleSubstring(String s, int start, int end) {
        Pattern ansiRegex = Pattern.compile(ANSI_PATTERN);
        Matcher matcher = ansiRegex.matcher(s);

        StringBuilder result = new StringBuilder();
        // boolean hadAnsi = false;
        int visibleIdx = 0;
        int rawIdx = 0;

        while (rawIdx < s.length() && visibleIdx < end) {
            if (matcher.find(rawIdx) && matcher.start() == rawIdx) {
                if (visibleIdx >= start) {
                    result.append(s, matcher.start(), matcher.end());
                    // hadAnsi = true;
                }
                rawIdx = matcher.end();
            } else {
                if (visibleIdx >= start) {
                    result.append(s.charAt(rawIdx));
                }
                visibleIdx++;
                rawIdx++;
            }
        }

        // if (hadAnsi) {
        //     result.append(ANSI.Reset);
        // }

        return result.toString();
    }

    public static String visibleText(String s) {
        String text = removeStyle(s);
        return text;
    }

    public static String removeStyle(String s) {
        return Pattern.compile(ANSI_PATTERN).matcher(s).replaceAll("");
    }

    public static void redirect(String url) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        }
    }
    public static void redirect(Path file) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(file.toUri()));
        }
    }

}
