package components;

import mainPackage.SegmentType;


public class DataSegment implements MySegment {
    private final User user;
    private final int seq;
    private final double sentTimestamp;
    private double receivedTimestamp; //TODO: utilizzarla per le statistiche


    public DataSegment(User user, int seq, double sentTimestamp) {
        this.user = user;
        this.seq = seq;
        this.sentTimestamp = sentTimestamp;
        this.receivedTimestamp = -1;
    }
    
    /* GETTER E SETTER */
    @Override
    public SegmentType getSegmentType() { return SegmentType.DATA; }

    @Override
    public User getUser() { return user; }
    
    @Override
    public int getSeq() { return seq; }

    public double getSentTimestamp() { return sentTimestamp; }
    
    void setReceivedTime(double timestamp) { this.receivedTimestamp = timestamp; }
}
