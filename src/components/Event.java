package components;

import mainPackage.EventType;

/**
 * Rappresenta un evento generico che viene elaborato dalla Future Event List.
 */
public class Event {
    private double timestamp;
    private EventType eventType;
    private User user;
    private MySegment segment;
    
    /* Evento generico */
    public Event(double timestamp, EventType eventType) {
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
    
    /* Evento di tipo TRANSMIT */
    public Event(double timestamp, User user) {
        this.timestamp = timestamp;
        this.user = user;
        this.eventType = EventType.TRANSMIT;
    }
    
    /* Evento di tipo TIMEOUT */
    public Event(double timestamp, MySegment segment) {
        this.timestamp = timestamp;
        this.segment = segment;
        this.eventType = EventType.TIMEOUT;
    }
      
    public void solveEvent() {
        if (eventType == EventType.CH_SOLVING) {
            Monitor.getCHANNEL(Thread.currentThread()).dequeueSegment();
        }
        if (eventType == EventType.TIMEOUT) {
            segment.getUser().timeout(segment);
        }
        if (eventType == EventType.TRANSMIT) {
            user.transmit();
        }
        if (eventType == EventType.TRAVEL) {
            Monitor.getCHANNEL(Thread.currentThread()).enqueueSegment();
        }
    }

    
    /* GETTER E SETTER */
    public double getTimestamp() { return timestamp; }

    public EventType getEventType() { return eventType; }

    public MySegment getSegment() { return segment; }
}
