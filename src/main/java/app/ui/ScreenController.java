package app.ui;

import app.bean.ConnectionContext;
import app.bean.ScreenPacket;
import app.service.ScreenShowService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
@Component
public class ScreenController {

    private ConnectionContext context;
    private String code;

    private ScreenShowService screenShowService;

    private Thread interfaceThread;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane screen;

    private final Map<Integer, ScreenPacket> images = new HashMap<>();


    @FXML
    void initialize() {
        screenShowService = new ScreenShowService(context);
        defineUpdateHostThread();
        interfaceThread.start();
        screen.getChildren().clear();

        new Thread(() -> {
            boolean started = false;
            while (true) {
                if (!started && code != null) {
                    screenShowService.start(code);
                    started = true;
                }
                if (started && context.getShow().size() > 0) {
                    ScreenPacket packet = context.getShow().poll();
                    if (packet != null) {
                        if (packet.getBytes().length > 0) {
                            images.put(packet.getPosition(), packet);
                        } else {
                            images.remove(packet.getPosition());
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error("Images map update interrupted.");
                }
            }
        }).start();
    }

    public void shutdown() {
        screenShowService.stop();
        interfaceThread.interrupt();
        clearScene();
        images.clear();
    }

    public void setContext(ConnectionContext context) {
        this.context = context;
        this.screenShowService = new ScreenShowService(context);
    }

    public void setCode(String code) {
        this.code = code;
    }

    private void defineUpdateHostThread() {
        interfaceThread = new Thread(() -> {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    clearScene();
                    Iterator<Map.Entry<Integer, ScreenPacket>> it = images.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<Integer, ScreenPacket> row = it.next();
                        ScreenPacket packet = row.getValue();
                        if (packet.getPosition() == 0) {
                            log.info("BG");
                        }
                        if (packet.getBytes().length > 0) {
                            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(packet.getBytes());
                            Image image = new Image(byteInputStream);
                            ImageView imageView = new ImageView(image);
                            imageView.setCache(false);
                            imageView.setX(packet.getX());
                            imageView.setY(packet.getY());

                            screen.getChildren().add(imageView);
                        }
                    }
                }
            };
            while (!interfaceThread.isInterrupted()) {
                try {
                    Platform.runLater(task);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("ShowScreen exception: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void clearScene() {
        screen.getChildren().clear();
    }
}
