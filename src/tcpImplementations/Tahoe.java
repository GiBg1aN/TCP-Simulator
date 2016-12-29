package tcpImplementations;

import components.Event;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;


public class Tahoe extends TCPCommonLayer implements TCP {
    protected int ssthresh;
    protected int lastAck;
    protected int repeatedAck;
    
    
    public Tahoe(User user) {
        super(user);
        this.ssthresh = MyConstants.SSTHRESH;
        this.lastAck = -1;
        this.repeatedAck = 0;
    }
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (ack.getSeq() == lastAck ) {
            repeatedAck++;
            if (repeatedAck > 3) {
                repeatedAck = 0;
                decreaseCongestionWindow();
            }
        } else if (congestionWindow.contains(ack.getSeq())) {
            lastAck = ack.getSeq();
            System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();
            
            Iterator<Integer> iterator = congestionWindow.iterator();
            while(iterator.hasNext()) {
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
}
