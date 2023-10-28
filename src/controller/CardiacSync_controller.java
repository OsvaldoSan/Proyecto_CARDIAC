package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CardiacSync_controller extends Cardiac{
    // Design Variables
    @FXML
    private GridPane gridPMachineStatus;

    Label gDescriptionStarter = new Label("Starter");
    Label gStarter= new Label("On");

    @FXML
    public void editLayout(){
        // Grid pane for Machine Status
        System.out.println("Gridpane value: "+gridPMachineStatus);
        int newRow = gridPMachineStatus.getRowCount();
        int lastCol = gridPMachineStatus.getColumnCount();
        gridPMachineStatus.addRow(newRow);
        GridPane.setRowIndex(gDescriptionStarter,newRow);
        GridPane.setColumnIndex(gDescriptionStarter,0);
        gridPMachineStatus.getChildren().add(gDescriptionStarter);

        GridPane.setRowIndex(gStarter,newRow);
        GridPane.setColumnIndex(gStarter,lastCol-1);
        gridPMachineStatus.getChildren().add(gStarter);
    }

}
