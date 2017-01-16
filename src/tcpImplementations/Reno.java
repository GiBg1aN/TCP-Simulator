package tcpImplementations;

import components.DataSegment;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import statistics.Statistics;


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
            while(iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    FEL.getInstance().removeTimeoutEvent(item.getSeq());
                    item.setReceivedTimestamp(FEL.getInstance().getSimTime());
                    Statistics.refreshResponseTimeStatistics(item);                    
                    this.devRTT = Statistics.getDevRTT(this.devRTT, item);
                    timeout = Statistics.getERTT() + (4 * this.devRTT);
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
        size = ssthresh;
        //System.out.println("------------------------------------------------"
          //      + "FASTRECO CONGESTION WINDOW SIZE: "+ size + "; SSTHRESH: " + ssthresh);     
    }
}
