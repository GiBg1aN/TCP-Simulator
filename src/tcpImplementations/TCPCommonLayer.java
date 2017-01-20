package tcpImplementations;

import components.DataSegment;
import components.Event;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public abstract class TCPCommonLayer implements TCP {
    protected int size;
    protected int segmentsToSend;
    protected int seqNumber;
    protected double timeout;
    protected double devRTT;
    protected List<DataSegment> congestionWindow;
    protected User user;
    
    public TCPCommonLayer(User user) {
        this.size = MyConstants.MSS;
        this.timeout = MyConstants.TIMEOUT;
        this.congestionWindow = new LinkedList<>();
        this.user = user;
    }
    
    protected void sendSegment() {
        //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " sends segment number: " + seqNumber + (char) 27 + "[0m");
        DataSegment segment = new DataSegment(this.user, this.seqNumber, Monitor.getFEL(Thread.currentThread()).getSimTime());
        Monitor.getCHANNEL(Thread.currentThread()).startTravel(segment);
        Monitor.getFEL(Thread.currentThread()).scheduleNextEvent(new Event(Monitor.getFEL(Thread.currentThread()).getSimTime() + timeout, segment));
        congestionWindow.add(segment);
        seqNumber++;
    }
    
    protected void sendSegment(int seqNumber) {
        //System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " REsends segment number: " + seqNumber + (char) 27 + "[0m");        
        MySegment segment = new DataSegment(this.user, seqNumber, Monitor.getFEL(Thread.currentThread()).getSimTime());
        Monitor.getCHANNEL(Thread.currentThread()).startTravel(segment);
        Monitor.getFEL(Thread.currentThread()).scheduleNextEvent(new Event(Monitor.getFEL(Thread.currentThread()).getSimTime() + timeout, segment));
    }
    
    @Override
    public abstract boolean receiveSegment(MySegment s);

    @Override
    public void startTransmission(int segmentsToSend) {
        this.segmentsToSend = segmentsToSend;
        while (seqNumber < segmentsToSend && congestionWindow.size() < size) {
            sendSegment();
        }
    }

    @Override
    public abstract void increaseCongestionWindow();

    @Override
    public abstract void decreaseCongestionWindow();

    @Override
    public void restart() {
        seqNumber = 0;
        size = MyConstants.MSS;
        double timestamp = Monitor.getFEL(Thread.currentThread()).getSimTime();
        Monitor.getFEL(Thread.currentThread()).scheduleNextEvent(new Event(timestamp, user));
        Monitor.getCHANNEL(Thread.currentThread()).resetChannelForUser(this.user.getID());
    }
    
    @Override
    public void timeout(MySegment segment) {
        decreaseCongestionWindow();
        ((DataSegment) segment).setReceivedTimestamp(Monitor.getFEL(Thread.currentThread()).getSimTime());
        Monitor.getSTATISTIC(Thread.currentThread()).refreshResponseTimeStatistics((DataSegment)segment);
        this.devRTT = Monitor.getSTATISTIC(Thread.currentThread()).getDevRTT(this.devRTT, (DataSegment) segment);
        timeout = Monitor.getSTATISTIC(Thread.currentThread()).getERTT() + (4 * this.devRTT);
        sendSegment(segment.getSeq());
        //System.out.printl1n(timeout);
    }

    @Override
    public int size() { return this.size; }
}
