package app.process.host;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HostFetcherThread extends Thread {

    public HostFetcherThread(ConnectionContext ctx) {
        super(new HostFetcherRunnable(ctx), "HostFetcherThread");
    }
}
