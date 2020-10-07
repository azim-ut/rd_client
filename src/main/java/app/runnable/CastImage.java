package app.runnable;

import app.bean.ConnectionContext;

public abstract class CastImage implements Runnable {

    protected ConnectionContext ctx;

    public CastImage(ConnectionContext ctx) {
        this.ctx = ctx;
    }
}
