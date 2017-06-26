package tcpImplementations;

import components.MySegment;

/**
 * A common interface for TCP protocols.
 */
public interface TCP {
    boolean receiveSegment(MySegment s);
    
    void startTransmission(int segmentsToSend);
    
    void increaseCongestionWindow();

    void decreaseCongestionWindow();
    
    void restart();
    
    void timeout(MySegment segment);

    int size();
}
