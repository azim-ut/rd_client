package app.process.host;

import app.bean.ConnectionContext;
import app.bean.SocketRestResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class HostUpdateRunnable implements Runnable {
    private final ConnectionContext ctx;
    private final static String URL = "https://it-prom.com/charts/rest/socket/show";
    private final Gson gson = new Gson();

    public HostUpdateRunnable(ConnectionContext state) {
        this.ctx = state;
    }

    @Override
    public void run() {
        long lastHostDateline = 0;
        try {
            while (true) {
                try {
                    defineCurrent();
                    if (lastHostDateline == 0) {
                        lastHostDateline = ctx.getDateline();
                    }
                    Thread.sleep(5000);
                } catch (IOException ioException) {
                    log.error("HostFetcherRunnable exception: " + ioException.getMessage());
                }
            }
        } catch (InterruptedException e) {
            log.info("HostFetcherRunnable interrupted.");
        }
    }

    private void defineCurrent() throws IOException {
        URL url = new URL(URL);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String ln = "";
        while ((ln = br.readLine()) != null) {
            stringBuilder.append(ln);
        }

        SocketRestResponse response = gson.fromJson(stringBuilder.toString(), SocketRestResponse.class);
        if (response.getData() == null) {
            log.info("No available port found: " + url);
            return;
        }
        ctx.setIp(response.getIp());
        ctx.setPortSave(response.getPortSave());
        ctx.setPortShow(response.getPortShow());
        ctx.setDateline(response.getDateline());
//        log.info("Update connection info: " + ctx.toString());
    }
}
