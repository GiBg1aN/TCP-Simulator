
package monitorUser;

import static mainPackage.MyConstants.*;

public class MyMonitor {
    private static final int MAX_USER = K;
    private int nUsers = 0;
    private static MyMonitor myMonitor;
    private int[] segments = new int[MAX_USER];

    public int[] getSegments() {
        return segments;
    }
    
    
    
    public static MyMonitor getInstance(){
        if (myMonitor==null){
            myMonitor = new MyMonitor();
            for(int i=0; i<MAX_USER;i++)
                myMonitor.getSegments()[i] = 0;
        }
        return myMonitor;
    }
    
    public synchronized void askConnection() throws InterruptedException{
        while(nUsers>MAX_USER)
            wait();
        nUsers++;
    }
    
    public synchronized void releaseConnection(int ID) throws InterruptedException{
        while(segments[ID]!=0){
            System.out.println("aspetto");
            wait();
        }
            
        nUsers--;
        notifyAll();
    }
    
    public synchronized void ack(int ID){
        segments[ID]--;
        notifyAll();
    }
    
}
