package tcpImplementations;

import components.Channel;
import components.DataSegment;
import components.Event;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public class AIMD implements TCP {
    private int size;
    private int segmentsToSend;
    private int seqNumber;
    private List<Integer> congestionWindow;
    private User user;
    
    public AIMD(User user) {
        this.size = MyConstants.MSS;
        this.congestionWindow = new LinkedList<>();
        this.user = user;
    }
    
    @Override
    public void startTransmission(int segmentsToSend) {
        this.segmentsToSend = segmentsToSend;
        while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
            sendSegment();
        }
    }
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (congestionWindow.contains(ack.getSeq())) {
            System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();
            congestionWindow.remove(Integer.valueOf(ack.getSeq()));
            FEL.getInstance().removeTimeoutEvent(ack.getSeq());
            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            if (congestionWindow.isEmpty()) {
                System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + user.getID() + " ends transmission" + (char) 27 + "[0m");
                // restart();
            }
            return true;
        }
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + (char) 27 + "[31m" + " - DUPLICATE" + (char) 27 + "[0m");
        return false;
    }
    
    private void sendSegment() {
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " sends segment number: " + seqNumber + (char) 27 + "[0m");
        MySegment segment = new DataSegment(this.user, this.seqNumber, FEL.getInstance().getSimTime());
        Channel.getInstance().enqueueSegment(segment);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MyConstants.TIMEOUT, segment));
        congestionWindow.add(seqNumber);
        seqNumber++;
    }
    
    private void sendSegment(int seqNumber) {
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " REsends segment number: " + seqNumber + (char) 27 + "[0m");        
        MySegment segment = new DataSegment(this.user, seqNumber, FEL.getInstance().getSimTime());
        Channel.getInstance().enqueueSegment(segment);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MyConstants.TIMEOUT, segment));
    }

    @Override
    public void increaseCongestionWindow() { size++; }

    @Override
    public void decreaseCongestionWindow() { size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; }

    @Override
    public void restart() {
        seqNumber = 0;
        size = MyConstants.MSS;
        double timestamp = FEL.getInstance().getSimTime() + 0.3; // TODO
        FEL.getInstance().scheduleNextEvent(new Event(timestamp, user));
    }
    
    @Override
    public void timeout(int seqNumber) {
        decreaseCongestionWindow();
        sendSegment(seqNumber);
    }
    
    @Override
    public int size() { return size; }
}
