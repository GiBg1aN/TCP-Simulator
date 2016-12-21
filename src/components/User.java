package components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public class User {
    private int ID;
    private int congestionWindowSize;
    private int segmentsToSend;
    private int segmentsNotConfirmed;
    private int segmentConfirmed;
    private int cumulativeAck;
    private int seqNumber;
    private List<DataSegment> congestionWindow;
    private LinkedList<Integer> retransmit = new LinkedList<>();
    
    public User(int ID) {
        this.ID = ID;
        congestionWindowSize = mainPackage.MyConstants.MSS;
        segmentsToSend = mainPackage.MyConstants.N;
        congestionWindow = new ArrayList<>();
    }

    public void transmit(double sendingTimestamp) {
        if(segmentConfirmed == segmentsToSend){
            System.out.println( "(" + sendingTimestamp + ") USER " + ID + " END TRASMISSION");
            segmentConfirmed++;
        }        
        for(DataSegment segm : congestionWindow){
            if(segm.timeout()){
                System.out.println((char)27 + "[33m(" + sendingTimestamp + ") - USER: " + ID + " - Segm n° " + segm.getSeq() + " timeout" + (char)27 + "[0m");
                retransmit.add(segm.getSeq());
                congestionWindowSize = (congestionWindowSize/2 > 0 ) ? congestionWindowSize/2 : MyConstants.MSS;
            }
            else{
                //System.out.println("Segment " + segm.getSeq() + " correctly sent.");
                congestionWindowSize += 1;
                segmentConfirmed += 1;
            }
        }        
        congestionWindow.clear();
        segmentsNotConfirmed = 0;
        
        while(segmentConfirmed < segmentsToSend && segmentsNotConfirmed < congestionWindowSize){
            DataSegment segm;
            if(retransmit.isEmpty() && seqNumber < segmentsToSend){
                segm = new DataSegment(this, seqNumber, sendingTimestamp); // Crea un segmento
                seqNumber++;
            } else{
                segm = new DataSegment(this, retransmit.removeFirst(), sendingTimestamp); // Crea un segmento 
            }
            sendSegment(segm);
            segmentsNotConfirmed++;
        }
    }

    private void sendSegment(DataSegment segm) {
        Channel.getInstance().enqueueSegment(segm);
        System.out.println((char)27 + "[31m(" + segm.getSentTimestamp() + ") - USER: " + ID + " - Sent data n° " + segm.getSeq() + (char)27 + "[0m");
        congestionWindow.add(segm);
    }

    public void restart() {
        segmentsNotConfirmed = 0;
        seqNumber = 0;
        //active = false;
        System.out.println("restart");
    }

    public int getID() {
        return ID;
    }
}
