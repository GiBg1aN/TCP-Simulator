package components;

import java.util.LinkedList;
import static mainPackage.MyConstants.*;

public class Channel extends LinkedList<MySegment>{
    private static final int MAX_LENGTH = T;    
    private static final Channel instance = new Channel();
    
    
    public Channel() {}
    
    public static Channel getInstance() { return instance; }

    public boolean enqueueSegment(MySegment segm) {
        if(segm.getSegmentType() == SegmentType.DATA) {
                System.out.println(segm.getUser().getID() + " say: Sent data n° " + segm.getSeq());
        } if(size() < MAX_LENGTH){
            addLast(segm);
            return true;
        }
        return false;
    }
    
    public void dequeueSegment() {
        if(!isEmpty()) {
            removeFirst().solveSegment();
        }
    }
}
