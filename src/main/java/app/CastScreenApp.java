package app;

import app.runnable.UploadFileTask;
import app.service.ScreenService;
import app.service.bean.Screen;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Hello world!
 */
@Component
public class CastScreenApp {

    ScreenService screenService = new ScreenService();

    CloseableHttpClient client;
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    private Screen bgScreen = null;
    private List<Integer> samples = null;
    private Queue<String> screens = new LinkedList<>();

    public void start(String[] args) {
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(5);
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();

        bgScreen = screenService.get();
        int side = screenService.getMaxEnabledSquare(bgScreen);

        saveBg(bgScreen);
        samples = bgScreen.croppedToSet(side, side);

        while (true) {
            int sampleIndex = 0;
            int changes = 0;

            for (int j = 0; j < bgScreen.getHeight(); j = j + side) {
                for (int i = 0; i < bgScreen.getWidth(); i = i + side) {
                    try {
                        changes += processArea(i, j, sampleIndex, side);

                        if (changes > samples.size() / 2) {
                            bgScreen = screenService.get();
                            saveBg(bgScreen);
                            samples = bgScreen.croppedToSet(side, side);
                            return;
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    sampleIndex++;
                }
                if (changes > samples.size() / 2) {
                    break;
                }
            }
            System.out.println("-----------------------");
        }
//        threadPoolExecutor.shutdown();
    }

    private int processArea(int i, int j, int sampleIndex, int sideSize) throws IOException {
        BufferedImage newCrop = screenService.get().getBufferedImage().getSubimage(i, j, sideSize, sideSize);
        Screen row = Screen.builder()
                .bufferedImage(newCrop)
                .width(sideSize)
                .height(sideSize)
                .build();
        String filePath = "screen/temp" + sampleIndex + ".jpg";
        int newBlockSize = row.getAreaSum(0, 0, sideSize, sideSize);
        if (newBlockSize < 0) {
            Files.deleteIfExists(Paths.get(filePath));
            return 0;
        }
        int sampleBlockSize = samples.get(sampleIndex);

        if (newBlockSize != sampleBlockSize) {
            File file = new File(filePath);
            ImageIO.write(newCrop, "jpg", file);
//            sendScreen(file);
            return 1;
        } else {
            Files.deleteIfExists(Paths.get(filePath));
        }
        return 0;
    }

    public void sendScreen(File file) {
        String url = "http://it-prom.com/upload.php";

        threadPoolExecutor.execute(new UploadFileTask(client, url, file));
    }

    private void saveBg(Screen screen) {
        try {
            ImageIO.write(screen.getBufferedImage(), "jpg", new File("screen/bg.jpg"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
