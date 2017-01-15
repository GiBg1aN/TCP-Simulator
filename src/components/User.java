package components;

import mainPackage.MyConstants;
import mainPackage.TCPProtocolType;
import tcpImplementations.AIMD;
import tcpImplementations.Reno;
import tcpImplementations.TCP;
import tcpImplementations.Tahoe;


public class User {
    private int ID;
    private TCP tcpProtocol;



    public User(int ID, TCPProtocolType tcpProtocol) {
        this.ID = ID;
        if (tcpProtocol == TCPProtocolType.AIMD) {
            this.tcpProtocol = new AIMD(this);
        }
        if (tcpProtocol == TCPProtocolType.RENO) {
            this.tcpProtocol = new Reno(this);
        }
        if (tcpProtocol == TCPProtocolType.TAHOE) {
            this.tcpProtocol = new Tahoe(this);
        }
    }

    public void transmit(double timestamp) {
        int segmentsToSend = MyConstants.generateSegmentsToSend();
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + ID + " starts transmission...sending " + segmentsToSend + " segments" + (char) 27 + "[0m");
        tcpProtocol.startTransmission(segmentsToSend); 
    }
    
    public void receiveAck(MySegment s) {
        tcpProtocol.receiveSegment(s);
    }
    
    public void timeout(int seqNumber) {
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[31m" + ID + " reachs timeout for segment number: " + seqNumber + (char) 27 + "[0m");
        tcpProtocol.timeout(seqNumber);
    }

    /* GETTER E SETTER */
    public int getID() { return ID; }
}

