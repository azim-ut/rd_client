package app.bean;

import app.constants.ServerMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;

@Slf4j
@Getter
@Setter
public class ConnectionContext {

    private final Queue<ScreenPacket> screens = new CircularFifoQueue<>(50);

    private final ServerMode mode;
    private String code;
    private String ip = null;
    private int port;
    private long dateline;
    private boolean connected;

    public ConnectionContext(ServerMode mode) {
        this.mode = mode;
        this.code = getMacAddress();
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

    private String getMacAddress() throws InternalError {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("", hexadecimal);
        } catch (SocketException | UnknownHostException e) {
            log.error("MacAddress define exception: " + e.getMessage(), e);
        }
        throw new InternalError("Can't get mac address");
    }

    public Queue<ScreenPacket> screens() {
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
