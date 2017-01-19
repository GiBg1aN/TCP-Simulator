package mainPackage;

import components.Event;
import components.FEL;
import components.Monitor;
import components.User;
import statistics.Statistics;

public class RunPilota implements Runnable {
    private int id;
    Thread t = new Thread(this);

    public void start() {
        t.start();
    }

    @Override
    public void run() {

        final int N_USERS = MyConstants.K;
        Event nextEvent;
        Monitor.addFEL(Thread.currentThread());
        FEL fel = Monitor.getFEL(Thread.currentThread());
        Monitor.addCHANNEL(Thread.currentThread());
        Statistics.getWriterInstance();
        System.out.println("Inizio simulazione");

        /* Creazione utenti */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, MyConstants.protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));

        /* Avvio simulazione */
        while (Monitor.getFEL(Thread.currentThread()).getSimTime() < MyConstants.simulationTime) {
            nextEvent = fel.getNextEvent();
            nextEvent.solveEvent();
        }

        System.out.println("Fine simulazione " + Monitor.getFEL(Thread.currentThread()).getSimTime());

        Statistics.printStatistics();
        Statistics.closeStream();
    }

}
