package modelo;

import java.util.Scanner;

public class Cardiac {
    private int cells;
    private int sizeCell;
    private String Memory[]; // Memory of CARDIAC Machine
    private int pc = 0;//Program counter
    private int acc=0;//Accumulator
    private String InReg="";//Instruction register
    private int opCode=0;
    private int operand=0;
    private Boolean Negative=false; //


    public Cardiac() {
        cells = 100;
        sizeCell = 3;//i.e. 0,1,2
        Memory = new String[cells];
    }

    /*Setters*/

    public void setCells(int cells) {
        this.cells = cells;
    }

    public void setSizeCell(int sizeCell) {
        this.sizeCell = sizeCell;
    }

    public void setMemory(String[] memory) {
        Memory = memory;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public void setInReg(String inReg) {
        InReg = inReg;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public void setOperand(int operand) {
        this.operand = operand;
    }

    public void setNegative(Boolean negative) {
        Negative = negative;
    }

    // Getters
    public int getCells() {
        return cells;
    }

    public int getSizeCell() {
        return sizeCell;
    }

    public String[] getMemory() {
        return Memory;
    }

    public int getPc() {
        return pc;
    }

    public int getAcc() {
        return acc;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getOperand() {
        return operand;
    }

    public String getInReg() {
        return InReg;
    }

    public Boolean getNegative() {
        return Negative;
    }



    // Auxiliary methods
    // This method receives a string in form 001-312-135 and returns every component in a array of strings
    // in the form [001,312,135] to work with these
    public String[] createCard(String card) {
        return card.split("-");
    }

    // Put the card in the memory
    public void loadCard(String[] card, int position) {
        for (int i = 0; i < card.length; ++i) {
            Memory[position + i] = card[i];
        }
    }

    // This function is to transform every integer to the format of the memory,i.e., array of string format
    public String toStr(int num) {
        String p = String.valueOf(num);
        if (p.length() < sizeCell) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (sizeCell - p.length()); i++)//The size that is needed to p
            {
                sb.append('0');
            }
            return sb.toString().concat(p).replaceAll(",", "");
        }
        return p;
    }

    //Transform strings for architecture of sizeCells=3 to architecture of every sizeCell
    public String[] transform(String[] card) { //Always receive instruction with 3 cells
        if (sizeCell == 3) {
            return card;
        }
        int numZeros = sizeCell - 3;
        String[] newCard = new String[card.length];
        int j = 0;
        for (String instruction : card) {
            String opCode = instruction.substring(0, 1);
            String dir = instruction.substring(1, 3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (numZeros); i++)//The size that is needed to p
            {
                sb.append('0');
            }
            newCard[j] = opCode.concat(sb.toString().concat(dir));
            ++j;
        }
        return newCard;
    }

    // Most difficult operations for cardiac
    public int input(int cell) {
        System.out.print("Input the content to the cell number " + cell + " : ");
        Scanner enter = new Scanner(System.in);
        return enter.nextInt();
    }

    public int shiftLeft(String p, int des) {
        if (des >= p.length())
            return 0;
        String prefix = p.substring(des);// From des to end of instruction
        StringBuffer suffix = new StringBuffer(); // suffix is the new end of the string
        for (int i = 0; i < des; ++i)
            suffix.append('0');
        return Integer.parseInt(prefix.concat(suffix.toString()).replaceAll(",", ""));
    }

    public int shiftRight(String p, int des) {
        if (des >= p.length())
            return 0;
        String suffix = p.substring(0, sizeCell - des);
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < (sizeCell - des); ++i)
            prefix.append('0');
        return Integer.parseInt(prefix.toString().concat(suffix).replaceAll(",", ""));
    }


    // Methods to make  CARDIAC works
    // It is the first instruction to make CARDIAC wake up
    public void start() {
        Memory[0] = "001";
        Memory[cells - 1] = "800"; // In the last cell
    }



}

