package app.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Builder
public class ActionPacket implements Serializable {

    private final String createFile;

    private final String removeFile;

    private final String code;

    private final long epoch;

    private final int position;

    private final byte[] bytes;

    @Override
    public String toString() {
        return "ActionPacket{" +
                "createFile='" + createFile + '\'' +
                ", removeFile='" + removeFile + '\'' +
                ", epoch='" + epoch + '\'' +
                ", code='" + code + '\'' +
                ", position=" + position +
                '}';
    }
}
