package mainPackage;

import components.Channel;
import components.FEL;
import components.User;


public class Main {
    static final int N_USERS = MyConstants.K;
    
    
    public static void main(String[] args) {
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
        
        while(true) {
            nextEvent = fel.getNextEvent(); // Ottengo il prossimo evento
            if (nextEvent <= N_USERS) {
                users[nextEvent].transmit(); 
                // Se l'utente ha ricevuto tutti gli ack invia i prossimi
                // segmenti altrimenti va in timeout e rispedisce
            } else {
                channel.dequeueSegment(); // Estrae un segmento e lo "risolve"
            }     
        }       
    }  
}
