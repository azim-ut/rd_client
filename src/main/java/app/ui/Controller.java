package app.ui;

import app.bean.ConnectionContext;
import app.constants.ServerMode;
import app.service.ScreenService;
import app.ui.constants.FaceState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
public class Controller {

    private FaceState faceState = FaceState.CLOSED_EYES;
    private ConnectionContext context;

    private ScreenService screenService;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button face;

    @FXML
    void initialize() {
        context = new ConnectionContext(ServerMode.SAVE);
        screenService = new ScreenService(context);
        face.setOnAction(event -> {
            if (faceState == FaceState.CLOSED_EYES) {
                startShare();
            } else {
                stopShare();
            }
        });
        setFace(faceState);
    }

    private void startShare() {
        screenService.start();
        setFace(FaceState.OPENED_EYES);
    }

    private void stopShare() {
        screenService.stop();
        setFace(FaceState.CLOSED_EYES);
    }

    private void setFace(FaceState newState) {

        Image semiImg = new Image(FaceState.SEMI_OPENED_EYES.src);
        ImageView semiView = new ImageView(semiImg);
        face.setGraphic(semiView);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            log.error("FaceState image change exception. ", e);
        }
        Image img = new Image(newState.src);
        ImageView view = new ImageView(img);
        face.setGraphic(view);

        faceState = newState;
    }
}
