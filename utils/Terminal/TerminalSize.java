package utils.Terminal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

/**
 * Native terminal size detection via the Java FFM API (JDK 22+, finalized).
 * Replaces termsize.c (Linux ioctl/TIOCGWINSZ) and termsize-win2.c
 * (Windows GetConsoleScreenBufferInfo) — no external process, no C binary.
 * <li> caution: Cloude Sonnet 5 made this.</li>
 */
public final class TerminalSize {

    public record Size(int rows, int cols) {}

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name", "").toLowerCase().contains("win");

    public static Size get() {
        try {
            return IS_WINDOWS ? getWindows() : getLinux();
        } catch (Throwable t) {
            // Same fallback your C programs printed on failure.
            System.err.println("Terminal size detection failed: " + t.getMessage());
            return new Size(24, 80);
        }
    }

    // ---------------------------------------------------------------
    // Linux: open("/dev/tty", O_RDONLY) -> ioctl(fd, TIOCGWINSZ, &w) -> close(fd)
    // ---------------------------------------------------------------
    private static Size getLinux() throws Throwable {
        Linker linker = Linker.nativeLinker();
        SymbolLookup libc = linker.defaultLookup();

        // struct winsize { unsigned short ws_row, ws_col, ws_xpixel, ws_ypixel; };
        StructLayout winsizeLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_SHORT.withName("ws_row"),
                ValueLayout.JAVA_SHORT.withName("ws_col"),
                ValueLayout.JAVA_SHORT.withName("ws_xpixel"),
                ValueLayout.JAVA_SHORT.withName("ws_ypixel")
        );
        VarHandle rowH = winsizeLayout.varHandle(MemoryLayout.PathElement.groupElement("ws_row"));
        VarHandle colH = winsizeLayout.varHandle(MemoryLayout.PathElement.groupElement("ws_col"));

        // Capture errno the same way strerror(errno) would report it.
        Linker.Option ccs = Linker.Option.captureCallState("errno");
        StructLayout capturedStateLayout = Linker.Option.captureStateLayout();
        VarHandle errnoH = capturedStateLayout.varHandle(
                MemoryLayout.PathElement.groupElement("errno"));

        MethodHandle open = linker.downcallHandle(
                libc.find("open").orElseThrow(() -> new RuntimeException("open not found")),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
                ccs);

        MethodHandle ioctl = linker.downcallHandle(
                libc.find("ioctl").orElseThrow(() -> new RuntimeException("ioctl not found")),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
                ccs);

