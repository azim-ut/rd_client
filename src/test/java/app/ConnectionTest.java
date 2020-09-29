package app;

import com.google.common.collect.Lists;
import app.bean.ConnectionState;
import app.runnable.HostFetcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Unit test for simple App.
 */
public class ConnectionTest {
    private ConnectionState connectionState;
    private final Queue<String> clientMessages = new LinkedList<>();

    @Before
    public void init() {
        this.connectionState = new ConnectionState("TEST");
    }

    @Ignore
    @Test
    public void connectionPath() throws InterruptedException {
        new Thread(new HostFetcher(connectionState)).start();
        Socket socket = null;

        clientMessages.addAll(Lists.newArrayList("one", "two", "three", "close"));

        while (connectionState.getPath() == null || connectionState.getPath().getIp() == null) {
            Thread.sleep(500);
        }
        while (socket == null) {
            try {
                socket = new Socket("localhost", connectionState.getPath().getPort());
                System.out.println("Connected");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        boolean closed = false;
        while (!closed) {

            DataInputStream inputStream = null;
            DataOutputStream outputStream = null;
            BufferedReader br = null;
            try {
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(System.in));

                String clientMessage = "", serverMessage = "";
                while (!clientMessage.equals("close")) {
                    clientMessage = clientMessages.poll();
                    outputStream.writeUTF(clientMessage);
                    outputStream.flush();
//                    serverMessage = inputStream.readUTF();
//                    System.out.println("From Server: " + serverMessage);
                }
                closed = true;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
