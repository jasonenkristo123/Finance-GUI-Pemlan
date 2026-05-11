package frontend;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/fxml/main-layout.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().add(
                Main.class.getResource("/css/app.css").toExternalForm()
        );

        stage.setTitle("Finance Tracker");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}