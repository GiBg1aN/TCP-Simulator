package components;

import static mainPackage.MyConstants.*;
import monitorUser.MyMonitor;

public class User extends Thread{
    private int ID = 0;
    private static final int nSegment = N;

    public int getID() {
        return ID;
    }

    
    
    @Override
    public void run() {
        while(true){
            try{
                MyMonitor.getInstance().askConnection(ID);
                startTransmission();
                MyMonitor.getInstance().releaseConnection(ID);            
            } catch (InterruptedException e){
                System.out.println("Connection issue");
            }
        }
    }
    
    public User(int ID){
        this.ID = ID;
    }
    
    public void startTransmission(){
        int sent = 0;

        while(sent<nSegment){
            if(sendSegment()){
                System.out.println("(SENT data) " + ID);
                MyMonitor.getInstance().data(ID);
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
