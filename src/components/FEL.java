package components;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import mainPackage.EventType;

/**
 * This class models the Future Event List.
 */
public class FEL {
    private final List<Event> fel = new LinkedList<>();
    private double simTime;
    
    /*
     * Returns the event with minimum timestamp in the future event list
     */
    public Event getNextEvent() {
        Event next = fel.stream().min(Comparator.comparing(e -> e.getTimestamp())).get();
        simTime = next.getTimestamp();
        fel.remove(next);
        return next;
    }

    public void scheduleNextEvent(Event event) { fel.add(event); }
    
    /*
     * When a data segment is received correctly its timeout event will be
     * removed from FEL.
     */
    public void removeTimeoutEvent(int seqNumber, int userID) {
        for (int i = 0; i < fel.size(); i++) {
            if (fel.get(i).getEventType() == EventType.TIMEOUT && fel.get(i).getSegment().getSeq() == seqNumber
                    && fel.get(i).getSegment().getUser().getID() == userID) {
                fel.remove(fel.get(i));
                i--;
            }
        }
    }
    
    
    /* GETTER */
    public double getSimTime() { return simTime; }
}
