package app.bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
public class ScreenPacket implements Serializable {
    @NotNull
    private final String fileName;

    @NotNull
    private final String code;

    @NotNull
    private final byte[] bytes;

    public String getFileInfo() {
        StringBuilder str = new StringBuilder();
        str.append("File: ");
        str.append(fileName);
        str.append(" ");
        str.append("Code: ");
        str.append(code);
        str.append(" ");
        str.append(bytes.length);
        return str.toString();
    }
}
