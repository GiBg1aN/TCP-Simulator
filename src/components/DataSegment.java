package components;


public class DataSegment implements MySegment {
    private final User user;
    private final int seq;
    private double sentTimestamp;
    private double receivedTimestamp;


    public DataSegment(User user, int seq, double sentTimestamp) {
        this.user = user;
        this.seq = seq;
        this.sentTimestamp = sentTimestamp;
        this.receivedTimestamp = -1;
    }
    
    
    /* GETTER E SETTER */
    @Override
    public User getUser() { return user; }
    
    @Override
    public int getSeq() { return seq; }

    public double getSentTimestamp() { return sentTimestamp; }
    
    public void setSentTimeStamp(double sentTimestamp) { this.sentTimestamp = sentTimestamp; }

    public double getReceivedTimestamp() { return receivedTimestamp; }
    
    public void setReceivedTimestamp(double timestamp) { this.receivedTimestamp = timestamp; }
}
