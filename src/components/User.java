package components;

import static mainPackage.MyConstants.*;
import monitorUser.MyMonitor;

public class User extends Thread{
    private int ID = 0;
    private static final int nSegment = N;
    private int nReceived = 0;

    @Override
    public void run() {
        try{
            MyMonitor.getInstance().askConnection();
            startConnection();
            MyMonitor.getInstance().releaseConnection(ID);
        } catch (InterruptedException e){
            System.out.println("Connection issue");
        }
    }
    
    public User(int ID){
        this.ID = ID;
    }
    
    public void startConnection(){
        int sent = 0;

        while(sent<nSegment){
            System.out.println("BANANA_1 " + ID );
            if(sendSegment()){
                System.out.println("(SENT data) " + ID);
                MyMonitor.getInstance().getSegments()[ID]++;
                sent++;       
            }
        }
        
    }
    
    private boolean sendSegment(){
        MySegment segm = new MySegment(SegmentType.DATA, this);
        return Channel.getInstance().enqueueSegment(segm);
    }
    
    public synchronized void receiveAck(){
        System.out.println("(RECEIVED ack) " + ID);
        MyMonitor.getInstance().ack(ID);
    }   
}
