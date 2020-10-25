package app.service;

import app.bean.ConnectionContext;
import app.process.cast.SenderImageThread;
import app.process.host.HostUpdateThread;
import app.process.screen.ScreenGetThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenService {
    private HostUpdateThread hostUpdateThread;
    private SenderImageThread senderImageThread;
    private ScreenGetThread screenGetThread;
    private Thread monitor;

    final private ConnectionContext ctx;

    public void start() {
        monitor = new Thread(() -> {
            try {
                startHostThread();
                startScreenGetThread();
                startImageSendThread();
                while (true) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                log.error("ScreenService monitor: " + e.getMessage());
            }
        });
        monitor.start();
    }

    public void stop() {
        hostUpdateThread.interrupt();
        senderImageThread.interrupt();
        screenGetThread.interrupt();
        monitor.interrupt();
    }

    private void startHostThread() {
        hostUpdateThread = new HostUpdateThread(ctx);
        hostUpdateThread.start();
    }

    private void startScreenGetThread() {
        screenGetThread = new ScreenGetThread(ctx);
        screenGetThread.start();
    }

    private void startImageSendThread() {
        senderImageThread = new SenderImageThread(ctx);
        senderImageThread.start();
    }
}
