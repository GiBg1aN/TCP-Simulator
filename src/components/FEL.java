package components;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class FEL {
    private final List<Event> fel;
    private final FEL instance = new FEL();
    private double simTime;
    
    private FEL() {
        fel = new LinkedList<>();
    }

    
    public FEL getInstance() { return instance; }
    
    /* Return the event with minimum timestamp */
    public Event getNextEvent() {
        return fel.stream().min(Comparator.comparing(e -> e.getTimestamp())).get();
    }

    public void scheduleNextEvent(int index) {}

    /* Add a new event in the FEL */
    public void scheduleNextEvent(Event event) {
        fel.add(event);
    }
    
    public double getSimTime() { return simTime; }
    
    public void setSimTime(double simTime) { this.simTime = simTime; }
}