        MethodHandle close = linker.downcallHandle(
                libc.find("close").orElseThrow(() -> new RuntimeException("close not found")),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

        final int O_RDONLY = 0;
        final long TIOCGWINSZ = 0x5413L; // Linux value (differs on macOS/BSD)

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment path = arena.allocateFrom("/dev/tty");
            MemorySegment callState = arena.allocate(capturedStateLayout);

            int fd = (int) open.invoke(callState, path, O_RDONLY);
            if (fd < 0) {
                int errno = (int) errnoH.get(callState, 0L);
                throw new RuntimeException("open /dev/tty failed, errno=" + errno);
            }

            MemorySegment winsize = arena.allocate(winsizeLayout);
            int rc = (int) ioctl.invoke(callState, fd, TIOCGWINSZ, winsize);
            int errno = (int) errnoH.get(callState, 0L);
            close.invoke(fd);

            if (rc < 0) {
                throw new RuntimeException("ioctl failed, errno=" + errno);
            }

            short rows = (short) rowH.get(winsize, 0L);
            short cols = (short) colH.get(winsize, 0L);
            if (rows == 0 || cols == 0) {
                throw new RuntimeException("ioctl returned zero — not a real TTY");
            }
            return new Size(rows, cols);
        }
    }

    // ---------------------------------------------------------------
    // Windows: CreateFileW("CONOUT$") -> GetConsoleScreenBufferInfo -> CloseHandle
    // ---------------------------------------------------------------
    private static Size getWindows() throws Throwable {
        Linker linker = Linker.nativeLinker();

        try (Arena arena = Arena.ofConfined()) {
            SymbolLookup kernel32 = SymbolLookup.libraryLookup("Kernel32.dll", arena);

            // CONSOLE_SCREEN_BUFFER_INFO:
            //   COORD dwSize;              (SHORT X, SHORT Y)      -> 4 bytes
            //   COORD dwCursorPosition;    (SHORT X, SHORT Y)      -> 4 bytes
            //   WORD  wAttributes;                                  -> 2 bytes
            //   SMALL_RECT srWindow;       (SHORT L,T,R,B)         -> 8 bytes
            //   COORD dwMaximumWindowSize; (SHORT X, SHORT Y)      -> 4 bytes
            // All fields are 2-byte SHORTs, naturally packed, total 22 bytes.
            StructLayout csbiLayout = MemoryLayout.structLayout(
                    ValueLayout.JAVA_SHORT.withName("dwSize_X"),
                    ValueLayout.JAVA_SHORT.withName("dwSize_Y"),
                    ValueLayout.JAVA_SHORT.withName("dwCursorPosition_X"),
                    ValueLayout.JAVA_SHORT.withName("dwCursorPosition_Y"),
                    ValueLayout.JAVA_SHORT.withName("wAttributes"),
                    ValueLayout.JAVA_SHORT.withName("srWindow_Left"),
                    ValueLayout.JAVA_SHORT.withName("srWindow_Top"),
                    ValueLayout.JAVA_SHORT.withName("srWindow_Right"),
                    ValueLayout.JAVA_SHORT.withName("srWindow_Bottom"),
                    ValueLayout.JAVA_SHORT.withName("dwMaximumWindowSize_X"),
                    ValueLayout.JAVA_SHORT.withName("dwMaximumWindowSize_Y")
            );
            VarHandle leftH = csbiLayout.varHandle(MemoryLayout.PathElement.groupElement("srWindow_Left"));
            VarHandle topH = csbiLayout.varHandle(MemoryLayout.PathElement.groupElement("srWindow_Top"));
            VarHandle rightH = csbiLayout.varHandle(MemoryLayout.PathElement.groupElement("srWindow_Right"));
            VarHandle bottomH = csbiLayout.varHandle(MemoryLayout.PathElement.groupElement("srWindow_Bottom"));

            Linker.Option ccs = Linker.Option.captureCallState("GetLastError");
            StructLayout capturedStateLayout = Linker.Option.captureStateLayout();
            VarHandle lastErrorH = capturedStateLayout.varHandle(
                    MemoryLayout.PathElement.groupElement("GetLastError"));

            // HANDLE CreateFileW(LPCWSTR, DWORD, DWORD, LPSECURITY_ATTRIBUTES, DWORD, DWORD, HANDLE)
            MethodHandle createFileW = linker.downcallHandle(
                    kernel32.find("CreateFileW").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS),
                    ccs);

            // BOOL GetConsoleScreenBufferInfo(HANDLE, PCONSOLE_SCREEN_BUFFER_INFO)
            MethodHandle getCsbi = linker.downcallHandle(
                    kernel32.find("GetConsoleScreenBufferInfo").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
                    ccs);

            // BOOL CloseHandle(HANDLE)
            MethodHandle closeHandle = linker.downcallHandle(
                    kernel32.find("CloseHandle").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

            final int GENERIC_READ = 0x80000000;
            final int GENERIC_WRITE = 0x40000000;
            final int FILE_SHARE_READ = 0x00000001;
            final int FILE_SHARE_WRITE = 0x00000002;
            final int OPEN_EXISTING = 3;
            final MemorySegment INVALID_HANDLE_VALUE = MemorySegment.ofAddress(-1L);

            // Windows wants UTF-16LE, not UTF-8.
            MemorySegment wname = toWideString(arena, "CONOUT$");

            MemorySegment callState = arena.allocate(capturedStateLayout);

            MemorySegment hOut = (MemorySegment) createFileW.invoke(
                    callState, wname,
                    GENERIC_READ | GENERIC_WRITE,
                    FILE_SHARE_READ | FILE_SHARE_WRITE,
                    MemorySegment.NULL, OPEN_EXISTING, 0, MemorySegment.NULL);

            if (hOut.address() == INVALID_HANDLE_VALUE.address()) {
                int err = (int) lastErrorH.get(callState, 0L);
                throw new RuntimeException("CreateFileW(CONOUT$) failed, GetLastError=" + err);
            }

            try {
                MemorySegment csbi = arena.allocate(csbiLayout);
                int ok = (int) getCsbi.invoke(callState, hOut, csbi);
                if (ok == 0) {
                    int err = (int) lastErrorH.get(callState, 0L);
                    throw new RuntimeException("GetConsoleScreenBufferInfo failed, GetLastError=" + err);
                }

                short left = (short) leftH.get(csbi, 0L);
                short top = (short) topH.get(csbi, 0L);
                short right = (short) rightH.get(csbi, 0L);
                short bottom = (short) bottomH.get(csbi, 0L);

                int cols = right - left + 1;
                int rows = bottom - top + 1;
                return new Size(rows, cols);
            } finally {
                closeHandle.invoke(hOut);
            }
        }
    }

    /** Allocates a native UTF-16LE, null-terminated wide string ("LPCWSTR"). */
    private static MemorySegment toWideString(Arena arena, String s) {
        MemorySegment seg = arena.allocate((s.length() + 1) * 2L);
        for (int i = 0; i < s.length(); i++) {
            seg.setAtIndex(ValueLayout.JAVA_CHAR, i, s.charAt(i));
        }
        seg.setAtIndex(ValueLayout.JAVA_CHAR, s.length(), '\0');
        return seg;
    }
}
