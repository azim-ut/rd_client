package app.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Builder
public class ScreenPacket implements Serializable {

    private final String createFile;

    private final String removeFile;

    private final String id;

    private final String code;

    private final int position;

    private final long epoch;

    private final int x;

    private final int y;

    private final int tw;

    private final int th;

    private final int w;

    private final int h;

    private final byte[] bytes;


    @Override
    public String toString() {
        return "ScreenPacket{" +
                "createFile='" + createFile + '\'' +
                ", removeFile='" + removeFile + '\'' +
                ", id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", position=" + position +
                ", epoch=" + epoch +
                ", x=" + x +
                ", y=" + y +
                ", tw=" + tw +
                ", th=" + th +
                ", w=" + w +
                ", h=" + h +
                '}';
    }
}
