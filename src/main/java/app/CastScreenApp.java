package app;

import app.bean.ActionPacket;
import app.bean.ConnectionContext;
import app.runnable.HostFetcher;
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

        Thread hostUpdateThread = new Thread(new HostFetcher(ctx));
        Thread senderThread = new Thread(new TcpScreenSendSocket(ctx, pipe));
        Thread screenThread = new Thread(new ScreenProcessor(pipe));

        senderThread.start();
        hostUpdateThread.start();
        screenThread.start();
    }
}
