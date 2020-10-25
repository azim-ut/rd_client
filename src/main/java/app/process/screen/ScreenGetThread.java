package app.process.screen;

import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScreenGetThread extends Thread {

    public ScreenGetThread(ConnectionContext ctx) {
        super(new ScreenProcessorRunnable(ctx), "ScreenThread");
    }
}
