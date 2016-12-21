package components;

public class FEL {
    private final double[] fel;
    private final int size;

    
    public FEL(int nUsers){
        fel = new double[nUsers + 1];
        size = nUsers + 1;
    }
    
    public int getNextEvent() {
        double min = fel[0];
        int index = 0;
        for (int i = 1; i < size; i++) {
            if (fel[i] < min) {
                min = fel[i];
                index = i;
            }
        }
        return index;
    }
    
    public void scheduleNextEvent(int index) {
        
    }
    
    public double getEventTime(int index) {
        return fel[index];
    }
    
    public void setEventTime(int index, double time) {
        this.fel[index] += time;
    }
}
