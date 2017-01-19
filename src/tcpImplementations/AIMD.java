package tcpImplementations;

import components.DataSegment;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;


public class AIMD extends TCPCommonLayer implements TCP {    
    public AIMD(User user) {
        super(user);
    }    
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
            //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
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
                    //System.out.println(String.valueOf(timeout));
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
        size++; 
        //System.out.println("------------------------------------------------"
          //      + "INCREASED CONGESTION WINDOW SIZE: "+ size);
    }

    @Override
    public void decreaseCongestionWindow() { 
        size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; 
        //System.out.println("------------------------------------------------"
             //   + "DECREASED CONGESTION WINDOW SIZE: "+ size);
    }
}
