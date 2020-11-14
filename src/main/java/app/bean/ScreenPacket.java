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
    private int frame = 0;
    private int position = -1;
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;
    private String command = null;

    public String getCode() {
        parseId();
        return code;
    }

    public int getFrame() {
        parseId();
        return frame;
    }

    public int getPosition() {
        parseId();
        return position;
    }

    private void parseId() {
        if (code == null || position == -1) {
            String[] temp = id.split("_");
            code = temp[0];
            frame = Integer.parseInt(temp[1]);
            position = Integer.parseInt(temp[2]);
            x = Integer.parseInt(temp[3]);
            y = Integer.parseInt(temp[4]);
            w = Integer.parseInt(temp[5]);
            h = Integer.parseInt(temp[6]);
        }
    }

    @Override
    public String toString() {
        int ln = 0;
        if (bytes != null) {
            ln = bytes.length;
        }
        return "ScreenPacket{" +
                ", id='" + id + '\'' +
                ", command='" + command + '\'' +
                ", bytes length='" + ln + '\'' +
                '}';
    }
}
