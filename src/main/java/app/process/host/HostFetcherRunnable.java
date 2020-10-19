package app.process.host;

import app.bean.ConnectionContext;
import app.bean.ConnectionContextResponse;
import app.constants.ServerMode;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class HostFetcherRunnable implements Runnable {
    private final ConnectionContext ctx;
    private final static String URL = "https://it-prom.com/charts/rest/ip/code";
    private final Gson gson = new Gson();


    public HostFetcherRunnable(ConnectionContext state) {
        this.ctx = state;
    }

    @Override
    public void run() {
        long lastHostDateline = 0;
        while (true) {
            try {
                defineCurrent(ctx.getMode(), ctx.getCode());
                if (lastHostDateline == 0) {
                    lastHostDateline = ctx.getDateline();
                }

                Thread.sleep(5000);
            } catch (IOException ioException) {
                log.error("HostFetcherRunnable exception: " + ioException.getMessage());
            } catch (InterruptedException e) {
                log.error("HostFetcherRunnable InterruptedException " + e.getMessage(), e);
            }
        }
    }

    private void defineCurrent(ServerMode mode, String code) throws IOException {
        URL url = new URL(URL + "/" + mode + "/" + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String ln = "";
        while ((ln = br.readLine()) != null) {
            stringBuilder.append(ln);
        }

        ConnectionContextResponse response = gson.fromJson(stringBuilder.toString(), ConnectionContextResponse.class);
        ctx.setIp(response.getIp());
        ctx.setPort(response.getPort());
        ctx.setDateline(response.getDateline());
//        log.info("Update connection info: " + ctx.toString());
    }
}
