package app.bean;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConnectionPath {
    String code;
    String ip;
    int port;
}
