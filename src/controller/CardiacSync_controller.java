package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class CardiacSync_controller extends Cardiac{
    // Design Variables
    @FXML
    private GridPane gridPMachineStatus;

    Label gDescriptionStarter = new Label("Starter"),gDescriptionSwitcher=new Label("Switcher");
    Label gStarter= new Label("Working"),gSwitcher=new Label("SO");

    @FXML
    public void editLayout(){
        // Grid pane for Machine Status
        // CSS definitions for labels
        gStarter.setId("labelNumeric");
        gSwitcher.setId("labelNumeric");

        // Definitions for Machine Status
        System.out.println("Gridpane value: "+gridPMachineStatus);
        // Define the height of the new row equal to all the original rows
        RowConstraints newRowConstraints = new RowConstraints();
        newRowConstraints.setPrefHeight(gridPMachineStatus.getRowConstraints().get(0).getPrefHeight());
        // Get the number of rows
        int newRow = gridPMachineStatus.getRowCount();
        // Add the new row and the constraints
        gridPMachineStatus.addRow(newRow,gDescriptionStarter,gStarter);
        gridPMachineStatus.getRowConstraints().add(newRowConstraints);
        //Add switcher
        gridPMachineStatus.addRow(newRow+1,gDescriptionSwitcher,gSwitcher);


        gridPMachineStatus.getRowConstraints().add(newRowConstraints);



    }

}
