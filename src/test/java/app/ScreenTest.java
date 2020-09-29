package app.itprom;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import app.runnable.UploadFileTask;
import app.service.ScreenService;
import app.service.bean.Screen;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit test for simple App.
 */

@RunWith(MockitoJUnitRunner.class)
public class ScreenTest {

    @InjectMocks
    ScreenService screenService = new ScreenService();

    CloseableHttpClient client;
    ThreadPoolExecutor threadPoolExecutor;

    @Before
    public void start() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(5);
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
    }

    @Ignore
    @Test
    public void createScreen() throws AWTException, IOException, InterruptedException {
        Screen bgScreen = screenService.get();

        Assert.notNull(bgScreen, "Screen is null");
        Assert.notNull(bgScreen.getBufferedImage(), "Has no image");

        int side = screenService.getMaxEnabledSquare(bgScreen);
        System.out.println(side);

        ImageIO.write(bgScreen.getBufferedImage(), "jpg", new File("screen/bg.jpg"));
        List<Integer> samples = bgScreen.croppedToSet(side, side);


        int counter = 100;
        while (counter-- > 0) {
            int walk = 0;
            int changes = 0;
            Screen newScreen = screenService.get();
            for (int j = 0; j < bgScreen.getHeight(); j = j + side) {
                for (int i = 0; i < bgScreen.getWidth(); i = i + side) {
                    BufferedImage newCrop = newScreen.getBufferedImage().getSubimage(i, j, side, side);
                    Screen row = Screen.builder()
                            .bufferedImage(newCrop)
                            .width(side)
                            .height(side)
                            .build();
                    int newBlockSize = row.getAreaSum(0, 0, side, side);
                    int sampleBlockSize = samples.get(walk);
                    String filePath = "screen/temp" + walk + ".jpg";

                    if (newBlockSize != sampleBlockSize) {
                        changes++;
                        if (changes > samples.size() / 2) {
                            bgScreen = screenService.get();
                            ImageIO.write(bgScreen.getBufferedImage(), "jpg", new File("screen/bg.jpg"));
                            samples = bgScreen.croppedToSet(side, side);
                            break;
                        }
                        File file = new File(filePath);
                        ImageIO.write(newCrop, "jpg", file);
//                        sendScreen(file);
                    } else {
                        Files.deleteIfExists(Paths.get(filePath));
                    }

                    walk++;
                }
                if (changes > samples.size() / 2) {
                    break;
                }
            }
            System.out.println("-----------------------");

            Thread.sleep(10);
        }
        threadPoolExecutor.shutdown();
    }

    public void sendScreen(File file) {
        String url = "http://it-prom.com/upload.php";

        threadPoolExecutor.execute(new UploadFileTask(client, url, file));
    }

    @Ignore
    @Test
    public void resetScreen() {

    }
}


