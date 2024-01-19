package modelo;

public class CardiacSync extends Cardiac{
    private String SecondaryMemory[] = {"0000","0001","0002"};
    private String bootloaderContent[]={"0008"};
    private String starterStatus;
    private int cycleLimitSwitch;
    private int cycleSwitcherCounter=0;
    private boolean switcherStatus=false;
    private int directionStartPreSO;
    private int directionUserStart;
    private int directionUserEnd;
    private int directionChargeStart;
    private int directionChargeEnd;
    private int directionHaltSO;
    private int lastDirectionSO;
    private int directionSaverJump;

    public int getDirectionUserStart(){return directionUserStart;}
    public int getDirectionUserEnd(){return directionUserEnd;}
    public int getDirectionChargeStart(){return directionChargeStart;}
    public int getDirectionChargeEnd(){return directionChargeEnd;}
    public int getDirectionHaltSO(){return  directionHaltSO;}
    public int getLastDirectionSO(){return lastDirectionSO;}
    public int getDirectionSaverJump(){return directionSaverJump;}


    public void setStarterStatus(String status){ starterStatus =status;}
    public void setCycleLimitSwitch(int cycleLimit){cycleLimitSwitch=cycleLimit;}
    public void setCycleSwitcherCounter(int counter){cycleSwitcherCounter=counter;}
    public void setSwitcherStatus(boolean switcher){switcherStatus=switcher;}


    public String getStarterStatus(){return starterStatus;}
    public int getCycleLimitSwitch(){return cycleLimitSwitch;}
    public int getCycleSwitcherCounter(){return cycleSwitcherCounter;}
    public boolean getSwitcherStatus(){return switcherStatus;}
    public int getDirectionStartPreSO(){return directionStartPreSO;}


    public CardiacSync(int cells,int directionPreSo,int cycleLimit,int directionssuerstart,int directionuserend,int dirHalt,int lastdirectionso,int directionsj){
        super(validationCells(cells));
        directionStartPreSO =directionPreSo;
        cycleLimitSwitch=cycleLimit;
        directionChargeStart=0;
        directionChargeEnd=3;
        directionUserStart=directionssuerstart;
        directionUserEnd=directionuserend;
        directionHaltSO=dirHalt;
        lastDirectionSO=lastdirectionso;
        directionSaverJump=directionsj;
        //Read the file to charge the second memory

    }

    // Preprocess methods
    private static int validationCells(int cells){
        if(cells < 1000) cells=1000; // CARDIAC SYNC needs at least 1000 cells to work
        return cells;
    }

    // Override the startVM


}
