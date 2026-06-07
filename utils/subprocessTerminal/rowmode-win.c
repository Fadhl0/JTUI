#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>

static char savedModePath[MAX_PATH];

static void buildSavedModePath() {
    char tempDir[MAX_PATH];
    if (GetTempPath(MAX_PATH, tempDir) == 0) {
        strcpy(savedModePath, ".rawmode_saved_console");
    } else {
        snprintf(savedModePath, MAX_PATH, "%s.rawmode_saved_console", tempDir);
    }
}

// Always open the real console device, not stdin (which may be a pipe)
static HANDLE openConsole() {
    return CreateFile(
        "CONIN$",
        GENERIC_READ | GENERIC_WRITE,
        FILE_SHARE_READ | FILE_SHARE_WRITE,
        NULL,
        OPEN_EXISTING,
        0,
        NULL
    );
}

void enableRawMode() {
    buildSavedModePath();

    HANDLE hInput = openConsole();
    if (hInput == INVALID_HANDLE_VALUE) return;

    DWORD originalMode;
    if (!GetConsoleMode(hInput, &originalMode)) {
        CloseHandle(hInput);
        return;
    }

    // Save original
    FILE *f = fopen(savedModePath, "wb");
    if (f) { fwrite(&originalMode, sizeof(originalMode), 1, f); fclose(f); }

    DWORD raw = originalMode
        & ~(ENABLE_ECHO_INPUT | ENABLE_LINE_INPUT | ENABLE_PROCESSED_INPUT)
        |   ENABLE_VIRTUAL_TERMINAL_INPUT;  // <-- THIS makes Windows emit ANSI sequences
    
    SetConsoleMode(hInput, raw);
    CloseHandle(hInput);
}

void disableRawMode() {
    buildSavedModePath();

    FILE *f = fopen(savedModePath, "rb");
    if (!f) {
        fprintf(stderr, "No saved console mode found at: %s\n", savedModePath);
        return;
    }

    DWORD originalMode;
    fread(&originalMode, sizeof(originalMode), 1, f);
    fclose(f);
    remove(savedModePath);

    HANDLE hInput = openConsole();
    if (hInput == INVALID_HANDLE_VALUE) {
        fprintf(stderr, "Failed to open CONIN$ for restore: %lu\n", GetLastError());
        return;
    }

    SetConsoleMode(hInput, originalMode);
    CloseHandle(hInput);
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