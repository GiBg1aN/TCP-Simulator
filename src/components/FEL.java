package components;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import mainPackage.EventType;


public class FEL {
    private final List<Event> fel;
    private static final FEL instance = new FEL();
    private double simTime;
    
    
    private FEL() {
        fel = new LinkedList<>();
    }

    public static FEL getInstance() { return instance; }
    
    /* Return the event with minimum timestamp */
    public Event getNextEvent() { 
        Event next = fel.stream().min(Comparator.comparing(e -> e.getTimestamp())).get();
        simTime = next.getTimestamp();
        fel.remove(next);
        return next;
    }

    /* Add a new event in the FEL */
    public void scheduleNextEvent(Event event) { fel.add(event); }
    
    public void removeTimeoutEvent(int seqNumber) {
        for (Event e : fel) {
            if (e.getEventType() == EventType.TIMEOUT && e.getSegment().getSeq() == seqNumber) {
                fel.remove(e);
                break;
            }
        }
    }
    
    
    /* GETTER E SETTER */
    public double getSimTime() { return simTime; }
    
    public void setSimTime(double simTime) { this.simTime = simTime; }
}
