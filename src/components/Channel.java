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

    public synchronized boolean enqueueSegment(MySegment m){
        if(size()<MAX_LENGTH){
            addLast(m);
            return true;
        }
        return false;
    }
    
    public synchronized int dequeueSegment(){
        if(!isEmpty()){
            MySegment segm = removeFirst();
            if(segm.getSegmentType()==SegmentType.DATA){
                while(!sendAcknowledgement(segm.getUser())){System.out.println("TANTE BANANE");}
                System.out.println("(SENT ack)" + segm.getUser().getID());
            }
            else
                segm.getUser().receiveAck();
            return 1;
        }
        return 0;
    }
    
    private boolean sendAcknowledgement(User user){
        MySegment ack = new MySegment(SegmentType.ACK, user);
        return Channel.getInstance().enqueueSegment(ack);
    } 

}
