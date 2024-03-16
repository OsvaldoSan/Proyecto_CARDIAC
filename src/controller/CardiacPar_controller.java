package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CardiacPar_controller extends  CardiacSync_controller{

    // variables of cardiac
    protected String InReg2;
    protected int opCode2,operand2,acc2,pc2;
    protected Boolean negative2;

    // hardware variables
    private int cycleNumber2;

    // cardiac/hardware JFX variables
    public Label gOperation2= new Label("1"),gCycleNumber2 = new Label("100"),gWaiter = new Label("Stopped");
    protected Label gInReg2= new Label("2"),gOpCode2= new Label("3"),gOperand2= new Label(" 4"),gPc2= new Label("5"),gAcc2= new Label("6"),gNegative2= new Label("7");

    Label  descGInReg2= new Label("Instruction Register"),descGOpCode2=new Label("Operational Code"),descGOperand2=new Label("Operand"),descGAcc2=new Label("Accumulator"), descGPc2 =new Label("Program Counter"), descGNegative2=new Label("Negative");

    Label descGWaiter=new Label("Waiter"), descGOperation2=new Label("Operation CPUE"),descGCycleNumber2 = new Label("Cycle CPUE");



    // From Scene Builder
    @FXML
    protected VBox vBoxLeftSide;
    @FXML
    protected TitledPane titleCPU;
    @FXML
    protected AnchorPane anchorCPU;
    @FXML
    protected ScrollPane scrollCPU;
    @FXML
    protected GridPane gridPCPU;


    // Complete new Layout for vbox
    protected TabPane tabCPU;
    protected Tab tabCPU1,tabCPU2 ;
    protected AnchorPane anchorCPU2;
    protected ScrollPane scrollCPU2;
    protected GridPane gridCPU2;


    // Conecction with the SO
    protected String fileNameSO="/home/mrblue/Documents/Proyectos/Tesis_MAC/Cardiac_VM/src/modelo/Files/File_System_SO_Parallel.txt";

    @FXML
    protected void editMachineStatus(){
        // Grid pane for Machine Status
        // Set the CSS Config
        gCycleNumber2.setId("LabelNumeric");

        // Definitions for Machine Status
        System.out.println("Gridpane value: "+gridPMachineStatus);
        // Define the height of the new row equal to all the original rows
        RowConstraints newRowConstraints = new RowConstraints();
        newRowConstraints.setPrefHeight(gridPMachineStatus.getRowConstraints().get(0).getPrefHeight());
        // Get the number of rows
        int newRow = gridPMachineStatus.getRowCount();
        // Add the new row and the constraints

        gridPMachineStatus.addRow(newRow,descGCycleNumber2,gCycleNumber2);
        gridPMachineStatus.addRow(newRow+1,descGOperation2,gOperation2);



    }

    @Override
    public void editTitleCPU(){
        // CSS definitions for labels
        gStarter.setId("labelNumeric"); //Moved to CPULoader
        gInReg2.setId("labelNumeric");
        gOpCode2.setId("labelNumeric");
        gOperand2.setId("labelNumeric");
        gPc2.setId("labelNumeric");
        gAcc2.setId("labelNumeric");
        gNegative2.setId("labelNumeric");
        gCycleLimitSwitch.setId("labelNumeric");
        gSwitcherCycleCounter.setId("labelNumeric");
        gSwitcher.setId("labelNumeric");
        gWaiter.setId("labelNumeric");


        gSwitcher.setId("labelNumeric"); // Moved to CPUE
        gCycleLimitSwitch.setId("labelNumeric");// Moved to CPUE
        gSwitcherCycleCounter.setId("labelNumeric");// Moved to CPUE

        //Create new Tab Pane
        tabCPU = new TabPane();
        VBox.setVgrow(tabCPU, Priority.NEVER);

        // Create the first tab with existing components
        tabCPU1 = new Tab("CPU Loader");
        tabCPU1.setClosable(false); // Disable closing of this tab
        tabCPU1.setContent(anchorCPU);

        // Create the second tab with new components
        tabCPU2 = new Tab("CPU Executor");
        tabCPU2.setClosable(false); // Disable closing of this tab
        anchorCPU2 = new AnchorPane();
        anchorCPU2.setPadding(new Insets(5));
        scrollCPU2 = new ScrollPane();
        //scrollCPU2.setPrefSize(595, 200);
        scrollCPU2.setVisible(true);
        //scrollCPU2.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        //scrollCPU2.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        AnchorPane.setTopAnchor(scrollCPU2, 2.0);
        AnchorPane.setBottomAnchor(scrollCPU2, 2.0);
        AnchorPane.setLeftAnchor(scrollCPU2, 2.0);
        AnchorPane.setRightAnchor(scrollCPU2, 2.0);
        //scrollCPU2.setPrefViewportWidth(500); // Set the preferred viewport width
        //scrollCPU2.setPrefViewportHeight(500); // Set the preferred viewport height
        gridCPU2 = new GridPane();
        // Add componentes to gridCPU2
        scrollCPU2.setContent(gridCPU2);
        anchorCPU2.getChildren().add(scrollCPU2);
        tabCPU2.setContent(anchorCPU2);

        // Add tabs to the tab pane
        tabCPU.getTabs().addAll(tabCPU1,tabCPU2);
        tabCPU.setPrefWidth(titleCPU.getPrefWidth());
        tabCPU.setPrefHeight(268);
        tabCPU.setMinHeight(titleCPU.getMinHeight());
        tabCPU.setMinWidth(titleCPU.getMinWidth());
        tabCPU.setMaxHeight(titleCPU.getMaxHeight());
        tabCPU.setMaxWidth(titleCPU.getMaxWidth());

        // Find and replace the TitledPane "titleCPU" with the TabPane "tabPCPU"
        for (int i = 0; i < vBoxLeftSide.getChildren().size(); i++) {
            Node node = vBoxLeftSide.getChildren().get(i);
            if (node instanceof TitledPane && node.getId() != null && node.getId().equals("titleCPU")) {
                System.out.println("It will found the titleCPU to remove and susbtitude");
                vBoxLeftSide.getChildren().remove(i);
                vBoxLeftSide.getChildren().add(i, tabCPU);
                break;
            }
        }

        // Definitions for new grid pane for the executor
        // Definitions for Machine Status
        System.out.println("Gridpane value: "+gridCPU2);
        // Copy properties from gridCPU to gridCPU2


        gridCPU2.getRowConstraints().addAll(gridPCPU.getRowConstraints());
        gridCPU2.setHgap(10); // Set horizontal gap to 10
        gridCPU2.setVgap(10);

        gridCPU2.getColumnConstraints().addAll(gridPCPU.getColumnConstraints());

        //gridCPU2.getChildren().setAll(gridPCPU.getChildren());
        //gridCPU2.setStyle(gridPCPU.getStyle());
        String css = getClass().getResource("../view/styles_to_cardiac.css").toExternalForm();
        gridCPU2.getStylesheets().add(css);
        gridCPU2.setGridLinesVisible(true);




        RowConstraints newRowConstraints = new RowConstraints();
        newRowConstraints.setPrefHeight(gridCPU2.getRowConstraints().get(0).getPrefHeight());

        // Add the new row and the constraints

        gridCPU2.addRow(0,descGInReg2,gInReg2);
        gridCPU2.addRow(1,descGOpCode2,gOpCode2);
        gridCPU2.addRow(2,descGOperand2,gOperand2);
        gridCPU2.addRow(3,descGPc2,gPc2);
        gridCPU2.addRow(4,descGAcc2,gAcc2);
        gridCPU2.addRow(5,descGNegative2,gNegative2);
        gridCPU2.addRow(6,gDescriptionCycleLimitSwitch,gCycleLimitSwitch);
        gridCPU2.addRow(7,gDescriptionSwitcherCycleCounter,gSwitcherCycleCounter);
        gridCPU2.addRow(8,gDescriptionSwitcher,gSwitcher);
        gridCPU2.addRow(9,descGWaiter,gWaiter);


    }


}
