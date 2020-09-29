package app.runnable;

import com.google.gson.Gson;
import app.bean.ConnectionPath;
import app.bean.ConnectionPathResponse;
import app.bean.ConnectionState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HostFetcher implements Runnable {
    private ConnectionState connectionState;
    private String code;
    private String url = "https://it-prom.com/charts/rest/ip/code";
    private Gson gson = new Gson();

    public HostFetcher(ConnectionState state) {
        this.connectionState = state;
    }

    @Override
    public void run() {
        int cnt = 20;
        int i = cnt;
        while (true) {
            try {
                ConnectionPath newPath = getCurrent(connectionState.getCode());
                if(newPath != null){
                    if (!newPath.equals(this.connectionState) || i-- < 0) {
                        this.connectionState.setPath(newPath);
                        i = cnt;
                    }
                }
                Thread.sleep(5000);
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private ConnectionPath getCurrent(String code) throws IOException {
        URL url = new URL(this.url + "/" + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String ln = "";
        while ((ln = br.readLine()) != null) {
            stringBuilder.append(ln);
        }
        ConnectionPathResponse response = gson.fromJson(stringBuilder.toString(), ConnectionPathResponse.class);
        return response.getData();
    }
}
