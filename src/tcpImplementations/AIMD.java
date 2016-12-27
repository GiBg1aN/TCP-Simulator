package tcpImplementations;

import components.Channel;
import components.DataSegment;
import components.Event;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public class AIMD extends TCPCommonLayer implements TCP {
    public AIMD(User user) {
        super(user);
    }    
    
    @Override
    public boolean receiveSegment(MySegment ack) {
        if (congestionWindow.contains(ack.getSeq())) {
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
        size++; 
        System.out.println("------------------------------------------------"
                + "INCREASED CONGESTION WINDOW SIZE: "+ size);
    }

    @Override
    public void decreaseCongestionWindow() { 
        size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; 
        System.out.println("------------------------------------------------"
                + "DECREASED CONGESTION WINDOW SIZE: "+ size);
    }

    @Override
    public void restart() {
        seqNumber = 0;
        size = MyConstants.MSS;
        double timestamp = FEL.getInstance().getSimTime() + 0.3; // TODO
        FEL.getInstance().scheduleNextEvent(new Event(timestamp, user));
    }
}
