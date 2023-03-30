package controller;

// There is an infinite cycle when there is a "load" form an empty element
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;

import static javafx.geometry.HPos.CENTER;

public class Cardiac implements Initializable {
    //The model and all of its parameters are not necessary, but it is clearer keep in mind what are the parts
    //that cardiac needs, although in the future could be good for save information.
    // Cardiac Variables
    private modelo.Cardiac cardiac;

    private String InReg;
    private String[] Memory;
    private int opCode,operand,acc,pc,sizeCell;
    private Boolean negative;

    private int totalCells=100;

    // GUI variables
    // Internal variable of output that gOutput uses
    private String output;
    @FXML // This label must use before any new variable that exists in the FXML
    private Label gInReg,gOpCode,gOperand,gPc,gAcc,gNegative, gTerminalNote,gCycleNumber,gCardiacStatus,gOperation,gStatusDownload;
    @FXML
    private TextField gTerminalText;
    @FXML
    private TextArea gDeckText;
    @FXML
    private Button gTerminalRun, gAddCard, gStartStop,gPause,gRestart,gDownloadOutput,gBackHome;
    @FXML
    private GridPane gridMemory= new GridPane(); //Is the grid pane that will have each cell, there is not intiallize in Scene Builder, because the length will be decided with the sizeCell
    @FXML
    private StackPane stackGridMemory = new StackPane();
    //it could be without label
    @FXML
    private ScrollPane scrollMemory;//Is the area whit scroll that has the grid
    @FXML
    private StackPane stackCardsInWaitingList;
    @FXML
    private ChoiceBox<String> tempos, architecture;
    @FXML
    private ListView<String> outputCardsList;

    // The size will be set in the method to make the gridPane
    private StackPane itemsDirection[];
    private StackPane itemsContent[];
    private Label gContentMemory[];
    private Label gDirectionMemory[];
    private ListView<String> cardsWaitingList;

    //Timing variables variables
    private int TIME=1600;
    private Timeline timeline ;
    //Control variables
    private Boolean isInput=false, isStarted=false, isPause=false;
    private int cycleNumber=0;

    private Queue<String> cards;

    // Final Variables
    private final String STATUS="CARDIAC is ";

    // Style variables
    // Max columns to the gridpane, this could change to have better performance
    private final int MAX_COLUMNS = 10;


    /* ----------------- Methods that are the main connection with the GUI ----------------------------------*/

    @FXML // This bar controls when is started,paused or stopped Cardiac
    public void controlBar(ActionEvent event){
        Object button=event.getSource();
        if(button.equals(gStartStop) && isStarted==false) {
            gStartStop.setText("Stop");
            tempoControl();
            architectureControl();
            gCardiacStatus.setText(STATUS+"working");
            // Active Cardiac
            cardiac = new modelo.Cardiac(totalCells);
            // timeline knows what has to do, but is still stopped
            timeline = new Timeline(new KeyFrame(Duration.millis(TIME), e -> cycleSystem() ));
            timeline.setCycleCount(Animation.INDEFINITE); // The amount of cycles for the timeline is set

            // The list of cards is set
            cards =new LinkedList<>();

            isStarted=true;
            // Initialize the Cardiac Variables
            cardiac.startCVM();

            updateCardsInWaitingList();
            getCardiacParameters();
            createGridMemory();
            changePC(0,0);// This is to select the 0 cell

            // Start the cycle
            timeline.play();
        }
        else if( (button.equals(gStartStop) || button.equals(gRestart) ) && isStarted==true){
            gStartStop.setText("Start");
            gCardiacStatus.setText(STATUS+"off");
            setCardiacParameters();//If you want to save the state of the virtual machine in the future with an upgrade in the code

            // Use new function to stop CARDIAC
            stopCVM();
            cards.clear();

            isStarted=false;
            if(button.equals(gRestart)){
                gStartStop.fire(); //Throws the event to start a new Cardiac machine
            }
        }
        else if( button.equals(gPause) ){
            if(isPause==false){
                isPause=true;
                gPause.setText("Play");
                timeline.pause();
            }
            else{
                timeline.play();
                isPause=false;
                gPause.setText("Pause");
            }


        }
    }

