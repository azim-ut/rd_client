package app.runnable;

import app.bean.ScreenPacket;
import app.bean.ConnectionContext;

import java.util.List;
import java.util.Queue;

public class Monitoring implements Runnable{
    private Queue<ScreenPacket> pipe;
    private List<Integer> samples = null;
    private Thread hostUpdateThread;
    private Thread senderThread;
    private Thread screenThread;

    public Monitoring(ConnectionContext ctx, Thread hostUpdateThread, Thread senderThread, Thread screenThread) {

    }

    @Override
    public void run() {

    }
}
