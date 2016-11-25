package components;

import java.util.Timer;
import java.util.TimerTask;
import static mainPackage.MyConstants.*;
import monitorUser.MyMonitor;

public class User extends Thread{
    private int ID = 0;
    private static final int nSegment = N;
    private Timer[] timeouts = new Timer[nSegment];
    
    /* ----------------------------------------------------------------------- */
    public class RemindTask extends TimerTask {
        int seq;
        
        public RemindTask(int seq){
            this.seq = seq;
        }
        
        public void run() {
            System.out.format(ID + " say: Time's up for segment "+ seq +"!%n      Resend data n° "+ seq +"%n");
            sendSegment(seq);
        }
    }
    
    /* ----------------------------------------------------------------------- */    
    
    
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
        int seq = 0;

        while(seq<nSegment){
            sendSegment(seq);
            seq++;      
        }
        
    }
    
    private void sendSegment(int seq){
        MySegment segm = new MySegment(SegmentType.DATA, this, seq);
        timeouts[seq] = new Timer();
        timeouts[seq].schedule(new RemindTask(seq), 300);
        if(Channel.getInstance().enqueueSegment(segm)){
                MyMonitor.getInstance().data(ID);    
        }
    }
    
    public synchronized void receiveAck(int seq){
        timeouts[seq].cancel();
        MyMonitor.getInstance().ack(ID);
    }   
}