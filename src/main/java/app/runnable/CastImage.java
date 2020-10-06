package app.runnable;

public abstract class CastImage implements Runnable {

    protected String code = null;
    protected String ip = null;
    protected  int port = 0;

    public CastImage(String code, String ip, int port) {
        this.code = code;
        this.ip = ip;
        this.port = port;
    }
}