    // This event actioned by gTerminalRun or gAddCard, is the method that throws events to the action
    //The system always give priority to the Waiting List
    @FXML
    public void execution(ActionEvent event){
        Object button=event.getSource();
        if(button.equals(gTerminalRun) && isInput==true){
            isInput=false;
            gTerminalNote.setText("Done!");
            Memory[operand]= cardiac.toStr(gTerminalText.getText());
            gTerminalText.clear();
            changePC(pc,pc+1);
            updateMemoryValuesG();
            timeline.play();
        }

        //Add Card
        else if(button.equals(gAddCard)){
            cards.addAll(Arrays.asList( gDeckText.getText().split("\n") ));
            //Erase all if there is not an input time
            gDeckText.clear();
            updateCardsInWaitingList();
            if(isInput==true){
                takeCardFromQueue();
                isInput=false;
                //pc++;
                changePC(pc,pc+1);
                timeline.play();
            }

        }
    }

    /* -------------- Download ---------------------- */

    public void downloadOutput(ActionEvent event){
        Object button = event.getSource();

        if ( button.equals(gDownloadOutput)){
            try{
                String home = System.getProperty("user.home");
                String originDownloads=home+"/Downloads";
                if(Files.exists(Path.of(originDownloads)) == false)
                    originDownloads=home;//If the path doesn't exist, it uses the home path

                System.out.println(home);
                File saveFile = new File(originDownloads+"/CARDIAC_Output_" + new Timestamp(System.currentTimeMillis()) + ".txt");
                saveFile.createNewFile();
                Files.write(saveFile.toPath(), outputCardsList.getItems().subList(0, outputCardsList.getItems().size()));
                gStatusDownload.setText("Downloaded");
            }
            catch(IOException e){
                gStatusDownload.setText("Error in download");
                System.out.println("An error occurred");
                e.printStackTrace();
            }
        }
    }

    /*------------- Change of Stages ---------------*/
    public void changeStages(ActionEvent event){
        // Alert Class to confirm return
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("If you return now you'll lose all progress");

        Object Button = event.getSource();

        if(Button.equals(gBackHome)){
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK ) {
                try {
                    //To get the window and execute in the same window
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    Parent newPage = FXMLLoader.load(getClass().getResource(Main.FXMLWELCOME));
                    Scene scene = new Scene(newPage);
                    scene.getStylesheets().add(getClass().getResource(Main.STYLESCSS).toExternalForm());

                    stage.setScene(scene);
                    stage.setTitle(Main.TITLE);
                    stage.show();

                } catch (IOException e) {
                    System.out.println("Error in the change of window");
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("THe user doesn't authorize");
            }
        }

    }

