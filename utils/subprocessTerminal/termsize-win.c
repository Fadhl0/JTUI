#include <stdio.h>

#ifdef _WIN32
    #include <windows.h>
#else
    #include <sys/ioctl.h>
    #include <fcntl.h>
    #include <unistd.h>
#endif

int main() {
#ifdef _WIN32
    CONSOLE_SCREEN_BUFFER_INFO csbi;
    // Open CONOUT$ directly to get console info even if stdout is redirected
    HANDLE h = CreateFileA("CONOUT$", GENERIC_READ | GENERIC_WRITE, 
                           FILE_SHARE_READ | FILE_SHARE_WRITE, 
                           NULL, OPEN_EXISTING, 0, NULL);
    
    if (h != INVALID_HANDLE_VALUE && GetConsoleScreenBufferInfo(h, &csbi)) {
        printf("%d %d\n",
            csbi.srWindow.Bottom - csbi.srWindow.Top  + 1,
            csbi.srWindow.Right  - csbi.srWindow.Left + 1);
        CloseHandle(h);
    } else {
        // Fallback if not running in a native Windows console (like Git Bash)
        printf("24 80\n");
        if (h != INVALID_HANDLE_VALUE) CloseHandle(h);
    }
#else
    int fd = open("/dev/tty", O_RDONLY);
    if (fd < 0) { printf("24 80\n"); return 1; }
    struct winsize w;
    ioctl(fd, TIOCGWINSZ, &w);
    close(fd);
    printf("%d %d\n", w.ws_row, w.ws_col);
#endif
    return 0;
}