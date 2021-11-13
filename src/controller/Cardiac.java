package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Cardiac {
    //The model and all of its parameters are not necessary, but it is clearer keep in mind what are the parts
    //that cardiac needs, although in the future could be good for save information.
    // Cardiac Variables
    modelo.Cardiac cardiac = new modelo.Cardiac();

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
    private Button gTerminalRun;

    @FXML
    private GridPane gridMemory= new GridPane();
    @FXML
    private ScrollPane scrollMemory ;
    private Label cellMemory[]=new Label[100], numberMemory[]=new Label[100];


    //ejecucion más lenta elejida por el usuario
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1300), e -> cycleSystem() ));
    //Control variables
    private Boolean isInput=false, isStarted=false;
    private String newInput;
    private int cycleNumber=0;




    @FXML
    public void startTerminal(ActionEvent event){
        if(isStarted==false) {
            isStarted=true;
            timeline.setCycleCount(Animation.INDEFINITE);
            cardiac.start();
            updateCardiacParameters();
            createGridMemory();
            timeline.play();
        }
    }

    @FXML
    public void runTerminal(ActionEvent event){

        if(isInput==true){
            isInput=false;
            gTerminalNote.setText("Gracias!");
            Memory[operand]= gTerminalText.getText();
            updateMemoryParametersG();
            timeline.play();
        }

    }


    public void createGridMemory(){
        final int cells=100;
        int column=0,row=0;
        scrollMemory.setContent(gridMemory);
        scrollMemory.setPannable(true);

        gridMemory.getStyleClass().add("grid");


       /*gridMemory.setMinWidth(Region.USE_COMPUTED_SIZE);
        gridMemory.setPrefWidth(Region.USE_COMPUTED_SIZE);
        gridMemory.setMaxWidth(Region.USE_PREF_SIZE);*/

        //set grid height
        /*gridMemory.setMinHeight(Region.USE_COMPUTED_SIZE);
        gridMemory.setPrefHeight(Region.USE_COMPUTED_SIZE);
        gridMemory.setMaxHeight(Region.USE_PREF_SIZE);*/

        for(int i=0;i<100;++i){

            if(row==10){//20 because there will be a column for the number and a column for the content
                row=0;
                column+=2;//because it must jump the content
            }
            numberMemory[i]= new Label(Integer.toString(i));
            numberMemory[i].getStyleClass().clear();
            numberMemory[i].getStyleClass().add("labelDirection");

            addConstraintsGrid(numberMemory[i],column++,row);

            gridMemory.getChildren().add(numberMemory[i]);

            cellMemory[i]= new Label("N/A");
            if(Memory[i]!=null){
                System.out.println("La memoria es "+Memory[i]);
                cellMemory[i].setText(Memory[i] );
            }
            cellMemory[i].getStyleClass().clear();
            cellMemory[i].getStyleClass().add("labelContent");

            addConstraintsGrid(cellMemory[i],column--,row++); //Is column-- because for every cycle
            //we put in row 0 and column 0 a direction and in column 1 a content, but next will be roq 1 and column 0



            gridMemory.getChildren().add(cellMemory[i]);

        }

    }

    public void addConstraintsGrid(Label memory,int x,int y){
        GridPane.setConstraints(memory,x,y);
        GridPane.setVgrow(memory, Priority.ALWAYS);
        GridPane.setHgrow(memory, Priority.ALWAYS);

    }

    public void updateMemoryParametersG(){
        for(int i=0;i<100;++i){
            if(Memory[i]!=cellMemory[i].getText()){
                if(Memory[i]==null){
                    cellMemory[i].setText("N/A");
                }
                else{
                    cellMemory[i].setText(Memory[i]);
                }
            }
        }
    }

    public void updateCardiacParameters(){
        InReg=cardiac.getInReg();
        Memory=cardiac.getMemory();
        opCode = cardiac.getOpCode();
        operand = cardiac.getOperand();
        acc = cardiac.getAcc();
        pc = cardiac.getPc();
        sizeCell= cardiac.getSizeCell();
    }

    public void updateContentG(){
        gInReg.setText(InReg);
        gOpCode.setText(Integer.toString(opCode));
        gOperand.setText(Integer.toString(operand));
        gPc.setText(Integer.toString(pc));
        gAcc.setText(Integer.toString(acc));


        //gCycleNumber.setText(Integer.toString(cycleNumber));
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

        switch (opCode){
            case 0:
                gTerminalNote.setText("Ingrese el contenido para la celda "+operand);
                isInput=true;
                timeline.pause();
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



}

