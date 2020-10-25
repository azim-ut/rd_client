package app.process.cast;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SenderImageThread extends Thread {

    public SenderImageThread(ConnectionContext ctx) {
        super(new TcpShareImageRunnable(ctx), "CastImageThread");
    }
}
