package components;

import java.util.LinkedList;
import static mainPackage.MyConstants.*;

public class Channel extends LinkedList<MySegment>{
    private static final int MAX_LENGTH = T;    
    private static Channel myChannel;
    
    public static Channel getInstance(){
        if (myChannel==null)
            myChannel = new Channel();
        return myChannel;
    }

    public synchronized boolean enqueueSegment(MySegment segm){
        if(segm.getSegmentType() == SegmentType.DATA)
                System.out.println(segm.getUser().getID() + " say: Sent data n° " + segm.getSeq());
        if(size()<MAX_LENGTH){
            addLast(segm);
            return true;
        }
        return false;
    }
    
    public void dequeueSegment(){
        if(!isEmpty()){
            removeFirst().solveSegment();
        }
    }
}
