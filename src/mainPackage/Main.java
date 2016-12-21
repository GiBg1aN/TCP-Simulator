package mainPackage;

import components.Channel;
import components.FEL;
import components.User;
import static java.lang.Thread.sleep;


public class Main {
    static final int N_USERS = MyConstants.K;

    public static void main(String[] args) throws InterruptedException {
        int nextEvent;
        FEL fel = new FEL(N_USERS);
        Channel channel = Channel.getInstance();
        User[] users = new User[N_USERS];

        System.out.println("Inizio simulazione");

        /* ---------------------------------------------------------- */
        for (int i = 0; i < N_USERS; i++) {
            users[i] = new User(i);
        }
        /* ---------------------------------------------------------- */

        while (true) {
            nextEvent = fel.getNextEvent(); // Ottengo il prossimo evento
            //System.out.println(fel.getEventTime(nextEvent) + " - " + nextEvent);
            //sleep(1000);
            if (nextEvent < N_USERS) {
                users[nextEvent].transmit(fel.getEventTime(nextEvent));
                fel.setEventTime(nextEvent, 0.3);
                // Se l'utente ha ricevuto tutti gli ack invia i prossimi
                // segmenti altrimenti va in timeout e rispedisce
            } else {
                channel.dequeueSegment(fel.getEventTime(nextEvent)); // Estrae un segmento e lo "risolve"
                fel.setEventTime(nextEvent, 0.01);
            }
        }
    }
}
