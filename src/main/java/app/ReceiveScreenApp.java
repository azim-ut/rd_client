package app;

import app.bean.ConnectionContext;
import app.constants.Mode;
import app.runnable.HostFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Hello world!
 */
@Slf4j
@Component
public class ReceiveScreenApp extends BaseApp {

    public void start(String[] args) throws InterruptedException {
        init(args);
        ConnectionContext ctx = new ConnectionContext(Mode.SHOW, this.code);

        Thread hostUpdateThread = new Thread(new HostFetcher(ctx), "HostUpdateThread");
        hostUpdateThread.start();

        long socketLastDateline = ctx.getDateline();


        while (true) {
            if (!ctx.enableToConnect()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.error("Waiting to get remote IP and port" + e.getMessage(), e);
                }
                continue;
            }
            try (Socket socket = new Socket(ctx.getIp(), ctx.getPort())) {
                ctx.setConnected(true);
                log.info("Connection to the socket: " + ctx.toString());

                int nextSize = 0;
                int currentSize = 0;
                byte[] message = new byte[0];

                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    DataInputStream dIn = new DataInputStream(socket.getInputStream());

                    if (currentSize >= nextSize && nextSize > 0) {
                        log.info("Done: " + nextSize);
                        dOut.writeUTF("Done");
                        currentSize = 0;
                        nextSize = 0;
                    }

                    if (nextSize == 0) {
                        if (dIn.available() > 0) {
                            String key = dIn.readUTF();
                            nextSize = dIn.readInt();
                            if (nextSize < 0 || nextSize > 1000000) {
                                continue;
                            }
                            log.info("Next screen " + key + " file length: " + nextSize);
                            message = new byte[nextSize];
                        }
                    }


                    if (nextSize > 0) {
                        log.info("Start receive file total: " + nextSize + " loaded: " + currentSize);
                        int i;
                        currentSize += dIn.read(message);
//                        while ((i = dIn.read()) != -1 && message.length != currentSize) {
//                            try {
//                                dIn.read(message, currentSize, 1);
//                                currentSize++;
//                            } catch (ArrayIndexOutOfBoundsException e) {
//                                e.printStackTrace();
//                            }
//                        }

                        log.info("Loaded: " + currentSize);
                    }
                }
            } catch (IOException e) {
                log.error("Socket connection Exception: {}", e.getMessage());
            } finally {
                log.info("Need to reconnect to socket.");
                if (ctx != null) {
                    ctx.reset();
                }
            }
        }
    }
}
