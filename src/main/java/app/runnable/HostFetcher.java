package app.runnable;

import app.constants.HostAct;
import com.google.gson.Gson;
import app.bean.ConnectionContextResponse;
import app.bean.ConnectionContext;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class HostFetcher implements Runnable {
    private ConnectionContext ctx;
    private String url = "https://it-prom.com/charts/rest/ip/code";
    private Gson gson = new Gson();


    public HostFetcher(ConnectionContext state) {
        this.ctx = state;
    }

    @Override
    public void run() {
        while (true) {
            try {
                defineCurrent(ctx.getAct(), ctx.getCode());
                Thread.sleep(5000);
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void defineCurrent(HostAct act, String code) throws IOException {
        URL url = new URL(this.url + "/" + act + "/" + code);
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
