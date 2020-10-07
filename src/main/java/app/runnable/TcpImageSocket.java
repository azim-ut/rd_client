package app.runnable;

import app.bean.ActionPacket;
import app.bean.ConnectionContext;
import app.bean.ResponsePacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;


@Slf4j
public class TcpImageSocket extends CastImage {
    private Queue<ActionPacket> screens = null;

    public TcpImageSocket(ConnectionContext ctx) {
        super(ctx);
    }


    public TcpImageSocket withQueue(Queue<ActionPacket> screens) {
        this.screens = screens;
        return this;
    }

    @Override
    public void run() {
        while (true) {
            if (ctx == null || !ctx.enableToConnect()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.error("Waiting to get remote IP and port" + e.getMessage(), e);
                }
                continue;
            }

            try (Socket socket = new Socket(ctx.getIp(), ctx.getPort())) {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());


                while (true) {
                    ActionPacket packet = screens.peek();
                    if (packet != null) {
                        log.info("Send 1 of " + screens.size() + " Image into. " + packet.toString());
                        outputStream.writeObject(packet);
                        outputStream.flush();
                        try {
                            while (true) {
                                if (socket.getInputStream().available() != 0) {
                                    ResponsePacket serverAnswer = (ResponsePacket) inputStream.readObject();
                                    log.info("Answer: " + serverAnswer.toString());
                                    break;
                                }
                            }
                        } catch (ClassNotFoundException | IOException e) {
                            log.error(e.getMessage(), e);
                        }
                        screens.poll();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("Need to reconnect to socket.");
                if(ctx != null){
                    ctx.reset();
                }
            }
        }
//        log.warn("FINISHED");
    }
}
