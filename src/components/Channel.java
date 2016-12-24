package components;

import java.util.LinkedList;
import mainPackage.EventType;
import static mainPackage.MyConstants.*;


public class Channel extends LinkedList<MySegment> {
    private static final int MAX_LENGTH = T;
    private static final Channel instance = new Channel();

    
    private Channel() {}
    
    public static Channel getInstance() { return instance; }

    public void enqueueSegment(MySegment segm) {
        if (size() < MAX_LENGTH) {
            addLast(segm);
        }
    }

    public void dequeueSegment() {
        if (!isEmpty()) {
            removeFirst().solveSegment();
        }
        FEL.getInstance().scheduleNextEvent(new Event(FEL.getInstance().getSimTime() + 0.01, EventType.CH_SOLVING));
    }
}
