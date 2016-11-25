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

    public synchronized boolean enqueueSegment(MySegment segm){
        if(segm.getSegmentType() == SegmentType.DATA)
                System.out.println(segm.getUser().getID() + " say: Sent data n° " + segm.getSeq());
        if(size()<MAX_LENGTH){
            addLast(segm);
            return true;
        }
        return false;
    }
    
    public synchronized int dequeueSegment(){
        if(!isEmpty()){
            MySegment segm = removeFirst();            
            if(segm.getSegmentType()==SegmentType.DATA){
                System.out.println(segm.getUser().getID() + " say: Received data n° " + segm.getSeq());
                while(!sendAcknowledgement(segm)){System.out.println("TANTE BANANE");}
                System.out.println(segm.getUser().getID() + " say: Sent ack n° " + segm.getSeq());
            }
            else{
                System.out.println(segm.getUser().getID() + " say: Received ack n° " + segm.getSeq());
                segm.getUser().receiveAck(segm.getSeq());
            }
               
            return 1;
        }
        return 0;
    }
    
    private synchronized boolean sendAcknowledgement(MySegment segm){
        MySegment ack = new MySegment(SegmentType.ACK, segm.getUser(), segm.getSeq() );
        return Channel.getInstance().enqueueSegment(ack);
    } 

}
