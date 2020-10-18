package app;

import app.bean.ConnectionContext;
import app.bean.ScreenPacket;
import app.constants.ServerMode;
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
public class CastScreenApp extends BaseApp {

    private final Queue<ScreenPacket> pipe = new LinkedList<>();

    public void start(String[] args) {
        init(args);
        ConnectionContext ctx = new ConnectionContext(ServerMode.SAVE, this.code);

        Thread hostUpdateThread = new Thread(new HostFetcher(ctx), "HostUpdateThread");
        Thread senderThread = new Thread(new TcpScreenSendSocket(ctx, pipe), "SendScreenThread");
        Thread screenThread = new Thread(new ScreenProcessor(ctx, pipe), "ScreenProcessThread");

        Thread monitoringThread = new Thread(new Monitoring(ctx, hostUpdateThread, senderThread, screenThread), "Monitor");

        monitoringThread.start();

        senderThread.start();
        hostUpdateThread.start();
        screenThread.start();
    }
}
