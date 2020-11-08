package app.bean;

import app.constants.ServerMode;
import app.manager.ToolsManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Queue;

@Slf4j
@Getter
@Setter
public class ConnectionContext {

    private final Queue<ScreenPacket> save = new CircularFifoQueue<>(50);
    private final Queue<ScreenPacket> show = new CircularFifoQueue<>(50);

    private final ServerMode mode;
    private String code;
    private String ip = null;
    private int portSave;
    private int portShow;
    private long dateline;
    private boolean connected;

    public ConnectionContext(ServerMode mode) {
        this.mode = mode;
        this.code = getMacAddress();
    }

    public String getIp() {
        return ip;
//        return "127.0.0.1";
    }

    public boolean enableToConnect() {
        if (ip == null || portSave == 0 || code == null) {
            return false;
        }
        if (ip.isEmpty() || code.isEmpty()) {
            return false;
        }
        return true;
    }

    private String getMacAddress() throws InternalError {
        return ToolsManager.getMacAddress();
    }

    public Queue<ScreenPacket> toSave() {
        return save;
    }

    public Queue<ScreenPacket> toShow() {
        return show;
    }

    public void reset() {
        this.ip = null;
        this.portSave = 0;
        dateline = 0;
    }

    @Override
    public String toString() {
        return "ConnectionContext{" +
                "code='" + code + '\'' +
                ", ip='" + ip + '\'' +
                ", portSave=" + portSave +
                ", portShow=" + portShow +
                ", dateline=" + dateline +
                '}';
    }
}
