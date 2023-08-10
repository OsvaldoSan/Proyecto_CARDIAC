package modelo;

public class CardiacSync extends Cardiac{
    private String SecondaryMemory[] = {"0000","0001","0002"};
    private String bootloaderContent[]={"0008"};
    private String StarterStatus;
    private int intSwitcher;
    private String strSwitcher;


    public void setStarterStatus(String status){ StarterStatus=status;}
    public void setIntSwitcher(int switcher){intSwitcher=switcher;}
    public void setStrSwitcher(String switcher){strSwitcher=switcher;}

    public String getStarterStatus(){return StarterStatus;}
    public String getStrSwitcher(){return strSwitcher;}
    public int getIntSwitcher(){return intSwitcher;}

    public CardiacSync(int cells){
        super(validationCells(cells));
        //Read the file to charge the second memory

    }

    // Preprocess methods
    private static int validationCells(int cells){
        if(cells < 1000) cells=1000; // CARDIAC SYNC needs at least 1000 cells to work
        return cells;
    }

    // Override the startVM


}
