package app.runnable;

import app.bean.ScreenPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;


@Slf4j
public class TcpImageSocket extends CastImage {
    private Queue<ScreenPacket> screens = null;

    public TcpImageSocket(String code, String ip, int port) {
        super(code, ip, port);
    }


    public TcpImageSocket withQueue(Queue<ScreenPacket> screens) {
        this.screens = screens;
        return this;
    }

    @Override
    public void run() {
        while (true) {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try (Socket socket = new Socket(ip, port)) {
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());

                ScreenPacket packet;
                while (true) {
                    packet = screens.poll();
                    if (packet != null) {
                        log.info("Send Image into. " + packet.getFileInfo());
                        outputStream.writeObject(packet);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
