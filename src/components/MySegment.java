package components;

import static mainPackage.MyConstants.*;
import static mainPackage.MyConstants.SegmentType.DATA;

public class MySegment {
    private SegmentType segmentType;
    private User user;
    private int seq;
    

    public MySegment(SegmentType segmentType, User user, int seq) {
        this.segmentType = segmentType;
        this.user = user;
        this.seq = seq;
    }
    
    public void solveSegment() {
        if(segmentType == DATA) {
            System.out.println("--- Received data n° " + seq + " from user " + user.getID());
            
            while(!sendAcknowledgement(this)) {
                System.out.println("TANTE BANANE"); // DEBUG
            }
            
            System.out.println("--- Sent ack n° " + seq + " for user " + user.getID());
        } else {
            System.out.println("User " + user.getID() + " say: Received ack n° " + seq);
            user.receiveAck(seq);
        }
    }
    
    private boolean sendAcknowledgement(MySegment segm) {
        MySegment ack = new MySegment(SegmentType.ACK, segm.getUser(), segm.getSeq());
        return Channel.getInstance().enqueueSegment(ack);
    }

    public int getSeq() {
        return seq;
    }

    public User getUser() {
        return user;
    }
    
    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }
    
    /*
    
    if(segm.getSegmentType()==SegmentType.DATA){
                System.out.println(segm.getUser() + " say: Received data n° " + segm.getSeq());
                
            }
            else{
                System.out.println(segm.getUser() + " say: Received ack n° " + segm.getSeq());
                //segm.getUser().receiveAck(segm.getSeq());
            }*/
}
