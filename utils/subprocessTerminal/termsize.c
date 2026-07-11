#include <stdio.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

int main() {
    int fd = open("/dev/tty", O_RDONLY);
    if (fd < 0) {
        // Prints why it failed — e.g. "No such device or address"
        fprintf(stderr, "open /dev/tty failed: %s\n", strerror(errno));
        printf("24 80\n");
        return 1;
    }

    struct winsize w;
    if (ioctl(fd, TIOCGWINSZ, &w) < 0) {
        fprintf(stderr, "ioctl failed: %s\n", strerror(errno));
        close(fd);
        printf("24 80\n");
        return 1;
    }

    close(fd);

    if (w.ws_col == 0 || w.ws_row == 0) {
        fprintf(stderr, "ioctl succeeded but returned zero — not a real TTY\n");
        printf("24 80\n");
        return 1;
    }

    printf("%d %d\n", w.ws_row, w.ws_col);
    return 0;
}