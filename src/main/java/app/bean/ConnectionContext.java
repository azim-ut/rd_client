package app.bean;

import app.constants.ServerMode;
import app.screen.ScreenQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ConnectionContext {

    private final ScreenQueue screens = new ScreenQueue();

    private final ServerMode mode;
    private final String code;
    private String ip = null;
    private int port;
    private long dateline;

    public boolean enableToConnect() {
        if (ip == null || port == 0 || code == null) {
            return false;
        }
        if (ip.isEmpty() || code.isEmpty()) {
            return false;
        }
        return true;
    }

    public ScreenQueue screens() {
        return screens;
    }

    public void reset() {
        this.ip = null;
        this.port = 0;
        dateline = 0;
    }

    @Override
    public String toString() {
        return "ConnectionContext{" +
                "code='" + code + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", dateline=" + dateline +
                '}';
    }
}
