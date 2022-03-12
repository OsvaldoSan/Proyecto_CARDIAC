package modelo;

import java.util.Scanner;

public class  Cardiac {
    private int cells;//Amount of cells of the memory
    private int sizeCell; // Size in characters, depends on cells
    private String Memory[]; // Memory of CARDIAC Machine
    private int pc;//Program counter
    private int acc;//Accumulator
    private String InReg;//Instruction register
    private int opCode;
    private int operand;
    private Boolean Negative; //


    public Cardiac(int cells) {
        this.cells = cells;
        sizeCell = Integer.toString(cells).length();// Each cell needs 3 spaces when the total amount is 100
        Memory = new String[cells];
        pc=0;
        acc=0;
        InReg=null;
        opCode=0;
        operand=0;
        Negative=false;
    }
    public Cardiac(){
        this(100); // The default option is cells=100
    }

    /*Setters*/
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

    // Put the entire card in the memory
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
    // Receive a card with a list of instructions
    public String[] transformSpace(String[] card) { //Always receive instruction with 3 cells
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

    // Most difficult basic operations for cardiac
    public int input(int cell) { // Input since terminal
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
        //We use the index 0 because we only pass one value
        // We use this because if Cardiac needs another architecture this will change the architecture of the instructions
        Memory[0] = transformSpace(new String[]{"001"})[0];
        Memory[cells - 1] = transformSpace(new String[]{"800"})[0]; // In the last cell
    }

}

