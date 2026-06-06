package utils;
/*
reset:
JAVA: \u001B[0m
Bash: \e[0m
*/

public enum ANSI {
    Start("\u001B["),
    Reset("\u001B[0m");

    private String command;
    private ANSI(String command) {
        this.command = command;
    }
    @Override
    public String toString() {
        return this.command;
    }
}