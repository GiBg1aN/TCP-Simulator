package components;

public class QueueHandler extends Thread{
    @Override
    public void run() {
        while(true){
            Channel.getInstance().dequeueSegment();
        }
      }
}
