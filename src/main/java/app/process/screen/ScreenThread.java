package app.process.screen;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScreenThread extends Thread {

    public ScreenThread(ConnectionContext ctx) {
        super(new ScreenProcessor(ctx), "ScreenThread");
    }
}
