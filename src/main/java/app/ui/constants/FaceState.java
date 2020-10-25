package app.ui.constants;

public enum FaceState {

    OPENED_EYES("images/bbm_1.png"),

    SEMI_OPENED_EYES("images/bbm_2.png"),

    CLOSED_EYES("images/bbm_3.png");

    public final String src;

    FaceState(String src) {
        this.src = src;
    }


}
