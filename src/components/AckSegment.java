package components;

import mainPackage.SegmentType;


public class AckSegment implements MySegment {
    private User user;
    private int seq;
    private DataSegment reference;


    public AckSegment(User user, int seq, DataSegment reference) {
        this.user = user;
        this.seq = seq;
        this.reference = reference;
    }
    
    /* GETTER E SETTER */
    @Override
    public SegmentType getSegmentType() { return SegmentType.ACK; }

    @Override
    public User getUser() { return this.user; }
    
    @Override
    public int getSeq() { return this.seq; }
}
