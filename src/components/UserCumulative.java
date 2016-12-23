package components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import mainPackage.MyConstants;
import tcpImplementations.AIMD;
import tcpImplementations.Reno;
import tcpImplementations.TCP;
import tcpImplementations.Tahoe;


public class UserCumulative {
    private int ID;
    private TCP tcpProtocol;
    private int segmentsToSend;
    private int segmentsNotConfirmed;
    private int segmentConfirmed;
    private int cumulativeAck;
    private int lastAckReceived;
    private int seqNumber;
    private Map<Integer, DataSegment> congestionWindow;
    private LinkedList<Integer> retransmit = new LinkedList<>();
    
    public UserCumulative(int ID, MyConstants.TCPProtocolType tcpProtocol) {
        this.ID = ID;
        segmentsToSend = mainPackage.MyConstants.N;
        congestionWindow = new HashMap<>();
        lastAckReceived = -1;
        cumulativeAck = 0;
        if (tcpProtocol == MyConstants.TCPProtocolType.AIMD) {
            this.tcpProtocol = new AIMD();
        }
        if (tcpProtocol == MyConstants.TCPProtocolType.RENO) {
            this.tcpProtocol = new Reno();
        }
        if (tcpProtocol == MyConstants.TCPProtocolType.TAHOE) {
            this.tcpProtocol = new Tahoe();
        }
    }

    public void transmit(double sendingTimestamp) {
        if(segmentConfirmed == segmentsToSend){
            System.out.println( "(" + sendingTimestamp + ") USER " + ID + " END TRASMISSION");
            segmentConfirmed++;
        }        
        for(Map.Entry<Integer, DataSegment> segm : congestionWindow.entrySet()){
            if(segm.getValue().timeout()){
                System.out.println((char)27 + "[33m(" + sendingTimestamp + ") - USER: " + ID + " - Segm n° " + segm.getSeq() + " timeout" + (char)27 + "[0m");
                retransmit.add(segm.getKey());
                tcpProtocol.decreaseCongestionWindow();
            }
            else{
                //System.out.println("Segment " + segm.getSeq() + " correctly sent.");
                tcpProtocol.increaseCongestionWindow();
                segmentConfirmed += 1;
            }
        }        
        congestionWindow.clear();
        segmentsNotConfirmed = 0;
        
        while(segmentConfirmed < segmentsToSend && segmentsNotConfirmed < tcpProtocol.size()){
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
        congestionWindow.put(segm.getSeq(), segm);
    }

    public int getID() {
        return ID;
    }
    
    public void receiveAck(int ackSequence){
        congestionWindow.remove(ackSequence);
        if(ackSequence != lastAckReceived ){
            cumulativeAck = 1;
            lastAckReceived = ackSequence;
        } else {
            cumulativeAck += 1;
            if (cumulativeAck == 4)
                if(tcpProtocol instanceof Reno )
                    ((Reno)tcpProtocol).fastRecovery();
        }
    }
}
