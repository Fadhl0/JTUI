#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>

#ifdef _WIN32
    #include <windows.h>
#else
    #include <termios.h>
    #include <unistd.h>
#endif

#define SAVED_TERMIOS_PATH "/tmp/.rawmode_saved_termios"

void enableRawMode() {
#ifdef _WIN32
    HANDLE hInput = GetStdHandle(STD_INPUT_HANDLE);
    DWORD originalMode;
    if (!GetConsoleMode(hInput, &originalMode)) {
        fprintf(stderr, "Failed to get console mode\n");
        return;
    }

    FILE *f = fopen(SAVED_TERMIOS_PATH, "wb");
    if (f) { fwrite(&originalMode, sizeof(originalMode), 1, f); fclose(f); }

    DWORD raw = originalMode & ~(ENABLE_ECHO_INPUT | ENABLE_LINE_INPUT | ENABLE_PROCESSED_INPUT);
    if (!SetConsoleMode(hInput, raw)) {
        fprintf(stderr, "Failed to set raw console mode\n");
    }
#else
    // struct termios originalTermios;
    // if (tcgetattr(STDIN_FILENO, &originalTermios) == -1) {
    //     fprintf(stderr, "Failed to get terminal attributes\n");
    //     return;
    // }
    int fd = open("/dev/tty", O_RDWR); // ← always the real terminal
    if (fd == -1) { perror("open /dev/tty"); return; }

    struct termios originalTermios;
    if (tcgetattr(fd, &originalTermios) == -1) {
        perror("tcgetattr"); close(fd); return;
    }

    // Save original to file so -q can restore it later
    FILE *f = fopen(SAVED_TERMIOS_PATH, "wb");
    if (f) { fwrite(&originalTermios, sizeof(originalTermios), 1, f); fclose(f); }

    struct termios raw = originalTermios;
    raw.c_lflag &= ~(ECHO | ICANON | ISIG | IEXTEN);
    raw.c_iflag &= ~(IXON | ICRNL | BRKINT | INPCK | ISTRIP);
    // raw.c_cc[VMIN]  = 1;  // read blocks until at least 1 byte
    // raw.c_cc[VTIME] = 0;  // no timeout
    raw.c_cc[VMIN]  = 0;
    raw.c_cc[VTIME] = 1;

    // if (tcsetattr(STDIN_FILENO, TCSAFLUSH, &raw) == -1) {
    //     fprintf(stderr, "Failed to set raw terminal attributes\n");
    // }
    if (tcsetattr(fd, TCSAFLUSH, &raw) == -1) {
        perror("tcsetattr");
    }
    close(fd);
#endif
}

void disableRawMode() {
#ifdef _WIN32
    FILE *f = fopen(SAVED_TERMIOS_PATH, "rb");
    if (!f) { fprintf(stderr, "No saved console mode found\n"); return; }
    DWORD originalMode;
    fread(&originalMode, sizeof(originalMode), 1, f);
    fclose(f);
    remove(SAVED_TERMIOS_PATH);
    SetConsoleMode(GetStdHandle(STD_INPUT_HANDLE), originalMode);
#else
    FILE *f = fopen(SAVED_TERMIOS_PATH, "rb");
    if (!f) { fprintf(stderr, "No saved termios found\n"); return; }
    struct termios originalTermios;
    fread(&originalTermios, sizeof(originalTermios), 1, f);
    fclose(f);
    remove(SAVED_TERMIOS_PATH);

    // if (tcsetattr(STDIN_FILENO, TCSAFLUSH, &originalTermios) == -1) {
    //     fprintf(stderr, "Failed to restore terminal attributes\n");
    // }
    int fd = open("/dev/tty", O_RDWR);
    if (fd == -1) { perror("open /dev/tty"); return; }
    tcsetattr(fd, TCSAFLUSH, &originalTermios);
    close(fd);
#endif
}

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s [-s|-q]\n", argv[0]);
        return 1;
    }
    if (strcmp(argv[1], "-s") == 0) {
        enableRawMode();
    } else if (strcmp(argv[1], "-q") == 0) {
        disableRawMode();
    }
    return 0;
}