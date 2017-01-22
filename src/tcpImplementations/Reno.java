package tcpImplementations;

import components.DataSegment;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.Iterator;


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
            //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            increaseCongestionWindow();
            
            Iterator<DataSegment> iterator = congestionWindow.iterator();
            while (iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    Monitor.getInstance().getFEL(Thread.currentThread()).removeTimeoutEvent(item.getSeq(), item.getUser().getID());
                    item.setReceivedTimestamp(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime());
                    Monitor.getInstance().getStatistic(Thread.currentThread()).refreshResponseTimeStatistics(item);      
                    
                    this.devRTT = Monitor.getInstance().getStatistic(Thread.currentThread()).devRTT(this.devRTT, item);
                    timeout = Monitor.getInstance().getStatistic(Thread.currentThread()).ERTT() + (4 * this.devRTT);
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
    
    private void fastRecovery() {
        ssthresh = size / 2;
        if (ssthresh == 0)
            ssthresh++;
        size = ssthresh;
        //System.out.println("------------------------------------------------"
                //+ "FASTRECO CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);     
    }
}
