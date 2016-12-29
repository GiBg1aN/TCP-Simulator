package components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mainPackage.EventType;
import static mainPackage.MyConstants.*;


public class Channel extends LinkedList<MySegment> {
    private static final int MAX_LENGTH = T;
    private static final Channel instance = new Channel();
    private static final Map<Integer, List<Integer>> cumulativeAcks = new TreeMap<>(); //TODO: controllare se il final crea problemi
    
    private Channel() {}
    
    public static Channel getInstance() { return instance; }

    public void enqueueSegment(MySegment segm) {
        if (size() < MAX_LENGTH) {
            addLast(segm);
        }
    }

    public void dequeueSegment() {
        if (!isEmpty()) {
            MySegment s = removeFirst();
            if (s.getClass() == DataSegment.class ) {
                if (!cumulativeAcks.containsKey(s.getUser().getID())) {
                    cumulativeAcks.put(s.getUser().getID(), new LinkedList<>());
                }
                (cumulativeAcks.get(s.getUser().getID())).add(s.getSeq());
                sendAcknowledgement((DataSegment) s);
            } else {
                s.getUser().receiveAck(s);
            }
        }
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + 0.01, EventType.CH_SOLVING));
    }
    
    private void sendAcknowledgement(DataSegment segm) {
        int lastAck = getLastAcknowledgement(segm);        
        MySegment ack = new AckSegment(segm.getUser(), lastAck, segm);
        Channel.getInstance().enqueueSegment(ack);
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[34mAdversary sends ack number: " + ack.getSeq() + (char) 27 + "[0m");           
    }
    
    private int getLastAcknowledgement(DataSegment segm){
        List<Integer> tmp = cumulativeAcks.get(segm.getUser().getID());
        int min = -1;
        for (Integer i : tmp) {
            if (i == min + 1) {
                min++;
            }
        }
        return min;
    }
}
