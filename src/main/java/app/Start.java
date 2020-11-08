package app;

import app.service.ScreenToolsService;
import app.service.bean.Screen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Start extends Application {

    private final ScreenToolsService screenService = new ScreenToolsService();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Screen bgScreen = screenService.get();
        int width = bgScreen.getWidth();
        int height = bgScreen.getHeight();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/big_brother.fxml"));

        Parent root = loader.load();
        primaryStage.setTitle("Большой Брат");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
