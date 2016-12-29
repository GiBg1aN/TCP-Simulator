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
    private static Map<User, List> cumulativeAcks = new TreeMap<>();
    
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
            if (s.getClass() == DataSegment.class) {
                sendAcknowledgement((DataSegment) s);
                System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[34mAdversary sends ack number: " + s.getSeq() + (char) 27 + "[0m");
            } else {
                // TODO
            }
        }
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + 0.01, EventType.CH_SOLVING));
    }
    
    private void sendAcknowledgement(DataSegment segm) {
        MySegment ack = new AckSegment(segm.getUser(), segm.getSeq(), segm);
        Channel.getInstance().enqueueSegment(ack);
    }
    
    private class receivedData {
        int lastCorrectlyReceived;
        List<Integer> dataWindow;
        
        public receivedData() {
            lastCorrectlyReceived = -1;
            dataWindow = new LinkedList<>();
        }

        public int getLastCorrectlyReceived() {
            return lastCorrectlyReceived;
        }

        public void add(Integer i) {
            dataWindow.add(i, i);
            Iterator<Integer> listIterator = dataWindow.iterator();
            while (listIterator.hasNext()) {
                // TODO
            }
        }
    }
}
