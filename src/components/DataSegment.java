package components;

import static mainPackage.MyConstants.*;
import static mainPackage.MyConstants.SegmentType.DATA;

public class DataSegment implements MySegment {
    private User user;
    private int seq;
    private double sentTimestamp;
    private double receivedTimestamp;
    

    public DataSegment(User user, int seq, double sentTimestamp) {
        this.user = user;
        this.seq = seq;
        this.sentTimestamp = sentTimestamp;
        this.receivedTimestamp = -1;
    }
    
    @Override
    public void solveSegment(double timestamp) {
        System.out.println("(" + receivedTimestamp + ") - Received data n° " + seq + " from user " + user.getID());
        sendAcknowledgement(this);
        System.out.println("(" + timestamp + ") - Sent ack n° " + seq + " for user " + user.getID());        
    }
    
    private void sendAcknowledgement(DataSegment segm) {
        MySegment ack = new AckSegment(segm.getUser(), segm.getSeq(), this);
        Channel.getInstance().enqueueSegment(ack);
    }
    
    void setReceivedTime(double timestamp) {
        this.receivedTimestamp = timestamp;
    }

    @Override
    public int getSeq() {
        return seq;
    }

    @Override
    public User getUser() {
        return user;
    }
    
    @Override
    public SegmentType getSegmentType() {
        return SegmentType.DATA;
    }
}
