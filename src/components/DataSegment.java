package components;

import mainPackage.MyConstants;
import mainPackage.SegmentType;


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
        System.out.println((char) 27 + "[36m(" + timestamp + ") - USER: " + user.getID() + " - Received data n° " + seq + (char) 27 + "[0m");
        sendAcknowledgement(this);
        System.out.println((char) 27 + "[32m(" + timestamp + ") - USER: " + user.getID() + " - Sent ack n° " + seq + (char) 27 + "[0m");
    }

    private void sendAcknowledgement(DataSegment segm) {
        MySegment ack = new AckSegment(segm.getUser(), segm.getSeq(), this);
        Channel.getInstance().enqueueSegment(ack);
    }

    public boolean timeout() {
        /*System.out.println(sentTimestamp);
        System.out.println(receivedTimestamp);*/
        return receivedTimestamp == -1 || (receivedTimestamp - sentTimestamp) > MyConstants.TIMEOUT;
    }


    /* GETTER E SETTER */
    @Override
    public SegmentType getSegmentType() {
        return SegmentType.DATA;
    }

    @Override
    public User getUser() { return user; }
    
    @Override
    public int getSeq() { return seq; }

    public double getSentTimestamp() { return sentTimestamp; }
    
    void setReceivedTime(double timestamp) { this.receivedTimestamp = timestamp; }
}
