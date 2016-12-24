package tcpImplementations;

import components.MySegment;


public interface TCP {
    void startTransmission(int segmentsToSend);
    
    boolean receiveSegment(MySegment s);
    
    void increaseCongestionWindow();

    void decreaseCongestionWindow();
    
    void restart();
    
    void timeout(int seqNumber);

    int size();
}
