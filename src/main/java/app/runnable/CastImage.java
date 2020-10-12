package app.runnable;

import app.bean.ScreenPacket;
import app.bean.ConnectionContext;

import java.util.Queue;

public abstract class CastImage implements Runnable {

    protected ConnectionContext ctx;
    protected Queue<ScreenPacket> screens;

    public CastImage(ConnectionContext ctx, Queue<ScreenPacket> screens) {
        this.ctx = ctx;
        this.screens = screens;
    }
}