    /* -------------- Creation of GUI -------------------------------*/
    /*Create the grid Memory*/
    public void createGridMemory() {
        //If there is children already in grid pane this will clean the content
        gridMemory.getChildren().clear();

        final int cells = cardiac.getCells();//This will change
        int MAX_ROWS = cells / MAX_COLUMNS;
        int column = 0, row = 0;

        //Assign the size to each element in the grid
        itemsDirection= new StackPane[cells] ;
        itemsContent=new StackPane[cells];
        gContentMemory=new Label[cells];
        gDirectionMemory=new Label[cells];

        scrollMemory.setContent(gridMemory);// Put the grid into the scroll
        scrollMemory.setPannable(false); // To move with  mouse


        // It searches in the style sheet defined for this module
        gridMemory.getStyleClass().add("grid");
        GridPane.setHalignment(gridMemory, CENTER);
        GridPane.setValignment(gridMemory, VPos.CENTER);

        for(int i=0;i<cells;++i){

            //There are two columns in each row, the direction and the content of the direction
            // Example with six direction that needs 3 columns and two rows
            // dir : cont dir:cont dir:cont
            // dir: cont dir:cont dir:cont
            if(column==(MAX_COLUMNS*2)){
                column=0;
                row+=1;//because it must jump the content
            }
            /*Add a new element to the grid
            * gDirectionMemory have a list of labels with direction, and itemsDirection have a list of panes that will receive that label
            * First we add the direction, then we add the content
            * */
            //Create item
            itemsDirection[i]=new StackPane();
            itemsDirection[i].getStyleClass().add("itemDirection");
            //Add label direction
            if(i<MAX_ROWS) {
                //The amount of zeros is sizeCell- len of i because will be used to put 001 or 091 depend on the case, and -1 because
                // the size of the cell is with one extra value than direction
                gDirectionMemory[i]= new Label("0".repeat(cardiac.getSizeCell()-1-Integer.toString(i).length())+Integer.toString(i)+" :");
            }
            else {
                gDirectionMemory[i] = new Label(Integer.toString(i)+" :");
            }

            gDirectionMemory[i].getStyleClass().add("labelDirection");
            itemsDirection[i].getChildren().add(gDirectionMemory[i]);
            gDirectionMemory[i].setTextAlignment(TextAlignment.LEFT);
            //gDirectionMemory[i].setAlignment(Pos.CENTER);
            itemsDirection[i].setAlignment(Pos.BOTTOM_LEFT);

            //Puts the restrictions to this pane in which column and row will be inside the grid memory
            addConstraintsGrid(itemsDirection[i],column++,row);//We put column++ because below we use that value of the column to the content
            //Add item to grid
            gridMemory.getChildren().add(itemsDirection[i]);
            GridPane.setHalignment(itemsDirection[i],HPos.LEFT);

            // Add the content pane to the grid
            //Create Label with empty content
            gContentMemory[i]= new Label(" ".repeat(cardiac.getSizeCell()));
            if(Memory[i]!=null){
                //System.out.println("The memory is "+Memory[i]);
                gContentMemory[i].setText(Memory[i] );
            }
            gContentMemory[i].getStyleClass().add("labelContent");
            //Create item and add label content
            itemsContent[i]=new StackPane();
            itemsContent[i].getStyleClass().add("itemContent");
            itemsContent[i].getChildren().add(gContentMemory[i]);

            //ADD Item
            addConstraintsGrid(itemsContent[i],column++,row); //Is column-- because for every cycle
            //we put in row 0 and column 0 a direction and in column 1 a content, but next will be row 1 and column 0
            gridMemory.getChildren().add(itemsContent[i]);

            //Allign labels

        }

        //scrollMemory.setFitToWidth(true);
    }

    // it's used to define constraints to the grid
    public void addConstraintsGrid(StackPane memory,int x,int y){
        GridPane.setConstraints(memory,x,y);// Define in which column and row will be put the Pane
        GridPane.setVgrow(memory, Priority.ALWAYS);// Define when the Vertical side will grow along the window
        GridPane.setHgrow(memory, Priority.ALWAYS);

    }

    /* ------------------------ Update Values of the GUI ------------------------- */

    // Values allocated in the Memory System are set in the graphic contentMemory
    // It is less efficient
    public void updateMemoryValuesG(){
        for(int i=0;i< cardiac.getCells();++i){
            if(Memory[i]!= gContentMemory[i].getText()){
                if(Memory[i]==null){
                    gContentMemory[i].setText(" ".repeat(cardiac.getSizeCell()));
                }
                else{
                    gContentMemory[i].setText(Memory[i]);
                }
            }
        }
    }

    public void updateContentG(){
        gInReg.setText(InReg);
        gOpCode.setText(Integer.toString(opCode));
        gOperand.setText(Integer.toString(operand));
        gPc.setText(Integer.toString(pc));
        gAcc.setText(Integer.toString(acc));
        gNegative.setText(negative.toString());

        updateOperationTextG(); // Updates the value of gOperation
        gCycleNumber.setText(Integer.toString(cycleNumber));

    }

