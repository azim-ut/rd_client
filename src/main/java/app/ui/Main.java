package app.ui;

import app.bean.ConnectionContext;
import app.constants.ServerMode;
import app.manager.ToolsManager;
import app.process.host.HostUpdateThread;
import app.service.ScreenSaveService;
import app.service.ScreenShowService;
import app.ui.constants.FaceState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
public class Main {

    private FaceState faceState = FaceState.CLOSED_EYES;
    private ConnectionContext context;

    private ScreenSaveService screenSaveService;
    private ScreenShowService screenShowService;
    private HostUpdateThread hostUpdateThread;
    private Thread interfaceThread;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button face;

    @FXML
    private TextField countText;

    @FXML
    private Button copyCodeBtn;

    @FXML
    private Pane screen;

    @FXML
    private TextField watchCode;

    @FXML
    private Button watchBtnStart;

    @FXML
    private Button watchBtnStop;

    private Stage stage;

    private ScreenController screenController;


    @FXML
    void initialize() {
        FXMLLoader loader = new FXMLLoader();

        context = new ConnectionContext(ServerMode.SAVE);
        screenSaveService = new ScreenSaveService(context);
        hostUpdateThread = new HostUpdateThread(context);
        countText.setText("0");


        copyCodeBtn.setText(ToolsManager.getMacAddress());
        copyCodeBtn.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            content.putString(ToolsManager.getMacAddress());
            clipboard.setContent(content);
        });

        watchBtnStart.setOnAction(event -> {
            if (stage == null) {
                loader.setLocation(getClass().getResource("/screen.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    log.error("Screen UI open exception: {}", e.getMessage());
                }
                Parent root = loader.getRoot();

                stage = new Stage();
                stage.setScene(new Scene(root));

                screenController = loader.getController();
                stage.setOnHidden(e -> {
                    screenController.shutdown();
                    stage.close();
                });
            }else{
                screenController.initialize();
            }

            screenController.setContext(context);
            screenController.setCode(watchCode.getText());
            stage.show();
        });

        hostUpdateThread.start();

        face.setOnAction(event -> {
            if (faceState == FaceState.CLOSED_EYES) {
                startShare();
            } else {
                stopShare();
            }
        });
        setFace(faceState);
    }

    private void clearScene() {
        screen.getChildren().removeAll(screen.getChildren());
    }

    private boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void startShare() {
        screenSaveService.start();
        setFace(FaceState.OPENED_EYES);
    }

    private void stopShare() {
        screenSaveService.stop();
        setFace(FaceState.CLOSED_EYES);
    }

    private void setFace(FaceState newState) {

        Image semiImg = new Image(FaceState.SEMI_OPENED_EYES.src);
        ImageView semiView = new ImageView(semiImg);
        face.setGraphic(semiView);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            log.error("FaceState image change exception. ", e);
        }
        Image img = new Image(newState.src);
        ImageView view = new ImageView(img);
        face.setGraphic(view);

        faceState = newState;
    }

    public void copyToClipboard(String link) {
    }
}
