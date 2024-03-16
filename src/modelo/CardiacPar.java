package modelo;

public class CardiacPar extends CardiacSync {

    private int dirWaiter; // Direction where waiter is activated
    private int dirIdCounterZV;// Direction where is the id counter of the ZV


    public CardiacPar(int cells, int directionPreSo, int cycleLimit, int directionssuerstart, int directionuserend, int dirHalt, int lastdirectionso, int directionsj, int dirstaticp, int dirwaiter, int diridcounter) {
        super(validationCells(cells), directionPreSo, cycleLimit, directionssuerstart, directionuserend, dirHalt, lastdirectionso, directionsj, dirstaticp);
        dirWaiter=dirwaiter;
        dirIdCounterZV=diridcounter;
    }

    public int getDirWaiter(){return dirWaiter;}
    public int getDirIdCounterZV(){return dirIdCounterZV;}


}
