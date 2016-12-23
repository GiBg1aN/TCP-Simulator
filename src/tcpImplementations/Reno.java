package tcpImplementations;

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
}
