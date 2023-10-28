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
    private Button gCardiac,gCardiacSync;

    @FXML
    private void goVirtualMachine(ActionEvent vmButton){
        Object resource=vmButton.getSource();
        if(resource.equals(gCardiac)){
            System.out.println("Go to cardiac");
            loadStage("../view/cardiac.fxml",vmButton,new Cardiac());
        }
        else if(resource.equals(gCardiacSync)){
            System.out.println("Go to E-CARDIAC SYNC");
            loadStage("../view/cardiac.fxml",vmButton,new CardiacSync_controller());
        }
    }

    private void loadStage(String fxmlPage, ActionEvent selectedVM, Object controller_selected){
        try{
            if(selectedVM.getSource()== gCardiac){
               controller_selected=(Cardiac)controller_selected;
            }
            else if(selectedVM.getSource()==gCardiacSync){
                controller_selected=(CardiacSync_controller)controller_selected;
                
            }

            //To get the window and execute in the same window
            Stage stage=(Stage)((Node)selectedVM.getSource()).getScene().getWindow();

            FXMLLoader loader=new FXMLLoader(getClass().getResource(fxmlPage));
            loader.setController(controller_selected);

            
            Parent newPage = (Parent) loader.load();
            Scene scene = new Scene(newPage);
            //scene.getStylesheets().add(getClass().getResource(Main.STYLESCSS).toExternalForm());
            scene.getStylesheets().add("../view/styles_to_cardiac.css");

            if(selectedVM.getSource()==gCardiacSync) ((CardiacSync_controller)controller_selected).editLayout();

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
