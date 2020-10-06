package app.runnable;

import app.bean.ActionPacket;
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

    public TcpImageSocket(String code, String ip, int port) {
        super(code, ip, port);
    }


    public TcpImageSocket withQueue(Queue<ActionPacket> screens) {
        this.screens = screens;
        return this;
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket(ip, port)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());


                while (true) {
                    ActionPacket packet = screens.peek();
                    if (packet != null) {
                        log.info("Send 1 of " + screens.size() + " Image into. " + packet.toString());
                        outputStream.writeObject(packet);
                        outputStream.flush();
                        try {
                            while(true){
                                if (socket.getInputStream().available() != 0) {
                                    ResponsePacket serverAnswer = (ResponsePacket) inputStream.readObject();
                                    log.info("Answer: " + serverAnswer.toString());
                                    break;
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            log.error(e.getMessage(), e);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                        screens.poll();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("Need to reconnect to socket.");
            }
        }
//        log.warn("FINISHED");
    }
}
