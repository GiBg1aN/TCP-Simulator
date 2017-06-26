package tcpImplementations;

import components.DataSegment;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;

/**
 * This class models the Tahoe behaviour.
 */
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
        if (ack.getSeq() == lastAck) {
            repeatedAck++;
            if (repeatedAck > 3) {
                repeatedAck = 0;
                decreaseCongestionWindow();
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

    @Override
    public void increaseCongestionWindow() {
        size = (size < ssthresh) ? size * 2 : size + 1;
        ssthresh = (size > ssthresh) ? size : ssthresh;
    }

    @Override
    public void decreaseCongestionWindow() {
        ssthresh = size / 2;
        if (ssthresh == 0)
            ssthresh++;
        size = MyConstants.MSS;
    }

    @Override
    public void restart() {
        super.restart();
        ssthresh = MyConstants.SSTHRESH;
        lastAck = -1;
        repeatedAck = 0;
    }
}
