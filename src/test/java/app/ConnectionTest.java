package app;

import app.bean.ConnectionContext;
import app.constants.HostAct;
import app.runnable.HostFetcher;
import com.google.common.collect.Lists;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit test for simple App.
 */
public class ConnectionTest {
    private ConnectionContext ctx;
    private final Queue<String> clientMessages = new LinkedList<>();

    @Before
    public void init() {
        this.ctx = new ConnectionContext(HostAct.SAVE, "TEST");
    }

    @Ignore
    @Test
    public void connectionPath() throws InterruptedException {
        new Thread(new HostFetcher(ctx)).start();
        Socket socket = null;

        clientMessages.addAll(Lists.newArrayList("one", "two", "three", "close"));

        while (ctx.getIp() == null || ctx.getPort() == 0) {
            Thread.sleep(500);
        }
        while (socket == null) {
            try {
                socket = new Socket(Constants.IP, ctx.getPort());
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

    @Ignore
    @Test
    public void testHttpConnection() {
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(5);
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        String url = "http://it-prom.com/upload.php";

//        threadPoolExecutor.execute(new UploadFileTask(client, url, file));
    }
}
