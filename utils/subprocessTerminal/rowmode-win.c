#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>

static char savedModePath[MAX_PATH];

static void buildSavedModePath() {
    char tempDir[MAX_PATH];
    if (GetTempPath(MAX_PATH, tempDir) == 0) {
        // Fallback to cwd if GetTempPath fails
        strcpy(savedModePath, ".rawmode_saved_console");
    } else {
        // GetTempPath returns a path that already ends with a backslash
        snprintf(savedModePath, MAX_PATH, "%s.rawmode_saved_console", tempDir);
    }
}

void enableRawMode() {
    buildSavedModePath();

    HANDLE hInput = GetStdHandle(STD_INPUT_HANDLE);
    DWORD originalMode;
    if (!GetConsoleMode(hInput, &originalMode)) {
        fprintf(stderr, "Failed to get console mode\n");
        return;
    }

    FILE *f = fopen(savedModePath, "wb");
    if (f) {
        fwrite(&originalMode, sizeof(originalMode), 1, f);
        fclose(f);
    }

    DWORD raw = originalMode & ~(ENABLE_ECHO_INPUT | ENABLE_LINE_INPUT | ENABLE_PROCESSED_INPUT);
    if (!SetConsoleMode(hInput, raw)) {
        fprintf(stderr, "Failed to set raw console mode\n");
    }
}

void disableRawMode() {
    buildSavedModePath();

    FILE *f = fopen(savedModePath, "rb");
    if (!f) {
        fprintf(stderr, "No saved console mode found\n");
        return;
    }

    DWORD originalMode;
    fread(&originalMode, sizeof(originalMode), 1, f);
    fclose(f);
    remove(savedModePath);

    SetConsoleMode(GetStdHandle(STD_INPUT_HANDLE), originalMode);
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

