package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import modelo.FileData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CardiacSync_controller extends Cardiac {
    // Design Variables
    @FXML
    private GridPane gridPMachineStatus;

    @FXML
    private HBox HBoxBottom;

    @FXML
    private TitledPane bottomQueue;
    @FXML
    private TabPane bottomTabPane;

    private TableView<FileData> fileSystem = new TableView<>();

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

        // Create new pane
        TitledPane newTitlePane = new TitledPane();
        HBox.setHgrow(bottomQueue, Priority.ALWAYS);
        HBox.setHgrow(bottomTabPane, Priority.ALWAYS);
        HBox.setHgrow(newTitlePane, Priority.ALWAYS);
        // Definitions and redefintions of the space
        newTitlePane.setPrefHeight(204);
        bottomQueue.setPrefWidth(271);
        bottomTabPane.setPrefWidth(561);
        newTitlePane.setPrefWidth(271);

        newTitlePane.setText("Memoria Secundaria");

        // Content of the new pane

        // Add a TextArea to the newTitledPane
        TextArea textArea = new TextArea();
        textArea.setPromptText("Empty");
        textArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 100 ? change : null));

        newTitlePane.setCollapsible(false); // Optional: Make the TitledPane non-collapsible

        newTitlePane.setContent(fileSystem);

        HBoxBottom.getChildren().add(newTitlePane);

        initializeTableView();


    }

    private void initializeTableView() {
        // Read data from the file and populate TableView
        try {
            ObservableList<FileData> data = readFileAndCreateData("/home/mrblue/Documents/Proyectos/Tesis_MAC/Cardiac_VM/src/modelo/Files/File_System_CSYNC.txt");
            fileSystem.setItems(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Falló la lectura del archivo");
            // Handle the exception as needed
        }

        // Set up columns
        TableColumn<FileData, String> directionColumn = new TableColumn<>("Direction");
        directionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDirection()));

        TableColumn<FileData, String> datosColumn = new TableColumn<>("Datos");
        datosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDatos()));

        // Add columns to TableView
        fileSystem.getColumns().addAll(directionColumn, datosColumn);

        // Set the TableView to stretch columns to fill the available width
        fileSystem.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }


    private ObservableList<FileData> readFileAndCreateData(String fileName) throws IOException {
        ObservableList<FileData> data = FXCollections.observableArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length >= 2) {
                    data.add(new FileData(columns[0], columns[1]));
                }
            }
        }

        return data;
    }


}


