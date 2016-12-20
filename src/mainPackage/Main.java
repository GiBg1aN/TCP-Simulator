package mainPackage;

import components.Channel;
import components.FEL;
import components.User;


public class Main {
    static final int nUsers = MyConstants.K;
    
    public static void main(String[] args){
        int nextEvent;
        FEL FEL = new FEL(nUsers); 
        Channel channel = new Channel();
        User[] users = new User[nUsers];
        
        System.out.println("Inizio simulazione");
        
        /* ---------------------------------------------------------- */
        for(int i = 0; i < nUsers; i++){
            User user = new User(i);
            users[i] = user;
        }
        /* ---------------------------------------------------------- */
        
        while(true){
            nextEvent = FEL.getNextEvent(); // Ottengo il prossimo evento
            if(nextEvent<=nUsers){
                users[nextEvent].transmit(); 
                // Se l'utente ha ricevuto tutti gli ack invia i prossimi
                // segmenti altrimenti va in timeout e rispedisce
            } else {
                channel.dequeueSegment(); // Estrae un segmento e lo "risolve"
            }     
        }       
    }  
}
