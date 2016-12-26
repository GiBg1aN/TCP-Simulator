package tcpImplementations;

import components.Channel;
import components.DataSegment;
import components.Event;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public class Tahoe implements TCP {
    private int size;
    private int ssthresh;
    private int segmentsToSend;
    private int seqNumber;
    private List<Integer> congestionWindow;
    private User user;
    private int lastAck;
    private int repeatedAck;
    
    
    public Tahoe(User user) {
        this.size = MyConstants.MSS;
        this.ssthresh = MyConstants.SSTHRESH;
        this.lastAck = -1;
        this.repeatedAck = 0;
        this.congestionWindow = new LinkedList<>();
        this.user = user;
    }
    
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (ack.getSeq() == lastAck ) {
            repeatedAck++;
            if (repeatedAck > 3) 
                decreaseCongestionWindow();
        } else if (congestionWindow.contains(ack.getSeq())) {
            System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();
            
            Iterator<Integer> iterator = congestionWindow.iterator();
            while(iterator.hasNext()){
                Integer item = iterator.next();
                if (item <= ack.getSeq()) {
                    FEL.getInstance().removeTimeoutEvent(item);
                    iterator.remove();
                }
            }
            
            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            if (congestionWindow.isEmpty()) {
                System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + user.getID() + " ends transmission" + (char) 27 + "[0m");
                // restart(); TODO
            }
            return true;
        }
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + (char) 27 + "[31m" + " - DUPLICATE" + (char) 27 + "[0m");
        return false;
    }
    
    @Override
    public void startTransmission(int segmentsToSend) {this.segmentsToSend = segmentsToSend;
        while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
            sendSegment();
        }
    }
    
    @Override
    public void increaseCongestionWindow() { 
        size = (size < ssthresh) ? size * 2 : size + 1; 
        System.out.println("------------------------------------------------"
                + "INCREASED CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);     
    } //TODO: pensare al superamento esponenziale di ssthresh

    @Override
    public void decreaseCongestionWindow() {
        ssthresh = size / 2;
        size = MyConstants.MSS;
        System.out.println("------------------------------------------------"
                + "DECREASED CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);        
    }

    @Override
    public void restart() {
        seqNumber = 0;
        size = MyConstants.MSS;
        ssthresh = MyConstants.SSTHRESH;
        lastAck = -1;
        repeatedAck = 0;
        double timestamp = FEL.getInstance().getSimTime() + 0.3; // TODO
        FEL.getInstance().scheduleNextEvent(new Event(timestamp, user));
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
    public void timeout(int seqNumber) {
        decreaseCongestionWindow();
        sendSegment(seqNumber);
    }
    
    @Override
    public int size() { return this.size; }
}
