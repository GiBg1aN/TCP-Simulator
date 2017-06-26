package tcpImplementations;

import components.DataSegment;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;

/**
 * This class models the AIMD behaviour.
 */
public class AIMD extends TCPCommonLayer implements TCP {    
    public AIMD(User user) {
        super(user);
    }    
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
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
    
    @Override
    public void increaseCongestionWindow() { size++; }

    @Override
    public void decreaseCongestionWindow() { size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; }
}
