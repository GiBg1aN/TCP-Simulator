package components;

import mainPackage.EventType;


public class Event {
    private double timestamp;
    private EventType eventType;
    private User user;
    private MySegment segment;

    public Event(double timestamp, EventType eventType) {
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
    
    public Event(double timestamp, User user) {
        this.timestamp = timestamp;
        this.user = user;
        this.eventType = EventType.TRANSMIT;
    }
    
    public Event(double timestamp, MySegment segment) {
        this.timestamp = timestamp;
        this.segment = segment;
        this.eventType = EventType.TIMEOUT;
    }
      
    public void solveEvent() {
        if (eventType == EventType.CH_SOLVING) {
            Channel.getInstance().dequeueSegment();
        }
        if (eventType == EventType.TIMEOUT) {
            segment.getUser().timeout(segment);
        }
        if (eventType == EventType.TRANSMIT) {
            user.transmit(timestamp);
        }
        if (eventType == EventType.TRAVEL) {
            Channel.getInstance().enqueueSegment();
        }
    }

    /* GETTER E SETTER */
    public double getTimestamp() { return timestamp; }

    public EventType getEventType() { return eventType; }

    public MySegment getSegment() { return segment; }
}
