package components;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mainPackage.EventType;
import static mainPackage.MyConstants.*;

/**
 * Rappresenta il canale dove viaggiano i segmenti.
 */
public class Channel {
    private static final int MAX_LENGTH = T;
    private final LinkedList<MySegment> queue = new LinkedList<>();
    private final Map<Integer, List<Integer>> cumulativeAcks = new TreeMap<>();
    private final LinkedList<DataSegment> travellingData = new LinkedList<>(); // Rappresenta i segmenti data in transito.
    private final LinkedList<AckSegment> travellingAck = new LinkedList<>(); // Rappresenta i segmenti ack in transito.
    private final Thread t;

    public Channel(Thread t) {
        this.t = t;
    }

    /**
     * Inserisce un segmento in transito.
     *
     * @param segm segmento da elaborare.
     */
    public void startTravel(MySegment segm) {
        if (segm instanceof DataSegment) {
            travellingData.addLast((DataSegment) segm);
            Monitor.getInstance().getFEL(t).scheduleNextEvent((new Event(Monitor.getInstance().getFEL(t).getSimTime() + TRAVEL_TIME, EventType.TRAVEL_DATA)));
        } else {
            travellingAck.addLast((AckSegment) segm);
            Monitor.getInstance().getFEL(t).scheduleNextEvent((new Event(Monitor.getInstance().getFEL(t).getSimTime() + TRAVEL_TIME, EventType.TRAVEL_ACK)));
        }
    }

    /*
     * Inserisce un segmento in transito all'interno del canale.
     */
    public void enqueueSegment(EventType event) {
        MySegment segm = (event == EventType.TRAVEL_DATA) ? travellingData.removeFirst() : travellingAck.removeFirst();
        if (queue.size() < MAX_LENGTH) {
            queue.addLast(segm);
        } else {
            Monitor.getInstance().getStatistic(Thread.currentThread()).increaseDroppedSegmentNumber();
        }
    }

    /**
     * Estrae un segmento dal canale, se è arrivato correttamente nel caso in
     * cui sia di tipo <code>DataSegment</code> allora inserisce un ack nel
     * canale, altrimenti nel caso in cui sia di tipo <code>AckSegment</code> lo
     * spedisce all'utente.
     */
    public void dequeueSegment() {
        if (!queue.isEmpty()) {
            MySegment s = queue.removeFirst();

            if (Monitor.getInstance().isSegmentNotCorrupted(Thread.currentThread())) {
                if (s.getClass() == DataSegment.class) {
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
                Monitor.getInstance().getStatistic(Thread.currentThread()).increaseCorruptedSegmentsNumber();
            }
        }
        Monitor.getInstance().getFEL(t).scheduleNextEvent(new Event(Monitor.getInstance().getFEL(t).getSimTime() + MU, EventType.CH_SOLVING));
    }

    public void resetChannelForUser(int id) {
        cumulativeAcks.remove(id);
    }

    /**
     * Mette in transito un segmento di tipo <code>AckSegment</code>.
     *
     * @param segm segmento di riferimento.
     */
    private void sendAcknowledgement(DataSegment segm) {
        int lastAck = getLastAcknowledgement(segm.getUser().getID());
        MySegment ack = new AckSegment(segm.getUser(), lastAck, segm);
        Monitor.getInstance().getChannel(t).startTravel(ack);
        //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[34mAdversary sends ack number: " + ack.getSeq() + (char) 27 + "[0m");           
    }

    /**
     * Ritorna il numero di sequenza dell'ultimo segmento arrivato
     * correttamente.
     *
     * @param id id dell'utente di riferimento.
     * @return il numero di sequenza dell'ultimo segmento arrivato
     * correttamente.
     */
    private int getLastAcknowledgement(int id) {
        List<Integer> tmp = cumulativeAcks.get(id);
        int min = -1;
        for (Integer i : tmp) {
            if (i == min + 1) {
                min++;
            }
        }
        return min;
    }
    
    public int size() { return queue.size(); }
}
