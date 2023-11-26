package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class CardiacSync_controller extends Cardiac{
    // Design Variables
    @FXML
    private GridPane gridPMachineStatus;

    @FXML
    private HBox HBoxBottom;

    Label gDescriptionStarter = new Label("Starter"),gDescriptionSwitcher=new Label("Switcher");
    Label gStarter= new Label("Working"),gSwitcher=new Label("SO");

    @FXML
    public void editLayout(){

        // -------------------------------------------- Section Left Bar ------------------------------------------------------------------

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

        // ----------------------------------------- Section bottom right -------------------------------------------------------

        TitledPane newTitlePane = new TitledPane();
        HBox.setHgrow(newTitlePane, Priority.ALWAYS);




        newTitlePane.setText("Memoria Secundaria");
        // Add a TextArea to the newTitledPane
        TextArea textArea = new TextArea();
        textArea.setPromptText("Empty");
        textArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 100 ? change : null));

        newTitlePane.setCollapsible(false); // Optional: Make the TitledPane non-collapsible
        newTitlePane.setContent(textArea);

        HBoxBottom.getChildren().add(newTitlePane);




    }

}
