package mainPackage;

import components.Event;
import components.FEL;
import components.Monitor;
import components.User;

public class RunPilota implements Runnable {
    Thread t = new Thread(this);

    public void start() { t.start(); }
    
    public void stop() { t.interrupt(); }

    @Override
    public void run() {
        final int N_USERS = MyConstants.K;
        Event nextEvent;
        Monitor.addFEL(Thread.currentThread());
        FEL fel = Monitor.getFEL(Thread.currentThread());
        Monitor.addChannel(Thread.currentThread());
        Monitor.addStatistic(Thread.currentThread());
        Monitor.addRandomStream(Thread.currentThread());

        /* Creazione utenti */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, MyConstants.protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));

        /* Avvio simulazione */
        //while (Monitor.getFEL(Thread.currentThread()).getSimTime() < MyConstants.simulationTime) {
        while (true) {
            nextEvent = fel.getNextEvent();
            nextEvent.solveEvent();
        }

        /*System.out.println("Fine simulazione " + Monitor.getFEL(Thread.currentThread()).getSimTime());

        Monitor.getSTATISTIC(Thread.currentThread()).printStatistics();
        Monitor.getSTATISTIC(Thread.currentThread()).closeStream();*/
    }

}
