package components;


public class AckSegment implements MySegment {
    private final User user;
    private final int seq;
    private final DataSegment reference; // TODO: perché non lo usiamo?


    public AckSegment(User user, int seq, DataSegment reference) {
        this.user = user;
        this.seq = seq;
        this.reference = reference;
    }
    
    /* GETTER E SETTER */
    @Override
    public User getUser() { return this.user; }
    
    @Override
    public int getSeq() { return this.seq; }
}
