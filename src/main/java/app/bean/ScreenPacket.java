package app.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Builder
public class ScreenPacket implements Serializable {

    private final String id;

    private final byte[] bytes;

    private String code = null;
    private int position = -1;

    public String getCode() {
        parseId();
        return code;
    }

    public int getPosition() {
        parseId();
        return position;
    }

    private void parseId() {
        if (code == null || position == -1) {
            String[] temp = id.split(":");
            code = temp[0];
            position = Integer.parseInt(temp[1]);
        }
    }

    @Override
    public String toString() {
        return "ScreenPacket{" +
                ", id='" + id + '\'' +
                ", bytes length='" + bytes.length + '\'' +
                '}';
    }
}