    //Updates the value of gOperation that shows to the user which operation is do it
    public void updateOperationTextG(){
        switch(opCode){
            case 0:
                gOperation.setText("Input");
                break;
            case 1:
                gOperation.setText("Load");
                break;
            case 2:
                gOperation.setText("Add");
                break;
            case 3:
                gOperation.setText("Branch if less than zero");
                break;
            case 4:
                gOperation.setText("Shift");
                break;
            case 5:
                gOperation.setText("Output");
                break;
            case 6:
                gOperation.setText("Store");
                break;
            case 7:
                gOperation.setText("Subtraction");
                break;
            case 8:
                gOperation.setText("Jump");
                break;
            case 9:
                gOperation.setText("Halt");
                break;
            default:
                gOperation.setText(" ");
                break;
        }
    }

    /* ------------------------------- Objects to control the execution of the VM -----------------------------------*/
    // Change the pc in the system and in the GUI, because it has to change the style of the item
    public void changePC(int actualPC, int nextPC){
        pc=nextPC;
        if(actualPC>=0) {
            itemsDirection[actualPC].getStyleClass().clear();
            itemsDirection[actualPC].getStyleClass().add("itemDirection");
        }
        itemsDirection[nextPC].getStyleClass().add("itemDirectionSelected");
    }

    /*Control the Waiting List and the  stop*/

    //Is for the options that charge a complete deck
    // It is not optimized, because it use all the queue each time that a new element is get out of the queue
    public void updateCardsInWaitingList(){
        //cards is the queue
        //cardsWaitingList is the List view that shows which instructions are waiting his turn
        //stackCardsInWaitingList is the pane that has the list in the GUI
        if(cards.isEmpty()==false){
            //toString returns an arrays of elements ex: ["Hola","Adios"], with substring(1) we take the first bracket and with the split regex
            // we have the words individual in a list of each word
            cardsWaitingList =new ListView<String>(FXCollections.observableArrayList(cards.toString().substring(1).split("\\[|,[ ]|\\]") ));
        }
        else{
            cardsWaitingList =new ListView<String>( );
        }

        // If cardsSystem is not in Stack Pane we add it
        if(cardsWaitingList.getParent() == null){
            stackCardsInWaitingList.getChildren().add(cardsWaitingList);
        }
    }

    // Take an instruction from the queue "cards" and put them into Memory
    // This requires that operand will be updated since the place where this method is called
    public void takeCardFromQueue(){
        Memory[operand]= cardiac.toStr(cards.remove());
        updateMemoryValuesG();
        updateCardsInWaitingList();
        //wait(TIME);//Time in miliseconds
    }

    //Transform every variable to null, including the memory and update the values of the GUI
    public void stopCVM(){
        InReg = null;
        opCode = 0;
        operand = 0;
        cycleNumber=0;
        acc=0;
        pc=0;
        Arrays.fill(Memory,null);//This clean the Memory of the machine
        updateContentG();
        updateMemoryValuesG();

        //To restart all the values
        //
        scrollMemory.setContent(new AnchorPane());

        timeline.stop();
    }

    // Stops the Virtual Machine by itself
    public void emergencyStop(){
        stopCVM();
        gCardiacStatus.setText(STATUS+"dead, please restart");
    }

    // Controls the speed of each cycle in the VM
    public void tempoControl(){
        // Tempos is the list that have the different tempos, if there is nothing selected takes the default
        if(tempos.getValue()==null){
            gCardiacStatus.setText("Normal speed will be set");
            TIME=1600;
            return;
        }
        String tempo=tempos.getValue();
        if(tempo=="Fast"){
            TIME=200;
        }
        else if(tempo=="Normal"){
            TIME=1600;
        }
        else{ // The option for slow tempo
            TIME=4000;
        }
    }

    // Controls the amount of cells in the VM
    public void architectureControl(){
        // Tempos is the list that have the different tempos, if there is nothing selected takes the default
        if(architecture.getValue()==null){
            gCardiacStatus.setText("Architecture selected");
            totalCells=100;
            return;
        }
        totalCells = Integer.parseInt(architecture.getValue());

    }


