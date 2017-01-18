package components;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import mainPackage.EventType;

/**
 * Rappresenta la Future Event List.
 */
public class FEL {
    private final List<Event> fel;
    private static final FEL instance = new FEL(); // Pattern Singleton.
    private double simTime;
    
    
    private FEL() {
        fel = new LinkedList<>();
    }

    public static FEL getInstance() { return instance; }
    
    /* Ritorna l'evento con il timestamp minimo */
    public Event getNextEvent() { 
        Event next = fel.stream().min(Comparator.comparing(e -> e.getTimestamp())).get();
        simTime = next.getTimestamp();
        fel.remove(next);
        return next;
    }

    public void scheduleNextEvent(Event event) { fel.add(event); }
    
    /**
     * Se l'ack del segmento inviato è arrivato correttamente il relativo timeout
     * viene annullato.
     * @param seqNumber numero di sequenza del segmento.
     * @param userID    id dell'utente.
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
    
    
    /* GETTER E SETTER */
    public double getSimTime() { return simTime; }
    
    public void setSimTime(double simTime) { this.simTime = simTime; }
}
