package components;

import java.util.LinkedList;
import static mainPackage.MyConstants.*;

public class Channel extends LinkedList<MySegment>{
    private static final int MAX_LENGTH = T;    
    private static Channel myChannel;
    
    public static Channel getInstance(){
        if (myChannel==null)
            myChannel = new Channel();
        return myChannel;
    }

    public boolean enqueueSegment(MySegment m){
        if(size()<MAX_LENGTH){
            addLast(m);
            return true;
        }
        return false;
    }
    
    public void dequeueSegment(){
        if(!isEmpty()){
            MySegment segm = removeFirst();
            if(segm.getSegmentType()==SegmentType.DATA){
                while(!sendAcknowledgement(segm.getUser())){System.out.println("TANTE BANANE");}
                System.out.println("(SENT ack)");
            }
            else
                segm.getUser().receiveAck();
        }
    }
    
    private boolean sendAcknowledgement(User user){
        MySegment ack = new MySegment(SegmentType.ACK, user);
        return Channel.getInstance().enqueueSegment(ack);
    } 
}
