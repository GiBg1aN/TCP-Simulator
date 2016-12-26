package mainPackage;

import components.Channel;
import components.Event;
import components.FEL;
import components.User;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int N_USERS = MyConstants.K;
        Event nextEvent;
        FEL fel = FEL.getInstance();
        Channel channel = Channel.getInstance();

        System.out.println("Inizio simulazione");

        /* ---------------------------------------------------------- */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, TCPProtocolType.TAHOE)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));
        /* ---------------------------------------------------------- */

        while (FEL.getInstance().getSimTime() < 30) {
            nextEvent = fel.getNextEvent(); // Ottengo il prossimo evento
            nextEvent.solveEvent();
        }
    }
}
