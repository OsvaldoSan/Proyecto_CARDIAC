package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Cardiac {
    //The model and all of its parameters are not necessary, but it is clearer keep in mind what are the parts
    //that cardiac needs, although in the future could be good for save information.
    // Cardiac Variables
    modelo.Cardiac cardiac;

    private String InReg;
    private String[] Memory;
    private int opCode;
    private int operand;
    private int acc;
    private int pc ;
    private int sizeCell;

    // Graphic variables
    private String output;

    @FXML
    private Label gInReg,gOpCode,gOperand,gPc,gAcc, gTerminalNote,gOutput,gCycleNumber;

    @FXML
    private TextField gTerminalText;

    @FXML
    private TextArea gDeckText;

    @FXML
    private Button gTerminalRun, gAddCard,gStart,gPause,gStop,gRestart;

    @FXML
    private GridPane gridMemory= new GridPane();
    @FXML
    private ScrollPane scrollMemory;
    @FXML
    private StackPane stackCardsInSystem;

    private StackPane itemsDirection[]= new StackPane[100],itemsContent[]=new StackPane[100];
    private Label contentMemory[]=new Label[100], directionMemory[]=new Label[100];
    private ListView<String> cardsSystem;


    //ejecucion más lenta elejida por el usuario
    private int TIME=1400;
    private Timeline timeline ;
    //Control variables
    private Boolean isInput=false, isStarted=false, isPause=false;
    private int cycleNumber=0;
    private Queue<String> cards;


    @FXML
    public void controlBar(ActionEvent event){
        Object button=event.getSource();
        if(button.equals(gStart) && isStarted==false) {
            cardiac = new modelo.Cardiac();
            timeline = new Timeline(new KeyFrame(Duration.millis(TIME), e -> cycleSystem() ));
            cards =new LinkedList<>();

            isStarted=true;
            timeline.setCycleCount(Animation.INDEFINITE);
            cardiac.start();
            updateCardsInSystem();
            getCardiacParameters();
            createGridMemory();
            timeline.play();
        }
        else if( (button.equals(gStop) || button.equals(gRestart) ) && isStarted==true){
            setCardiacParameters();//If you want to save the state of the virtual machine in the future with a better in the code
            clearContentG();
            clearMemoryParametersG();
            cards.clear();
            isStarted=false;
            cardiac = new modelo.Cardiac();
            if(button.equals(gStart)){
                gStart.fire(); //Throws the event to start a new Cardiac machine
            }
        }
        else if( button.equals(gPause) ){
            if(isPause=false){
                timeline.pause();
                isPause=true;
                gPause.setText("Play");
            }
            else{
                timeline.play();
                isPause=false;
                gPause.setText("Pause");
            }


        }
    }

    @FXML
    public void execution(ActionEvent event){
        Object button=event.getSource();
        if(button.equals(gTerminalRun) && isInput==true){
            isInput=false;
            gTerminalNote.setText("Thanks!");
            Memory[operand]= gTerminalText.getText();
            gTerminalText.clear();
            updateMemoryParametersG();
            timeline.play();
        }

        //Add Card
        if(button.equals(gAddCard)){
            cards.addAll(Arrays.asList( gDeckText.getText().split("\n") ));
            gDeckText.clear();
            updateCardsInSystem();
            if(isInput==true){
                takeCardFromQueue();
                isInput=false;
                updateCardsInSystem();
                timeline.play();
            }

        }



    }


    public void createGridMemory(){
        final int cells=100;
        int column=0,row=0;
        scrollMemory.setContent(gridMemory);
        scrollMemory.setPannable(true);

        gridMemory.getStyleClass().add("grid");

        for(int i=0;i<100;++i){

            if(row==10){//20 because there will be a column for the number and a column for the content
                row=0;
                column+=2;//because it must jump the content
            }
            //Create item
            itemsDirection[i]=new StackPane();
            itemsDirection[i].getStyleClass().add("itemDirection");
            //Add label direction
            if(i<10) {
                directionMemory[i]= new Label("0"+Integer.toString(i)+" :");
            }
            else {
                directionMemory[i] = new Label(Integer.toString(i)+" :");
            }

            directionMemory[i].getStyleClass().add("labelDirection");
            itemsDirection[i].getChildren().add(directionMemory[i]);

            addConstraintsGrid(itemsDirection[i],column++,row);
            //Add item to grid
            gridMemory.getChildren().add(itemsDirection[i]);

            //Create Label with content
            contentMemory[i]= new Label("    ");
            if(Memory[i]!=null){
                System.out.println("La memoria es "+Memory[i]);
                contentMemory[i].setText(Memory[i] );
            }
            contentMemory[i].getStyleClass().add("labelContent");
            //Create item and add label content
            itemsContent[i]=new StackPane();
            itemsContent[i].getStyleClass().add("itemContent");
            itemsContent[i].getChildren().add(contentMemory[i]);

            //ADD Item
            addConstraintsGrid(itemsContent[i],column--,row++); //Is column-- because for every cycle
            //we put in row 0 and column 0 a direction and in column 1 a content, but next will be roq 1 and column 0
            gridMemory.getChildren().add(itemsContent[i]);

        }

    }

    public void addConstraintsGrid(StackPane memory,int x,int y){
        GridPane.setConstraints(memory,x,y);
        GridPane.setVgrow(memory, Priority.ALWAYS);
        GridPane.setHgrow(memory, Priority.ALWAYS);

    }

    public void getCardiacParameters(){
        InReg=cardiac.getInReg();
        Memory=cardiac.getMemory();
        opCode = cardiac.getOpCode();
        operand = cardiac.getOperand();
        acc = cardiac.getAcc();
        pc = cardiac.getPc();
        sizeCell= cardiac.getSizeCell();
    }
    public void updateMemoryParametersG(){
        for(int i=0;i<100;++i){
            if(Memory[i]!= contentMemory[i].getText()){
                if(Memory[i]==null){
                    contentMemory[i].setText("    ");
                }
                else{
                    contentMemory[i].setText(Memory[i]);
                }
            }
        }
    }

    public void clearMemoryParametersG(){
        for(int i=0;i<100;++i){
                    contentMemory[i].setText("    ");
        }
    }

    public void setCardiacParameters(){
        cardiac.setInReg(InReg);
        cardiac.setMemory(Memory);
        cardiac.setOpCode(opCode);
        cardiac.setOperand(operand);
        cardiac.setAcc(acc);
        cardiac.setPc(pc);
    }

    public void updateContentG(){
        gInReg.setText(InReg);
        gOpCode.setText(Integer.toString(opCode));
        gOperand.setText(Integer.toString(operand));
        gPc.setText(Integer.toString(pc));
        gAcc.setText(Integer.toString(acc));


        gCycleNumber.setText(Integer.toString(cycleNumber));
    }

    public void clearContentG(){
        gInReg.setText("  ");
        gOpCode.setText(Integer.toString(0));
        gOperand.setText(Integer.toString(0));
        gPc.setText(Integer.toString(0));
        gAcc.setText(Integer.toString(0));


        gCycleNumber.setText(Integer.toString(0));
    }

    public void updateCardsInSystem(){

        if(cards.isEmpty()==false){
            cardsSystem=new ListView<String>(FXCollections.observableArrayList(cards.toString().substring(1).split("\\[|,[ ]|\\]") ));
        }
        else{
            cardsSystem=new ListView<String>( FXCollections.observableArrayList("Nothing Here","Neither here") );
        }

        // If cardsSystem is not in Stack Pane we add it
        if(cardsSystem.getParent() == null){
            stackCardsInSystem.getChildren().add(cardsSystem);
        }
    }

    public void takeCardFromQueue(){
        Memory[operand]=cards.remove();
        updateMemoryParametersG();
        wait(TIME);//Time in miliseconds
    }

    public void cycleSystem() {
        cycleNumber++;

        output = "null";//Every cycle this variable is restart
        int o1, o2;//Auxiliaries in shift
        boolean jump=false;

        if(Memory[pc]!=null){
            InReg = Memory[pc];
            opCode = Integer.parseInt(Memory[pc].substring(0, 1));
            operand = Integer.parseInt(Memory[pc].substring(1));
        }
        else{
            InReg = null;
            opCode = 0;
            operand = 0;
            updateContentG();
            timeline.stop();
        }

        updateContentG();
        String prueba;
        switch (opCode){
            case 0:
                gTerminalNote.setText("Ingrese el contenido para la celda "+operand);
                if(cards.isEmpty() == false){
                    takeCardFromQueue();
                }
                else {
                    isInput = true;
                    timeline.pause();
                }
                break;
            case 1:
                acc = Integer.parseInt(Memory[operand]);
                break;
            case 2:
                acc += Integer.parseInt(Memory[operand]);
                break;
            case 3:
                if (acc < 0) {
                    pc = operand;
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
                pc = operand;
                jump=true;
                break;
            case 9:
                pc = operand;
                jump=true;
                System.out.println("Termino de programa");
                break;

        }
            if(jump==false) { pc += 1; }
            updateMemoryParametersG();
        }

    public void wait(int n){
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}

