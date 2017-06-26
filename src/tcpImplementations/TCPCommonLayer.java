package tcpImplementations;

import components.DataSegment;
import components.Event;
import components.Monitor;
import components.MySegment;
import components.User;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;

/**
 * This class models the common behaviour of Tahoe and Reno.
 */
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
        DataSegment segment = new DataSegment(this.user, this.seqNumber, Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime());
        Monitor.getInstance().getChannel(Thread.currentThread()).startTravel(segment);
        Monitor.getInstance().getFEL(Thread.currentThread()).scheduleNextEvent(new Event(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime() + timeout, segment));
        congestionWindow.add(segment);
        seqNumber++;
    }
    
    protected void sendSegment(int seqNumber, double timestamp) {
        MySegment segment = new DataSegment(this.user, seqNumber, timestamp);
        Monitor.getInstance().getChannel(Thread.currentThread()).startTravel(segment);
        Monitor.getInstance().getFEL(Thread.currentThread()).scheduleNextEvent(new Event(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime() + timeout, segment));
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
        double timestamp = Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime();
        Monitor.getInstance().getFEL(Thread.currentThread()).scheduleNextEvent(new Event(timestamp, user));
        Monitor.getInstance().getChannel(Thread.currentThread()).resetChannelForUser(this.user.getID());
    }
    
    @Override
    public void timeout(MySegment segment) {
        decreaseCongestionWindow();
        ((DataSegment) segment).setReceivedTimestamp(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime());
        Monitor.getInstance().getStatistic(Thread.currentThread()).refreshResponseTimeStatistics((DataSegment)segment, false);
        this.devRTT = Monitor.getInstance().getStatistic(Thread.currentThread()).devRTT(this.devRTT, (DataSegment) segment);
        timeout = Monitor.getInstance().getStatistic(Thread.currentThread()).ERTT() + (4 * this.devRTT);
        sendSegment(segment.getSeq(), ((DataSegment)segment).getSentTimestamp());
    }

    @Override
    public int size() { return this.size; }
}
