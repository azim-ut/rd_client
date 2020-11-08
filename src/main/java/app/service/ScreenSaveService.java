package app.service;

import app.bean.ConnectionContext;
import app.process.cast.SenderImageThread;
import app.process.screen.PrintScreenThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenSaveService {
    private SenderImageThread senderImageThread;
    private PrintScreenThread screenGetThread;
    private Thread monitor;

    final private ConnectionContext ctx;

    public void start() {
        monitor = new Thread(() -> {
            try {
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
        senderImageThread.interrupt();
        screenGetThread.interrupt();
        monitor.interrupt();
    }

    private void startScreenGetThread() {
        screenGetThread = new PrintScreenThread(ctx);
        screenGetThread.start();
    }

    private void startImageSendThread() {
        senderImageThread = new SenderImageThread(ctx);
        senderImageThread.start();
    }
}
