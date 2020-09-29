package app;

public enum Commands {
    MOUSE_DOWN(-1),
    MOUSE_UP(-2),
    KEY_DOWN(-3),
    KEY_UP(-4),
    MOVE_MOUSE(-5);

    private int abbrev;

    Commands(int abbrev) {
        this.abbrev = abbrev;
    }

    public int getAbbrev() {
        return abbrev;
    }
}
