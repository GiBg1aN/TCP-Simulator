package components;

import java.util.ArrayList;
import java.util.List;


public class User {
    private int ID;
    private int congestionWindowSize;
    private int segmentsToSend;
    private int segmentsNotConfirmed;
    private int cumulativeAck;
    private int seqNumber;
    private List<MySegment> congestionWindow;

    public User(int ID) {
        this.ID = ID;
        congestionWindowSize = mainPackage.MyConstants.MSS;
        segmentsToSend = mainPackage.MyConstants.N;
        congestionWindow = new ArrayList<>();
    }

    public void transmit(double sendingTimestamp) {
        if (seqNumber < segmentsToSend) {
            while (segmentsNotConfirmed < congestionWindowSize) {
                sendSegment(seqNumber, sendingTimestamp);
                segmentsNotConfirmed++;
                seqNumber++;
            }
        }
    }

    private void sendSegment(int seq, double sentTimestamp) {
        MySegment segm = new DataSegment(this, seq, sentTimestamp); // Crea un segmento
        Channel.getInstance().enqueueSegment(segm);
        congestionWindow.add(segm);
    }

    public void receiveAck(int seq, double ackTimestamp) {
    
    }

    public void restart() {
        segmentsNotConfirmed = 0;
        seqNumber = 0;
        //active = false;
        System.out.println("restart");
    }

    //public boolean active(){
    //    return active;
    //}
    private void timeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getID() {
        return ID;
    }
}
