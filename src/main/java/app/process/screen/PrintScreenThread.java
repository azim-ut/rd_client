package app.process.screen;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintScreenThread extends Thread {

    public PrintScreenThread(ConnectionContext ctx) {
        super(new PrintScreenRunnable(ctx), "PrintScreenThread");
    }
}
