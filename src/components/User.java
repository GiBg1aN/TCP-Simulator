package components;

import static mainPackage.MyConstants.*;


public class User {
    private int ID;
    private int congestionWindow;
    private int segmentsToSend;
    private int segmentsNotConfirmed;
    private int cumulativeAck;
    private int seqNumber;

    
    public User(int ID) {
        this.ID = ID;
        congestionWindow = MSS;
        segmentsToSend = N;
    }

    public void transmit() {
        if (segmentsNotConfirmed != 0) {
            timeout();
        }
        if (seqNumber < segmentsToSend) {
            while (segmentsNotConfirmed < congestionWindow) {
                sendSegment(seqNumber);
                segmentsNotConfirmed++;
                seqNumber++;
            }
        }
    }

    private void sendSegment(int seq) {
        MySegment segm = new MySegment(SegmentType.DATA, this, seq); // Crea un segmento
        Channel.getInstance().enqueueSegment(segm);
    }

    public void receiveAck(int seq) {
        cumulativeAck++;
        if (cumulativeAck == segmentsToSend) {
            restart();
        } else {
            //active = false;
            segmentsNotConfirmed = 0;
        }
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
