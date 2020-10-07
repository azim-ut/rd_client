package app.runnable;

import app.bean.ActionPacket;
import app.bean.ConnectionContext;

import java.util.Queue;

public abstract class CastImage implements Runnable {

    protected ConnectionContext ctx;
    protected Queue<ActionPacket> screens;

    public CastImage(ConnectionContext ctx, Queue<ActionPacket> screens) {
        this.ctx = ctx;
        this.screens = screens;
    }
}
