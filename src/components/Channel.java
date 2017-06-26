package components;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mainPackage.EventType;
import static mainPackage.MyConstants.*;

/**
 * This class models the behaviour of the channel where data segments and ack
 * segments travel.
 */
public class Channel {
    private static final int MAX_LENGTH = T;
    private final LinkedList<MySegment> channel = new LinkedList<>();
    /** Contains information about the segments successfully received during the transmission */
    private final Map<Integer, List<Integer>> cumulativeAcks = new TreeMap<>();
    /** Represents the queue where data segments are waiting to be sent */
    private final LinkedList<DataSegment> travellingData = new LinkedList<>();
    /** Represents the queue where ack segments are waiting to be sent */
    private final LinkedList<AckSegment> travellingAck = new LinkedList<>();
    private final Thread t;

    public Channel(Thread t) {
        this.t = t;
    }

    /**
     * Enqueue a segment ready to be sent.
     *
     * @param segm segment to send.
     */
    public void startTravel(MySegment segm) {
        if (segm instanceof DataSegment) {
            travellingData.addLast((DataSegment) segm);
            Monitor.getInstance().getFEL(t).scheduleNextEvent((new Event(Monitor.getInstance().getFEL(t).getSimTime() + TRAVEL_TIME, EventType.TRAVEL_DATA)));
        } else {
            travellingAck.addLast((AckSegment) segm);
            Monitor.getInstance().getFEL(t).scheduleNextEvent((new Event(Monitor.getInstance().getFEL(t).getSimTime() + TRAVEL_TIME, EventType.TRAVEL_ACK)));
        }
    }

    /**
     * Enqueue a segment waiting to be sent in the channell. If the channel is full the segment will be dropped.
     * 
     * @param event event type.
     */
    public void enqueueSegment(EventType event) {
        MySegment segm = (event == EventType.TRAVEL_DATA) ? travellingData.removeFirst() : travellingAck.removeFirst();
        if (channel.size() < MAX_LENGTH) {
            channel.addLast(segm);
        } else {
            Monitor.getInstance().getStatistic(Thread.currentThread()).increaseDroppedSegmentNumber();
        }
    }

    /**
     * Extracts a segment from the channel, if it arrives correctly: if it's a
     * <code>DataSegment</code> then an ack segment will be inserted in the
     * channel, otherwise if it's an ack segment, this will be send to the
     * corresponding user.
     */
    public void dequeueSegment() {
        if (!channel.isEmpty()) {
            MySegment s = channel.removeFirst();

            if (Monitor.getInstance().isSegmentNotCorrupted(Thread.currentThread())) {
                if (s.getClass() == DataSegment.class) {
                    if (!cumulativeAcks.containsKey(s.getUser().getID())) {
                        cumulativeAcks.put(s.getUser().getID(), new LinkedList<>());
                    }

                    (cumulativeAcks.get(s.getUser().getID())).add(s.getSeq());
                    sendAcknowledgement((DataSegment) s);
                } else {
                    s.getUser().receiveAck(s);
                }
            } else {
                Monitor.getInstance().getStatistic(Thread.currentThread()).increaseCorruptedSegmentsNumber();
            }
        }
        Monitor.getInstance().getFEL(t).scheduleNextEvent(new Event(Monitor.getInstance().getFEL(t).getSimTime() + MU, EventType.CH_SOLVING));
    }

    public void resetChannelForUser(int id) {
        cumulativeAcks.remove(id);
    }

    /**
     * Enqueue an ack segment ready to be sent.
     *
     * @param segm segment to send.
     */
    private void sendAcknowledgement(DataSegment segm) {
        int lastAck = getLastAcknowledgement(segm.getUser().getID());
        MySegment ack = new AckSegment(segm.getUser(), lastAck, segm);
        Monitor.getInstance().getChannel(t).startTravel(ack);
    }

    /**
     * Returns the number of the last correctly received segment.
     *
     * @param id user id
     */
    private int getLastAcknowledgement(int id) {
        List<Integer> tmp = cumulativeAcks.get(id);
        int min = -1;
        for (Integer i : tmp) {
            if (i == min + 1) {
                min++;
            }
        }
        return min;
    }
    
    public int size() { return channel.size(); }
}
