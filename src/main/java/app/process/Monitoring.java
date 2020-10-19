package app.process;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Monitoring implements Runnable {
    ConnectionContext ctx;

    public Monitoring(ConnectionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        while (true) {

        }
    }
}
