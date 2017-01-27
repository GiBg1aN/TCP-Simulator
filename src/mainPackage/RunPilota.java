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
        Monitor.getInstance().addFEL(Thread.currentThread());
        FEL fel = Monitor.getInstance().getFEL(Thread.currentThread());
        Monitor.getInstance().addChannel(Thread.currentThread());
        Monitor.getInstance().addStatistic(Thread.currentThread());
        Monitor.getInstance().addRandomStream(Thread.currentThread());

        /* Creazione utenti */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, MyConstants.protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));

        /* Avvio simulazione */
        while (true) {
            nextEvent = fel.getNextEvent();
            nextEvent.solveEvent();
        }

        //System.out.println("Fine simulazione " + Monitor.getFEL(Thread.currentThread()).getSimTime());
    }
}
