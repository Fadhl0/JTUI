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
    HANDLE h = GetStdHandle(STD_OUTPUT_HANDLE);
    if (GetConsoleScreenBufferInfo(h, &csbi)) {
        printf("%d %d\n",
            csbi.srWindow.Bottom - csbi.srWindow.Top  + 1,
            csbi.srWindow.Right  - csbi.srWindow.Left + 1);
    } else {
        printf("24 80\n");
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
