package components;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mainPackage.EventType;
import mainPackage.MyConstants;
import static mainPackage.MyConstants.*;


public class Channel extends LinkedList<MySegment> {
    private static final int MAX_LENGTH = T;
    private static final Channel instance = new Channel();
    private static final Map<Integer, List<Integer>> cumulativeAcks = new TreeMap<>(); // TODO: controllare se il final crea problemi
    private static final LinkedList<MySegment> travelling = new LinkedList<>();
    
    
    private Channel() {}
    
    public static Channel getInstance() { return instance; }

    public void startTravel(MySegment segm) {
        travelling.addLast(segm);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + TRAVEL_TIME, EventType.TRAVEL));
    }
    
    public void enqueueSegment() {
        MySegment segm = travelling.removeFirst();
        if (size() < MAX_LENGTH) {
            addLast(segm);
        }
    }

    public void dequeueSegment() {
        if (!isEmpty()) {
            MySegment s = removeFirst();
            if (MyConstants.segmentNotCorrupted()) {
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
                System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31mSEGMENT CORRUPTED!" + (char) 27 + "[0m");           
            }
        }
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MU, EventType.CH_SOLVING));
    }
    
    public void resetChannelForUser(int id) { cumulativeAcks.remove(id); }
    
    private void sendAcknowledgement(DataSegment segm) {
        int lastAck = getLastAcknowledgement(segm);        
        MySegment ack = new AckSegment(segm.getUser(), lastAck, segm);
        Channel.getInstance().startTravel(ack);
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
