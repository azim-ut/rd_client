package app.process.host;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HostUpdateThread extends Thread {

    public HostUpdateThread(ConnectionContext ctx) {
        super(new HostFetcherRunnable(ctx), "HostFetcherThread");
    }
}
