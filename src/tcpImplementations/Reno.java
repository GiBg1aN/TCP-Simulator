package tcpImplementations;

import components.DataSegment;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.Iterator;

/**
 * This class models the Reno behaviour.
 */
public class Reno extends Tahoe implements TCP {
    public Reno(User user) {
        super(user);
    }
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (ack.getSeq() == lastAck ) {
            repeatedAck++;
            if (repeatedAck > 3) {
                repeatedAck = 0;
                fastRecovery();
            }
        } else if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
            lastAck = ack.getSeq();
            increaseCongestionWindow();
            
            Iterator<DataSegment> iterator = congestionWindow.iterator();
            while (iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    Monitor.getInstance().getFEL(Thread.currentThread()).removeTimeoutEvent(item.getSeq(), item.getUser().getID());
                    item.setReceivedTimestamp(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime());
                    Monitor.getInstance().getStatistic(Thread.currentThread()).refreshResponseTimeStatistics(item, true);      
                    
                    this.devRTT = Monitor.getInstance().getStatistic(Thread.currentThread()).devRTT(this.devRTT, item);
                    timeout = Monitor.getInstance().getStatistic(Thread.currentThread()).ERTT() + (4 * this.devRTT);
                    iterator.remove();
                }
            }
            
            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            
            if (congestionWindow.isEmpty()) {
                restart();
            }
            return true;
        }
        return false;
    }
    
    private void fastRecovery() {
        ssthresh = size / 2;
        if (ssthresh == 0)
            ssthresh++;
        size = ssthresh;
    }
}
