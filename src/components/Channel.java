package components;

import java.util.LinkedList;
import static mainPackage.MyConstants.*;


public class Channel extends LinkedList<MySegment> {
    private static final int MAX_LENGTH = T;
    private static final Channel instance = new Channel();
<<<<<<< HEAD
    
    
    private Channel() {}
    
    public static Channel getInstance() { return instance; }
=======


    public Channel() {}

    public static Channel getInstance() { return instance; } // Singleton
>>>>>>> origin/master

    public void enqueueSegment(MySegment segm) {
        if (size() < MAX_LENGTH) {
            addLast(segm);
        }
    }

    public void dequeueSegment(double solveTimestamp) {
        if (!isEmpty()) {
            removeFirst().solveSegment(solveTimestamp);
        }
    }
}
