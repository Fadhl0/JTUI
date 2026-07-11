package utils.Terminal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

/**
 * Raw terminal mode via direct FFM calls into libc (Linux/glibc-style)
 * or kernel32.dll (Windows). No custom .so/.dll to build or ship.
 *
 * NOTE: the struct termios layout below matches glibc on Linux x86_64
 * (sizeof == 60). macOS's termios layout differs (no c_line field,
 * NCCS == 20) — if you need Mac support, say so and I'll add a second
 * layout switched on os.name.
 * <li> caution: Cloude Sonnet 5 made this.</li>
 */
public final class RawMode {
    private static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;
    private static final Linker LINKER = Linker.nativeLinker();
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS.contains("win");

    private RawMode() {}

    public static void enable() {
        try {
            if (IS_WINDOWS) enableWindows(); else enableUnix();
        } catch (Throwable t) {
            throw new RuntimeException("enableRawMode failed", t);
        }
    }

    public static void disable() {
        try {
            if (IS_WINDOWS) disableWindows(); else disableUnix();
        } catch (Throwable t) {
            throw new RuntimeException("disableRawMode failed", t);
        }
    }

    // =========================================================
    // Linux / macOS — libc, via Linker.defaultLookup() (already loaded)
    // =========================================================

    private static final SymbolLookup LIBC = LINKER.defaultLookup();

    private static final MethodHandle OPEN = LINKER.downcallHandle(
            LIBC.find("open").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
    private static final MethodHandle CLOSE = LINKER.downcallHandle(
            LIBC.find("close").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
    private static final MethodHandle TCGETATTR = LINKER.downcallHandle(
            LIBC.find("tcgetattr").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
    private static final MethodHandle TCSETATTR = LINKER.downcallHandle(
            LIBC.find("tcsetattr").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

    private static final int NCCS = 32; // glibc Linux
    private static final MemoryLayout TERMIOS_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("c_iflag"),
            ValueLayout.JAVA_INT.withName("c_oflag"),
            ValueLayout.JAVA_INT.withName("c_cflag"),
            ValueLayout.JAVA_INT.withName("c_lflag"),
            ValueLayout.JAVA_BYTE.withName("c_line"),
            MemoryLayout.sequenceLayout(NCCS, ValueLayout.JAVA_BYTE).withName("c_cc"),
            MemoryLayout.paddingLayout(3), // align c_ispeed to 4 bytes
            ValueLayout.JAVA_INT.withName("c_ispeed"),
            ValueLayout.JAVA_INT.withName("c_ospeed"));

    private static final long OFF_IFLAG = TERMIOS_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("c_iflag"));
    private static final long OFF_LFLAG = TERMIOS_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("c_lflag"));
    private static final long OFF_CC = TERMIOS_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("c_cc"));

    private static final int ECHO = 0000010, ICANON = 0000002, ISIG = 0000001, IEXTEN = 0100000;
    private static final int IXON = 0002000, ICRNL = 0000400, BRKINT = 0000002, INPCK = 0000020, ISTRIP = 0000040;
    private static final int VTIME_IDX = 5, VMIN_IDX = 6;
    private static final int TCSAFLUSH = 2;
    private static final int O_RDWR = 2;

    private static Arena unixArena;      // holds the saved-state segment across enable()/disable()
    private static MemorySegment savedTermios;

    private static void enableUnix() throws Throwable {
        unixArena = Arena.ofShared();
        MemorySegment path = unixArena.allocateFrom("/dev/tty");
        int fd = (int) OPEN.invokeExact(path, O_RDWR);
        if (fd == -1) throw new RuntimeException("open /dev/tty failed");

        try {
            MemorySegment cur = unixArena.allocate(TERMIOS_LAYOUT);
            if ((int) TCGETATTR.invokeExact(fd, cur) != 0) throw new RuntimeException("tcgetattr failed");

            savedTermios = unixArena.allocate(TERMIOS_LAYOUT);
            MemorySegment.copy(cur, 0, savedTermios, 0, TERMIOS_LAYOUT.byteSize());

            int iflag = cur.get(ValueLayout.JAVA_INT, OFF_IFLAG);
            int lflag = cur.get(ValueLayout.JAVA_INT, OFF_LFLAG);
            cur.set(ValueLayout.JAVA_INT, OFF_LFLAG, lflag & ~(ECHO | ICANON | ISIG | IEXTEN));
            cur.set(ValueLayout.JAVA_INT, OFF_IFLAG, iflag & ~(IXON | ICRNL | BRKINT | INPCK | ISTRIP));
            cur.set(ValueLayout.JAVA_BYTE, OFF_CC + VMIN_IDX, (byte) 0);
            cur.set(ValueLayout.JAVA_BYTE, OFF_CC + VTIME_IDX, (byte) 1);

            if ((int) TCSETATTR.invokeExact(fd, TCSAFLUSH, cur) != 0) throw new RuntimeException("tcsetattr failed");
        } finally {
            int ignored = (int) CLOSE.invokeExact(fd); // always runs, mirrors the C fallthrough
        }
    }

