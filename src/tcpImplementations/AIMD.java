package tcpImplementations;

import mainPackage.MyConstants;


public class AIMD implements TCP {
    private int size;
    
    
    public AIMD() {
        this.size = MyConstants.MSS;
    }

    @Override
    public void increaseCongestionWindow() { this.size++; }

    @Override
    public void decreaseCongestionWindow() { size = (size / 2 > 0) ? size / 2 : MyConstants.MSS; }

    @Override
    public int size() {
        return size;
    }
}
