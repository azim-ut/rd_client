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

    private final String code;

    private final long epoch;

    private final int position;

    private final int x;

    private final int y;

    private final int tw;

    private final int th;

    private final int w;

    private final int h;

    private final byte[] bytes;

    @Override
    public String toString() {
        return "ActionPacket{" +
                "createFile='" + createFile + '\'' +
                ", removeFile='" + removeFile + '\'' +
                ", code='" + code + '\'' +
                ", epoch=" + epoch +
                ", position=" + position +
                ", x=" + x +
                ", y=" + y +
                ", tw=" + tw +
                ", th=" + th +
                ", w=" + w +
                ", h=" + h +
                '}';
    }
}
