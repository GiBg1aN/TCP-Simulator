package components;

import mainPackage.MyConstants;


public class AckSegment implements MySegment {
    private User user;
    private int seq;
    private DataSegment reference;
    
    public AckSegment(User user, int seq, DataSegment reference) {
        this.user = user;
        this.seq = seq;
        this.reference = reference;
    }

    @Override
    public void solveSegment(double timestamp) {
        if(reference.getSentTimestamp()<timestamp){
            reference.setReceivedTime(timestamp);
            System.out.println("(" + timestamp + ") - USER: " + user.getID() + " - Received ack n° " + seq);
        }
    }

    @Override
    public int getSeq() {
        return this.seq;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public MyConstants.SegmentType getSegmentType() {
        return MyConstants.SegmentType.ACK;
    }
}
