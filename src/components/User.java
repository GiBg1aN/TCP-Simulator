package components;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Timer;
import java.time.Instant;
import java.util.TimerTask;
import static mainPackage.MyConstants.*;
import monitorUser.MyMonitor;

public class User extends Thread{
    private int ID = 0;
    private static final int nSegment = N;
    private Timer[] timeouts = new Timer[nSegment];
    private Instant[] timestamps = new Instant[nSegment];
    private Instant[] timestamps2 = new Instant[nSegment];
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss");
    
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
        //while(true){
            try{
                MyMonitor.getInstance().askConnection(ID);
                startTransmission();
                MyMonitor.getInstance().releaseConnection(ID);
                //for(int i = 0; i < nSegment; i++){
                //    System.out.println(Duration.between(timestamps[i], timestamps2[i]).toNanos());
                //}
            } catch (Exception e){
                System.out.println("Connection issue");
            }
        //}
    }
    
    public User(int ID){
        this.ID = ID;
    }
    
    public void startTransmission(){
        int seq = 0;

        while(seq<nSegment){
            sendSegment(seq);
            //timestamps[seq] = Instant.now();
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
        //timestamps2[seq] = Instant.now();
    }   
}