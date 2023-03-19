package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;

public class Welcome {

    @FXML
    private Button cardiac;

    @FXML
    private void goVirtualMachine(ActionEvent vmButton){
        Object resource=vmButton.getSource();
        if(resource.equals(cardiac)){
            System.out.println("Go to cardiac");
            loadStage("../view/cardiac.fxml",vmButton);
        }
    }

    private void loadStage(String fxmlPage, ActionEvent selectedVM){
        try{
            //To get the window and execute in the same window
            Stage stage=(Stage)((Node)selectedVM.getSource()).getScene().getWindow();


            Parent newPage = FXMLLoader.load(getClass().getResource(fxmlPage));
            Scene scene = new Scene(newPage);
            scene.getStylesheets().add(getClass().getResource(Main.STYLESCSS).toExternalForm());

            stage.setScene(scene);
            Button button = (Button) selectedVM.getSource();
            stage.setTitle(button.getText());
            stage.show();

        } catch (IOException e) {
            System.out.println("Error in the change of window");
            e.printStackTrace();
        }

    }
}
