package app.bean;

import lombok.Getter;

@Getter
public class ConnectionContextResponse {
    private ConnectionPath data;

    public String getIp() {
        return data.ip;
    }

    public String getCode() {
        return data.code;
    }

    public int getPort() {
        return data.port;
    }

    public long getDateline() {
        return data.dt;
    }

    @Getter
    static class ConnectionPath {
        private String code;
        private String ip;
        private int port;
        private long dt;
    }

}