package app.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Builder
public class ResponsePacket implements Serializable {

    private Integer ok = 1;
    private String msg;

    @Override
    public String toString() {
        return "StatePacket{" +
                "ok=" + ok +
                ", msg='" + msg + '\'' +
                '}';
    }
}