    private static void disableUnix() throws Throwable {
        if (savedTermios == null) return;
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment path = arena.allocateFrom("/dev/tty");
            int fd = (int) OPEN.invokeExact(path, O_RDWR);
            if (fd == -1) throw new RuntimeException("open /dev/tty failed");
            int r1 = (int) TCSETATTR.invokeExact(fd, TCSAFLUSH, savedTermios);
            int r2 = (int) CLOSE.invokeExact(fd);
        } finally {
            unixArena.close();
            savedTermios = null;
        }
    }

    // =========================================================
    // Windows — kernel32.dll (always present, no build step)
    // =========================================================

    private static final SymbolLookup KERNEL32 =
            IS_WINDOWS ? SymbolLookup.libraryLookup("kernel32", Arena.global()) : null;

    private static final MethodHandle CREATE_FILE_W = IS_WINDOWS ? LINKER.downcallHandle(
            KERNEL32.find("CreateFileW").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT, ValueLayout.ADDRESS)) : null;
    private static final MethodHandle GET_CONSOLE_MODE = IS_WINDOWS ? LINKER.downcallHandle(
            KERNEL32.find("GetConsoleMode").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS)) : null;
    private static final MethodHandle SET_CONSOLE_MODE = IS_WINDOWS ? LINKER.downcallHandle(
            KERNEL32.find("SetConsoleMode").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)) : null;
    private static final MethodHandle CLOSE_HANDLE = IS_WINDOWS ? LINKER.downcallHandle(
            KERNEL32.find("CloseHandle").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)) : null;

    private static final int GENERIC_READ = 0x80000000, GENERIC_WRITE = 0x40000000;
    private static final int FILE_SHARE_READ = 1, FILE_SHARE_WRITE = 2, OPEN_EXISTING = 3;
    private static final int ENABLE_ECHO_INPUT = 0x0004, ENABLE_LINE_INPUT = 0x0002, ENABLE_PROCESSED_INPUT = 0x0001;

    private static int savedWinMode;

    private static MemorySegment openConin(Arena arena) throws Throwable {
        MemorySegment name = arena.allocateFrom("CONIN$", StandardCharsets.UTF_16LE);
        MemorySegment handle = (MemorySegment) CREATE_FILE_W.invokeExact(
                name, GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE,
                MemorySegment.NULL, OPEN_EXISTING, 0, MemorySegment.NULL);
        if (handle.address() == -1L) throw new RuntimeException("CreateFileW(CONIN$) failed"); // INVALID_HANDLE_VALUE
        return handle;
    }

    private static void enableWindows() throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment handle = openConin(arena);
            MemorySegment modeOut = arena.allocate(ValueLayout.JAVA_INT);
            if ((int) GET_CONSOLE_MODE.invokeExact(handle, modeOut) == 0)
                throw new RuntimeException("GetConsoleMode failed");
            savedWinMode = modeOut.get(ValueLayout.JAVA_INT, 0);

            int raw = (savedWinMode & ~(ENABLE_ECHO_INPUT | ENABLE_LINE_INPUT | ENABLE_PROCESSED_INPUT))
                    | ENABLE_VIRTUAL_TERMINAL_INPUT; // from issue 1's fix
            if ((int) SET_CONSOLE_MODE.invokeExact(handle, raw) == 0)
                throw new RuntimeException("SetConsoleMode failed");
            int ignoredClose = (int) CLOSE_HANDLE.invokeExact(handle);
        }
    }

    private static void disableWindows() throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment handle = openConin(arena);
            if ((int) SET_CONSOLE_MODE.invokeExact(handle, savedWinMode) == 0)
                throw new RuntimeException("SetConsoleMode restore failed");
            int ignoredClose = (int) CLOSE_HANDLE.invokeExact(handle);
        }
    }
}