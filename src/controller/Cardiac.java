package controller;

// There is an infinite cycle when there is a "load" form an empty element
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

public class Cardiac implements Initializable {
    //The model and all of its parameters are not necessary, but it is clearer keep in mind what are the parts
    //that cardiac needs, although in the future could be good for save information.
    // Cardiac Variables
    private modelo.Cardiac cardiac;

    private String InReg;
    private String[] Memory;
    private int opCode,operand,acc,pc,sizeCell;

    // GUI variables
    // Internal variable of output that gOutput uses
    private String output;
    @FXML // This label must use before any new variable that exists in the FXML
    private Label gInReg,gOpCode,gOperand,gPc,gAcc, gTerminalNote,gOutput,gCycleNumber,gCardiacStatus,gOperation;
    @FXML
    private TextField gTerminalText;
    @FXML
    private TextArea gDeckText;
    @FXML
    private Button gTerminalRun, gAddCard,gStart,gPause,gStop,gRestart;
    @FXML
    private GridPane gridMemory= new GridPane(); //Is the grid pane that will have each cell, there is not intialize in Scene Builder, because the length will be decided with the sizeCell
    //it could be without label
    @FXML
    private ScrollPane scrollMemory;//Is the area whit scroll that has the grid
    @FXML
    private StackPane stackCardsInWaitingList;
    @FXML
    private ChoiceBox<String> tempos;

    // The size can change in function of the size of the cells
    private StackPane itemsDirection[]= new StackPane[100],itemsContent[]=new StackPane[100];
    private Label gContentMemory[]=new Label[100], gDirectionMemory[]=new Label[100];
    private ListView<String> cardsWaitingList;

    //Scheduling variables
    private int TIME=1400;
    private Timeline timeline ;
    //Control variables
    private Boolean isInput=false, isStarted=false, isPause=false;
    private int cycleNumber=0;

    private Queue<String> cards;

    // Final Variables
    private final String STATUS="CARDIAC is ";


    /* ----------------- Methods that are the main connection with the GUI ----------------------------------*/

    @FXML // This bar controls when is started,paused or stopped Cardiac
    public void controlBar(ActionEvent event){
        Object button=event.getSource();
        if(button.equals(gStart) && isStarted==false) {
            tempoControl();
            gCardiacStatus.setText(STATUS+"working");
            // Active Cardiac
            cardiac = new modelo.Cardiac();
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
        else if( (button.equals(gStop) || button.equals(gRestart) ) && isStarted==true){
            gCardiacStatus.setText(STATUS+"off");
            setCardiacParameters();//If you want to save the state of the virtual machine in the future with an upgrade in the code

            // Use new function to stop CARDIAC
            stopCVM();
            cards.clear();

            isStarted=false;
            if(button.equals(gRestart)){
                gStart.fire(); //Throws the event to start a new Cardiac machine
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
            gTerminalNote.setText("Thanks!");
            Memory[operand]= cardiac.toStr(gTerminalText.getText());
            gTerminalText.clear();
            changePC(pc,pc+1);
            updateMemoryValuesG();
            timeline.play();
        }

        //Add Card
        if(button.equals(gAddCard)){
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

    /* -------------- Creation of GUI -------------------------------*/
    /*Create the grid Memory*/
    public void createGridMemory(){
        final int cells=100;//This will change
        int column=0,row=0;

        scrollMemory.setContent(gridMemory);// Put the grid into the scroll
        scrollMemory.setPannable(true); // What is this?

        // It searches in the style sheet defined for this module
        gridMemory.getStyleClass().add("grid");

        for(int i=0;i<100;++i){

            //There are two columns in each row, the direction and the content of the direction
            // Example with six direction that needs 3 columns and two rows
            // dir : cont dir:cont dir:cont
            // dir: cont dir:cont dir:cont
            if(row==10){
                row=0;
                column+=2;//because it must jump the content
            }
            /*Add a new element to the grid
            * gDirectionMemory have a list of labels with direction, and itemsDirection have a list of panes that will receive that label
            * First we add the direction, the we add the content
            * */
            //Create item
            itemsDirection[i]=new StackPane();
            itemsDirection[i].getStyleClass().add("itemDirection");
            //Add label direction
            if(i<10) {
                gDirectionMemory[i]= new Label("0"+Integer.toString(i)+" :");
            }
            else {
                gDirectionMemory[i] = new Label(Integer.toString(i)+" :");
            }

            gDirectionMemory[i].getStyleClass().add("labelDirection");
            itemsDirection[i].getChildren().add(gDirectionMemory[i]);

            //Puts the restrictions to this pane in which column and row will be inside the grid memory
            addConstraintsGrid(itemsDirection[i],column++,row);//We put column++ because below we use that value of the column to the content
            //Add item to grid
            gridMemory.getChildren().add(itemsDirection[i]);

            // Add the content pane to the grid
            //Create Label with empty content
            gContentMemory[i]= new Label("    ");
            if(Memory[i]!=null){
                //System.out.println("La memoria es "+Memory[i]);
                gContentMemory[i].setText(Memory[i] );
            }
            gContentMemory[i].getStyleClass().add("labelContent");
            //Create item and add label content
            itemsContent[i]=new StackPane();
            itemsContent[i].getStyleClass().add("itemContent");
            itemsContent[i].getChildren().add(gContentMemory[i]);

            //ADD Item
            addConstraintsGrid(itemsContent[i],column--,row++); //Is column-- because for every cycle
            //we put in row 0 and column 0 a direction and in column 1 a content, but next will be row 1 and column 0
            gridMemory.getChildren().add(itemsContent[i]);

        }

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
        for(int i=0;i<100;++i){
            if(Memory[i]!= gContentMemory[i].getText()){
                if(Memory[i]==null){
                    gContentMemory[i].setText("    ");
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
            cardsWaitingList =new ListView<String>( FXCollections.observableArrayList("Nothing Here","Neither here") );
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
        wait(TIME);//Time in miliseconds
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
        //clearMemoryParametersG();

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
            TIME=1500;
            return;
        }
        String tempo=tempos.getValue();
        if(tempo=="Fast"){
            TIME=200;
        }
        else if(tempo=="Normal"){
            TIME=1500;
        }
        else{ // The option for slow tempo
            TIME=2500;
        }
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
                gOutput.setText(output);
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
            if(jump==false) { changePC(pc,pc+1); }
            updateMemoryValuesG();
        }

    public void wait(int n){
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb){
        //FXCollections.observableArrayList("Fast","Normal","Slow");
        tempos.getItems().addAll("Fast","Normal","Slow");
        tempos.setValue("Normal");
    }




    /* ------------------------------- Connection with the model -----------------------------------*/
    // Get Cardiac parameters from the model
    public void getCardiacParameters(){
        InReg=cardiac.getInReg();
        Memory=cardiac.getMemory();
        opCode = cardiac.getOpCode();
        operand = cardiac.getOperand();
        acc = cardiac.getAcc();
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
        cardiac.setPc(pc);
    }


}

