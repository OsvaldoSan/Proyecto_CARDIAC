package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Bienvenida {

    @FXML
    private Button cardiac;

    @FXML
    private void goVirtualMachine(ActionEvent event){
        Object recurso=event.getSource();
        if(recurso.equals(cardiac)){
            System.out.println("Reconocio a cardiac");
            loadStage("../view/cardiac.fxml",event);
        }
    }

    private void loadStage(String url, ActionEvent event){
        try{
            /*Object eventSource = event.getSource();
            Node sourceAsNode = (Node) eventSource ;
            Scene oldScene = sourceAsNode.getScene();
            Window window = oldScene.getWindow();
            //Stage stage = (Stage) window ;
            //stage.hide();*/
            Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();


            Parent nueva = FXMLLoader.load(getClass().getResource(url));
            Scene scene = new Scene(nueva);
            scene.getStylesheets().add(getClass().getResource("../view/styles_cardiac.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("CARDIAC");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
