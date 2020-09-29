package app.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConnectionState {
    private ConnectionPath path;
    private final String code;
    private Boolean connected = false;

    public synchronized void setPath(ConnectionPath connectionPath) {
        path = connectionPath;
    }

    public synchronized void setConnected(Boolean val){
        this.connected = val;
    }
}
