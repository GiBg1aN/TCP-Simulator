package components;

import java.util.LinkedList;
import java.util.List;

public class FEL {
    private final List<Event> fel;
    private final FEL instance = new FEL();
    private double simTime;
    
    private FEL() {
        fel = new LinkedList<>();
    }
    
    public FEL getInstance() {
        return instance;
    }
    
    /* Return the event with minimum timestamp */
    public Event getNextEvent() {
        double min = fel.get(0).getTimestamp();
        int index = 0;
        for (int i = 1; i < fel.size(); i++) {
            if (fel.get(i).getTimestamp() < min) {
                min = fel.get(i).getTimestamp();
                index = i;
            }
        }
        return fel.get(index);
    }
    
    /* Add a new event in the FEL */
    public void scheduleNextEvent(Event event) {
        fel.add(event);
    }
    
    public double getSimTime() {
        return simTime;
    }
    
    public void setSimTime(double simTime) {
        this.simTime = simTime;
    }
}
