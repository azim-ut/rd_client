package app.service;

import app.bean.ConnectionContext;
import app.bean.ScreenPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenShowService {
    final private ConnectionContext ctx;

    private Thread activeThread = null;
    private boolean work = false;

    public void stop() {
        if (activeThread != null && !activeThread.isInterrupted()) {
            activeThread.interrupt();
            work = false;
        }
    }

    public void start(final String code) {
        stop();
        work= true;
        activeThread = new Thread(() -> {
            try {
                ObjectInputStream in = null;
                while (true) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException("TcpShareImageRunnable is interrupted.");
                    }

                    if (!ctx.enableToConnect()) {
                        Thread.sleep(10);
                        continue;
                    }
                    try (Socket socket = new Socket(ctx.getIp(), ctx.getPortShow());
                         ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    ) {

                        out.writeUTF(code);
                        out.flush();

                        log.info("Connected to the socket: " + ctx.toString());
                        String command = null;
                        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        while (!"DONE".equals(command) && work) {
                            ScreenPacket packet = (ScreenPacket) in.readObject();
                            if(packet.getId() != null){
                                command = packet.getCommand();
                                ctx.getShow().add(packet);
                            }
                            log.debug("ScreenShowService received: {}", packet);
                        }
                    } catch (SocketException e) {
                        log.error("ScreenShowService: {}", e.getMessage());
                    } catch (ClassNotFoundException e) {
                        log.error("Class cast exception: " + e.getMessage(), e);
                    } catch (IOException e) {
                        log.error("Can't connect. Socket connection Exception: {}", e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                log.info("ScreenShowService interrupted");
            }
        });
        activeThread.start();
    }
}
