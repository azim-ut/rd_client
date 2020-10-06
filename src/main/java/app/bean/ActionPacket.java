package app.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Slf4j
@Getter
@Builder
public class ActionPacket implements Serializable {

    private final String createFile;

    private final String removeFile;

    private final String code;

    private final byte[] bytes;

    @Override
    public String toString() {
        return "ActionPacket{" +
                "createFile='" + createFile + '\'' +
                ", removeFile='" + removeFile + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
