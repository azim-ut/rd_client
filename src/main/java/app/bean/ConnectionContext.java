package app.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ConnectionContext {
    private final String code;
    private String ip = null;
    private int port;
    private Boolean connected = false;

    public synchronized void setConnected(Boolean val){
        this.connected = val;
    }

    public boolean enableToConnect(){
        if(ip == null || port == 0 || code == null){
            return false;
        }
        if(ip.isEmpty() || code.isEmpty()){
            return false;
        }
        return true;
    }

    public void reset(){
        this.ip = null;
        this.port = 0;
        connected = false;
    }
}
