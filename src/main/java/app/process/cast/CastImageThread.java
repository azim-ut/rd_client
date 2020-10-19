package app.process.cast;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CastImageThread extends Thread {

    public CastImageThread(ConnectionContext ctx) {
        super(new TcpCastImageRunnable(ctx), "CastImageThread");
    }
}
