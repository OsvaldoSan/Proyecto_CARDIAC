package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    public static final String TITLE = "CARDIAC Systems" ;
    public static final String FXMLWELCOME="../view/welcome.fxml";
    public static final String STYLESCSS="../view/styles_to_cardiac.css";
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/welcome.fxml"));

        // Search in web css background gradient transparency fxml
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}


