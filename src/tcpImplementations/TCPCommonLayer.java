package tcpImplementations;

import components.Channel;
import components.DataSegment;
import components.Event;
import components.FEL;
import components.MySegment;
import components.User;
import java.util.LinkedList;
import java.util.List;
import mainPackage.MyConstants;


public abstract class TCPCommonLayer implements TCP {
    protected int size;
    protected int segmentsToSend;
    protected int seqNumber;
    protected List<Integer> congestionWindow;
    protected User user;
    
    public TCPCommonLayer(User user) {
        this.size = MyConstants.MSS;
        this.congestionWindow = new LinkedList<>();
        this.user = user;
    }
    
    protected void sendSegment() {
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " sends segment number: " + seqNumber + (char) 27 + "[0m");
        MySegment segment = new DataSegment(this.user, this.seqNumber, FEL.getInstance().getSimTime());
        Channel.getInstance().startTravel(segment);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MyConstants.TIMEOUT, segment));
        congestionWindow.add(seqNumber);
        seqNumber++;
    }
    
    protected void sendSegment(int seqNumber) {
        System.out.println("(" + FEL.getInstance().getSimTime() + ")" + (char) 27 + "[35m" + user.getID() + " REsends segment number: " + seqNumber + (char) 27 + "[0m");        
        MySegment segment = new DataSegment(this.user, seqNumber, FEL.getInstance().getSimTime());
        Channel.getInstance().startTravel(segment);
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + MyConstants.TIMEOUT, segment));
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
        double timestamp = FEL.getInstance().getSimTime() + 0.3; // TODO
        FEL.getInstance().scheduleNextEvent(new Event(timestamp, user));
        Channel.getInstance().resetChannelForUser(this.user.getID());
    }
    
    @Override
    public void timeout(int seqNumber) {
        decreaseCongestionWindow();
        sendSegment(seqNumber);
    }

    @Override
    public int size() { return this.size; }
}
