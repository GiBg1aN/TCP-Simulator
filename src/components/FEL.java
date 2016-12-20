package components;

public class FEL {
    private double[] FEL;
    private int size;
    
    public FEL(int nUsers){
        FEL = new double[nUsers + 1];
        size = nUsers + 1;
    }
    
    public int getNextEvent() {
        double min = FEL[0];
        int index = 0;
        for(int i = 1; i < size; i++){
            if(FEL[i]<min){
                min = FEL[i];
                index = i;
            }
        }
        return index;
    }
}
