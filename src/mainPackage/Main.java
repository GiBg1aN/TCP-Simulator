package mainPackage;

import GUI.GUI;
import components.Event;
import components.FEL;
import components.User;
import java.io.IOException;
import statistics.Statistics;


public class Main {
    public static void main(String[] args)  {
        GUI.runGui();
    }
    
    
    public static void run() throws InterruptedException, IOException {
        final int N_USERS = MyConstants.K;
        Event nextEvent;
        FEL fel = FEL.getInstance();
        
        System.out.println("Inizio simulazione");

        /* Creazione utenti */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, MyConstants.protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));

        /* Avvio simulazione */
        while (FEL.getInstance().getSimTime() < MyConstants.simulationTime) {
            nextEvent = fel.getNextEvent();
            nextEvent.solveEvent();
        }
        
        System.out.println("Fine simulazione");
        
        Statistics.printStatistics();
        Statistics.closeStream();
    }
}
