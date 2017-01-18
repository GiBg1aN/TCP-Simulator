package components;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mainPackage.EventType;
import mainPackage.MyConstants;
import static mainPackage.MyConstants.*;
import statistics.Statistics;

/**
 * Rappresenta il canale dove viaggiano i segmenti.
 */
public class Channel extends LinkedList<MySegment> {
    private static final int MAX_LENGTH = T;
    private static final Channel instance = new Channel(); // Pattern Singleton
    private static final Map<Integer, List<Integer>> cumulativeAcks = new TreeMap<>();
    private static final LinkedList<MySegment> travelling = new LinkedList<>(); // Rappresenta i pacchetti in transito.
    
    
    private Channel() {}
    
    public static Channel getInstance() { return instance; }

    /**
     * Inserisce un segmento in transito.
     * @param segm      segmento da elaborare.
     */
    public void startTravel(MySegment segm) {
        travelling.addLast(segm);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + TRAVEL_TIME, EventType.TRAVEL));
    }
    
    /**
     * Inserisce un segmento in transito all'interno del canale.
     */
    public void enqueueSegment() {
        MySegment segm = travelling.removeFirst();
        if (size() < MAX_LENGTH) {
            addLast(segm);
        }
    }

    /**
     * Estrae un segmento dal canale, se è arrivato correttamente nel caso in cui
     * sia di tipo <code>DataSegment</code> allora inserisce un ack nel canale,
     * altrimenti nel caso in cui sia di tipo <code>AckSegment</code> lo spedisce
     * all'utente.
     */
    public void dequeueSegment() {
        if (!isEmpty()) {
            MySegment s = removeFirst();
            if (MyConstants.isSegmentNotCorrupted()) {
                if (s.getClass() == DataSegment.class ) {
                    if (!cumulativeAcks.containsKey(s.getUser().getID())) {
                        cumulativeAcks.put(s.getUser().getID(), new LinkedList<>());
                    }
                    (cumulativeAcks.get(s.getUser().getID())).add(s.getSeq());
                    sendAcknowledgement((DataSegment) s);
                } else {
                    s.getUser().receiveAck(s);
                }
            } else {
                //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31mSEGMENT CORRUPTED!" + (char) 27 + "[0m");           
                Statistics.increaseCorruptedSegmentsNumber();
            }
        }
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MU, EventType.CH_SOLVING));
    }
    
    public void resetChannelForUser(int id) { cumulativeAcks.remove(id); }
    
    /**
     * Mette in transito un segmento di tipo <code>AckSegment</code>.
     * @param segm 
     */
    private void sendAcknowledgement(DataSegment segm) {
        int lastAck = getLastAcknowledgement(segm.getUser().getID());        
        MySegment ack = new AckSegment(segm.getUser(), lastAck, segm);
        Channel.getInstance().startTravel(ack);
        //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[34mAdversary sends ack number: " + ack.getSeq() + (char) 27 + "[0m");           
    }
    
    /**
     * Ritorna il numero di sequenza dell'ultimo segmento arrivato correttamente.
     * @param id    id dell'utente di riferimento.
     * @return      il numero di sequenza dell'ultimo segmento arrivato correttamente.
     */
    private int getLastAcknowledgement(int id){
        List<Integer> tmp = cumulativeAcks.get(id);
        int min = -1;
        for (Integer i : tmp) {
            if (i == min + 1) {
                min++;
            }
        }
        return min;
    }
}
