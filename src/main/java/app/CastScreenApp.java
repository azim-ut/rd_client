package app;

import app.bean.ActionPacket;
import app.bean.ConnectionContext;
import app.runnable.HostFetcher;
import app.runnable.Monitoring;
import app.runnable.ScreenProcessor;
import app.runnable.TcpScreenSendSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Hello world!
 */
@Slf4j
@Component
public class CastScreenApp {

    private final Queue<ActionPacket> pipe = new LinkedList<>();

    public void start(String[] args) throws InterruptedException {
        ConnectionContext ctx = new ConnectionContext("TEST");

        Thread hostUpdateThread = new Thread(new HostFetcher(ctx), "HostUpdateThread");
        Thread senderThread = new Thread(new TcpScreenSendSocket(ctx, pipe), "SendScreenThread");
        Thread screenThread = new Thread(new ScreenProcessor(pipe), "ScreenProcessThread");

        Thread monitoringThread = new Thread(new Monitoring(ctx, hostUpdateThread, senderThread, screenThread), "Monitor");

        monitoringThread.start();

        senderThread.start();
        hostUpdateThread.start();
        screenThread.start();
    }
}
