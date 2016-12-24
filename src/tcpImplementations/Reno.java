package tcpImplementations;

import components.MySegment;
import mainPackage.MyConstants;


public class Reno implements TCP {
    private int size;
    private int ssthresh;
    
    @Override
    public void increaseCongestionWindow() { size = (size < ssthresh) ? size * 2 : size + 1; }

    @Override
    public void decreaseCongestionWindow() {
        ssthresh /= 2;
        size = MyConstants.MSS;
    }

    @Override
    public int size() { return this.size; }
    
    public void fastRecovery(){
        
    }

    @Override
    public void startTransmission(int segmentsToSend) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean receiveSegment(MySegment s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void restart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void timeout(int seqNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
