package app.process.cast;

import app.bean.ConnectionContext;
import app.bean.ResponsePacket;
import app.bean.ScreenPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


@Slf4j
public class TcpShareImageRunnable implements Runnable {

    protected ConnectionContext ctx;

    public TcpShareImageRunnable(ConnectionContext ctx) {

        this.ctx = ctx;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(Thread.interrupted()){
                    throw new InterruptedException("TcpShareImageRunnable is interrupted.");
                }

                if (!ctx.enableToConnect()) {
                    Thread.sleep(50);
                    continue;
                }

                try (Socket socket = new Socket(ctx.getIp(), ctx.getPort())) {
                    log.info("Connection to the socket: " + ctx.toString());
                    ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//                ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                    int lastQueueSize = -1;

                    while (true) {
                        ScreenPacket packet = ctx.screens().peek();
                        if (packet != null) {
                            log.info("Send 1 of " + ctx.screens().size() + " Image into. " + packet.toString());
                            outputStream.writeObject(packet);
                            outputStream.flush();
//                        outputStream.flush();
//                        inputStream = waitForAnswer(socket, inputStream);
                            ctx.screens().poll();
                            lastQueueSize = ctx.screens().size();
                        } else {
                            if (lastQueueSize != 0) {
                                log.info("Queue is off?");
                                lastQueueSize = 0;
                            }
                        }
                        Thread.sleep(50);
                    }
                } catch (IOException e) {
                    log.error("Can't connect. Socket connection Exception: {}", e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            log.info("TcpShareImageRunnable interrupted");
        }
    }

    private ObjectInputStream waitForAnswer(Socket socket, ObjectInputStream inputStream) {
        try {
            if (socket.getInputStream().available() != 0) {
                ResponsePacket serverAnswer = (ResponsePacket) inputStream.readObject();
                log.info("Answer: " + serverAnswer.toString());
            }
        } catch (ClassNotFoundException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return inputStream;
    }
}
