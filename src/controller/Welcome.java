package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
            /*Object eventSource = selectedVM.getSource();
            Node sourceAsNode = (Node) eventSource ;
            Scene oldScene = sourceAsNode.getScene();
            Window window = oldScene.getWindow();
            //Stage stage = (Stage) window ;
            //stage.hide();*/
            //To get the window and execute in the same window
            Stage stage=(Stage)((Node)selectedVM.getSource()).getScene().getWindow();


            Parent newPage = FXMLLoader.load(getClass().getResource(fxmlPage));
            Scene scene = new Scene(newPage);
            scene.getStylesheets().add(getClass().getResource("../view/styles_cardiac.css").toExternalForm());

            stage.setScene(scene);
            //Generalize with valie of button
            stage.setTitle("CARDIAC");
            stage.show();

        } catch (IOException e) {
            System.out.println("Error in the change of window");
            e.printStackTrace();
        }

    }
}
