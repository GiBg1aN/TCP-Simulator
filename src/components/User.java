package components;

import mainPackage.TCPProtocolType;
import tcpImplementations.AIMD;
import tcpImplementations.Reno;
import tcpImplementations.TCP;
import tcpImplementations.Tahoe;

/**
 * This class models an user, describing the TCP protocol used and manages the
 * transmission/reception of user's segments.
 */
public class User {
    private final int ID;
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

    public void transmit() {
        int segmentsToSend = Monitor.getInstance().generateSegmentsToSend(Thread.currentThread());
        tcpProtocol.startTransmission(segmentsToSend); 
    }
    
    public void receiveAck(MySegment s) {
        tcpProtocol.receiveSegment(s);
    }
    
    public void timeout(MySegment segment) {
        Monitor.getInstance().getStatistic(Thread.currentThread()).increaseTimeout();
        tcpProtocol.timeout(segment);
    }

    
    /* GETTER / SETTER */
    public int getID() { return ID; }
}

