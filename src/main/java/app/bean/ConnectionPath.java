package app.bean;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Builder
@Getter
public class ConnectionPath {
    
    @NotNull
    String code;

    @NotNull
    String ip;

    @NotNull
    int port;
}
