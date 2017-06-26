package components;

import mainPackage.EventType;

/**
 * Represents a generic event that gets elaborated from Future Event List.
 */
public class Event {
    private final double timestamp;
    private final EventType eventType;
    private User user;
    private MySegment segment;
    
    /* Generic event type */
    public Event(double timestamp, EventType eventType) {
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
    
    /* TRANSMIT event type */
    public Event(double timestamp, User user) {
        this.timestamp = timestamp;
        this.user = user;
        this.eventType = EventType.TRANSMIT;
    }
    
    /* TIMEOUT event type */
    public Event(double timestamp, MySegment segment) {
        this.timestamp = timestamp;
        this.segment = segment;
        this.eventType = EventType.TIMEOUT;
    }
      
    public void solveEvent() {
        if (eventType == EventType.CH_SOLVING) {
            Monitor.getInstance().getChannel(Thread.currentThread()).dequeueSegment();
        }
        if (eventType == EventType.TIMEOUT) {
            segment.getUser().timeout(segment);
        }
        if (eventType == EventType.TRANSMIT) {
            user.transmit();
        }
        if (eventType == EventType.TRAVEL_DATA) {
            Monitor.getInstance().getChannel(Thread.currentThread()).enqueueSegment(EventType.TRAVEL_DATA);
        }
        if (eventType == EventType.TRAVEL_ACK) {
            Monitor.getInstance().getChannel(Thread.currentThread()).enqueueSegment(EventType.TRAVEL_ACK);
        }
    }

    
    /* GETTER / SETTER */
    public double getTimestamp() { return timestamp; }

    public EventType getEventType() { return eventType; }

    public MySegment getSegment() { return segment; }
}
