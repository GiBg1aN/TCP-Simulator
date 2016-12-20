package monitorUser;

import components.Channel;

public class QueueHandler extends Thread{
    @Override
    public void run() {
        while(true){
            Channel.getInstance().dequeueSegment();
        }
      }
}
