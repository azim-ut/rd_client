package app.bean;

import lombok.Getter;

@Getter
public class SocketRestResponse {
    private SocketData data;

    public String getIp() {
        return data.ip;
    }

    public int getPortSave() {
        return data.port_save;
    }

    public int getPortShow() {
        return data.port_show;
    }

    public long getDateline() {
        return data.dt;
    }

    @Getter
    static class SocketData {
        private String ip;
        private int port_save;
        private int port_show;
        private int busy_save;
        private int busy_show;
        private long dt;
    }

}