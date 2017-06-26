package components;

/**
 * This class represents the ack segment, used to confirm the successful
 * reception of a data segment.
 */
public class AckSegment implements MySegment {
    private final User user;
    private final int seq;


    public AckSegment(User user, int seq, DataSegment reference) {
        this.user = user;
        this.seq = seq;
    }
    
    
    /* GETTER / SETTER */
    @Override
    public User getUser() { return this.user; }
    
    @Override
    public int getSeq() { return this.seq; }
}
