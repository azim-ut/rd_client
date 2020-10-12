package app.bean;

import app.constants.HostAct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ConnectionContext {
    private final HostAct act;
    private final String code;
    private String ip = null;
    private int port;
    private long dateline;
    private Boolean connected = false;

    public synchronized void setConnected(Boolean val) {
        this.connected = val;
    }

    public boolean enableToConnect() {
        if (ip == null || port == 0 || code == null) {
            return false;
        }
        if (ip.isEmpty() || code.isEmpty()) {
            return false;
        }
        return true;
    }

    public void reset() {
        this.ip = null;
        this.port = 0;
        dateline = 0;
        connected = false;
    }

    @Override
    public String toString() {
        return "ConnectionContext{" +
                "code='" + code + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", dateline=" + dateline +
                ", connected=" + connected +
                '}';
    }
}
