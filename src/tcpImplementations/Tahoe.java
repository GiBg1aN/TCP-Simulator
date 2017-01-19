package tcpImplementations;

import components.DataSegment;
import components.FEL;
import components.Monitor;
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
        if (ack.getSeq() == lastAck) {
            repeatedAck++;
            if (repeatedAck > 3) {
                repeatedAck = 0;
                decreaseCongestionWindow();
            }
        } else if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
            lastAck = ack.getSeq();
            System.out.println("(" + Monitor.getFEL(Thread.currentThread()).getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();

            Iterator<DataSegment> iterator = congestionWindow.iterator();
            while (iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    Monitor.getFEL(Thread.currentThread()).removeTimeoutEvent(item.getSeq(), item.getUser().getID());
                    item.setReceivedTimestamp(Monitor.getFEL(Thread.currentThread()).getSimTime());
                    Monitor.getSTATISTIC(Thread.currentThread()).refreshResponseTimeStatistics(item);
                    
                    this.devRTT = Monitor.getSTATISTIC(Thread.currentThread()).getDevRTT(this.devRTT, item);
                    timeout = Monitor.getSTATISTIC(Thread.currentThread()).getERTT() + (4 * this.devRTT);
                    iterator.remove();
                }
            }

            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            
            if (congestionWindow.isEmpty()) {
                System.out.println("(" + Monitor.getFEL(Thread.currentThread()).getSimTime() + ")" + (char) 27 + "[31m" + user.getID() + " ends transmission" + (char) 27 + "[0m");
                restart();
            }
            return true;
        }
        System.out.println("(" + Monitor.getFEL(Thread.currentThread()).getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + (char) 27 + "[31m" + " - DUPLICATE" + (char) 27 + "[0m");
        return false;
    }

    @Override
    public void increaseCongestionWindow() {
        size = (size < ssthresh) ? size * 2 : size + 1;
        ssthresh = (size > ssthresh) ? size : ssthresh;
        //System.out.println("" + size + " " + ssthresh);
        /*System.out.println("------------------------------------------------"
                + "INCREASED CONGESTION WINDOW SIZE: " + size + "; SSTHRESH: " + ssthresh);*/
    }

    @Override
    public void decreaseCongestionWindow() {
        ssthresh = size / 2;
        if (ssthresh == 0)
            ssthresh++;
        size = MyConstants.MSS;
        //System.out.println("" + size + " " + ssthresh);
        /*System.out.println("------------------------------------------------"
                + "DECREASED CONGESTION WINDOW SIZE: " + size + "; SSTHRESH: " + ssthresh);*/
    }

    @Override
    public void restart() {
        //System.out.println("restart");
        super.restart();
        ssthresh = MyConstants.SSTHRESH;
        lastAck = -1;
        repeatedAck = 0;
    }
}
