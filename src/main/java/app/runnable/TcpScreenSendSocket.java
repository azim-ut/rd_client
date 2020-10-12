package app.runnable;

import app.bean.ScreenPacket;
import app.bean.ConnectionContext;
import app.bean.ResponsePacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;


@Slf4j
public class TcpScreenSendSocket extends CastImage {


    public TcpScreenSendSocket(ConnectionContext ctx, Queue<ScreenPacket> screens) {
        super(ctx, screens);
    }

    @Override
    public void run() {

        long socketLastDateline = 0;

        while (true) {
            if (ctx == null || !ctx.enableToConnect()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.error("Waiting to get remote IP and port" + e.getMessage(), e);
                }
                continue;
            }

            socketLastDateline = ctx.getDateline();

            try (Socket socket = new Socket(ctx.getIp(), ctx.getPort())) {
                ctx.setConnected(true);
                log.info("Connection to the socket: " + ctx.toString());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = null;

                int lastQueueSize = -1;

                while (true) {
                    if (ctx.getDateline() != socketLastDateline) {
                        log.info("Code: {}. New socket detected. My dateline: {}, socket dateline: {}", ctx.getCode(), socketLastDateline, ctx.getDateline());
                        break;
                    }
                    ScreenPacket packet = screens.peek();
                    if (packet != null) {
                        log.info("Send 1 of " + screens.size() + " Image into. " + packet.toString());
                        outputStream.writeObject(packet);
                        outputStream.flush();
                        try {
                            if (socket.getInputStream().available() != 0) {
                                if (inputStream == null) {
                                    inputStream = new ObjectInputStream(socket.getInputStream());
                                }
                                ResponsePacket serverAnswer = (ResponsePacket) inputStream.readObject();
                                log.info("Answer: " + serverAnswer.toString());
                            }
                        } catch (ClassNotFoundException | IOException e) {
                            log.error(e.getMessage(), e);
                        }
                        screens.poll();
                        lastQueueSize = screens.size();
                    } else {
                        if (lastQueueSize != 0) {
                            log.info("Queue is off?");
                            lastQueueSize = 0;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
//        log.warn("FINISHED");
    }
}
