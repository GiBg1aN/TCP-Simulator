package tcpImplementations;

import GUI.Chart;
import components.DataSegment;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import mainPackage.MyConstants;
import statistics.Statistics;


public class AIMD extends TCPCommonLayer implements TCP {    
    public AIMD(User user) {
        super(user);
    }    
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (congestionWindow.stream().map(x -> x.getSeq()).anyMatch(x -> x == ack.getSeq())) {
            //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[32m" + user.getID() + " receives ack, number: " + ack.getSeq() + " - ACCEPTED" + (char) 27 + "[0m");
            // TODO: timestamp ricezione ack
            increaseCongestionWindow();
            
            Iterator<DataSegment> iterator = congestionWindow.iterator();
            while(iterator.hasNext()) {
                DataSegment item = iterator.next();
                if (item.getSeq() <= ack.getSeq()) {
                    FEL.getInstance().removeTimeoutEvent(item.getSeq(), item.getUser().getID());
                    item.setReceivedTimestamp(FEL.getInstance().getSimTime());
                    Statistics.refreshResponseTimeStatistics(item);
                    
                    this.devRTT = Statistics.getDevRTT(this.devRTT, item);
                    timeout = Statistics.getERTT() + (4 * this.devRTT);
                    //System.out.println(String.valueOf(timeout));
                    iterator.remove();
                }
            }
            
            while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
                sendSegment();
            }
            
            if (congestionWindow.isEmpty()) {
                //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + user.getID() + " ends transmission" + (char) 27 + "[0m");
                Chart.getInstance().reset(user.getID());
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
        Chart.getInstance().addValue(size, user.getID(), -1);
        //System.out.println("------------------------------------------------"
          //      + "INCREASED CONGESTION WINDOW SIZE: "+ size);
    }

    @Override
    public void decreaseCongestionWindow() { 
        size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; 
        Chart.getInstance().addValue(size, user.getID(), -1);
        //System.out.println("------------------------------------------------"
             //   + "DECREASED CONGESTION WINDOW SIZE: "+ size);
    }
}