    /* ------------------- Main Method that lead every change -----------------------------*/
    /*Control the cycle and times*/
    public void cycleSystem() {
        cycleNumber++;

        output = "null";//Every cycle this variable is restart
        int o1, o2;//Auxiliaries in shift
        boolean jump=false;

        if(Memory[pc]!=null){
            InReg = Memory[pc];
           // System.out.println("Memoria en pc != null "+Memory[pc]);
            opCode = Integer.parseInt(Memory[pc].substring(0, 1));
            operand = Integer.parseInt(Memory[pc].substring(1));
        }
        else{
            System.out.println("Memory of PC is null, pc=="+pc);
            emergencyStop();
        }

        updateContentG();

        if( (Memory[operand]==null) && ( (opCode==1) || (opCode==2) || (opCode==7) ) ){ //Security 2,1,7
            System.out.println("Memory of operand is null and opcode=="+opCode);
            emergencyStop();
        }

        switch (opCode){
            case 0:
                gTerminalNote.setText("Waiting for input to the cell "+operand);
                if(!cards.isEmpty()) takeCardFromQueue();//Always will give priority to get out the cards on the waiting list
                else {
                    isInput = true;
                    jump=true;//We simulate a jump to not add a cycle to the pc here, we'll add it in the ActionEvent Button
                    //Only when isInput==true we will add one to the pc
                    timeline.pause();
                }
                break;
            case 1:
                System.out.println(" Case 1  Operand :"+operand+"  Memory:" +Memory[operand]);
                acc = Integer.parseInt(Memory[operand]);
                break;
            case 2:
                System.out.println(" Case 2  Operand :"+operand+"  Memory:" +Memory[operand]);
                acc += Integer.parseInt(Memory[operand]);
                break;
            case 3:
                if (acc < 0) {
                    changePC(pc,operand);
                }
                break;
            case 4:
                o1 = Memory[pc].charAt(sizeCell - 2);
                o2 = Memory[pc].charAt(sizeCell - 1);
                acc = cardiac.shiftLeft(cardiac.toStr(acc), o1);
                acc = cardiac.shiftRight(cardiac.toStr(acc), o2);
                break;
            case 5:
                output = Memory[operand];
                System.out.println("Salida :"+output);
                outputCardsList.getItems().add(output);
                //gOutput.setText(output);
                break;
            case 6:
                Memory[operand] = cardiac.toStr(acc);
                break;
            case 7:
                acc -= Integer.parseInt(Memory[operand]);
                break;
            case 8:
                changePC(pc,operand);
                jump=true;
                break;
            case 9:
                changePC(pc,operand);
                jump=true;
                System.out.println("Program ended");
                break;

        }
            if (acc<0) negative=true;
            else negative =false;

            if(jump==false) { changePC(pc,pc+1); }
            updateMemoryValuesG();
        }




    @Override
    public void initialize(URL url, ResourceBundle rb){
        //FXCollections.observableArrayList("Fast","Normal","Slow");
        tempos.getItems().addAll("Fast","Normal","Slow");
        tempos.setValue("Normal");

        // The possibles architectures in amount of cells are put here
        architecture.getItems().addAll("100","1000","10000");
        architecture.setValue("100");
    }




    /* ------------------------------- Connection with the model -----------------------------------*/
    // Get Cardiac parameters from the model
    public void getCardiacParameters(){
        InReg=cardiac.getInReg();
        Memory=cardiac.getMemory();
        opCode = cardiac.getOpCode();
        operand = cardiac.getOperand();
        acc = cardiac.getAcc();
        negative=cardiac.getNegative();
        pc = cardiac.getPc();
        sizeCell= cardiac.getSizeCell();
    }

    // Set parameters to the model
    public void setCardiacParameters(){
        cardiac.setInReg(InReg);
        cardiac.setMemory(Memory);
        cardiac.setOpCode(opCode);
        cardiac.setOperand(operand);
        cardiac.setAcc(acc);
        cardiac.setNegative(negative);
        cardiac.setPc(pc);
    }


}

