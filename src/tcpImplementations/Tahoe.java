package tcpImplementations;

import components.DataSegment;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;
import statistics.Statistics;


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
        } else if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
            lastAck = ack.getSeq();
            //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();
            
            Iterator<DataSegment> iterator = congestionWindow.iterator();
            while(iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    FEL.getInstance().removeTimeoutEvent(item.getSeq());
                    item.setReceivedTime(FEL.getInstance().getSimTime());
                    Statistics.refreshResponseTimeStatistics(item);
                    iterator.remove();
                }
            }
            
            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            if (congestionWindow.isEmpty()) {
                //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + user.getID() + " ends transmission" + (char) 27 + "[0m");
                restart();
            }
            return true;
        }
        //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + (char) 27 + "[31m" + " - DUPLICATE" + (char) 27 + "[0m");
        return false;
    }
    
    @Override
    public void increaseCongestionWindow() { 
        size = (size < ssthresh) ? size * 2 : size + 1; 
        //System.out.println("------------------------------------------------"
          //      + "INCREASED CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);     
    } //TODO: pensare al superamento esponenziale di ssthresh

    @Override
    public void decreaseCongestionWindow() {
        ssthresh = size / 2;
        size = MyConstants.MSS;
        //System.out.println("------------------------------------------------"
          //      + "DECREASED CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);        
    }

    @Override
    public void restart() {
        super.restart();
        ssthresh = MyConstants.SSTHRESH;
        lastAck = -1;
        repeatedAck = 0;
    }    
}
