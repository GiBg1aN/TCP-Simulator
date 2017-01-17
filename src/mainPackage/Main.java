package mainPackage;

import components.Event;
import components.FEL;
import components.User;
import java.io.IOException;
import java.io.PrintWriter;
import statistics.Statistics;


public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
    
        GUI.GUI.runGui();
    }
    
    
    public static void run() {
        final int N_USERS = MyConstants.K;
        Event nextEvent;
        FEL fel = FEL.getInstance();
        //Channel channel = Channel.getInstance();
        PrintWriter writer = Statistics.getWriterInstance();

        
        System.out.println("Inizio simulazione");

        /* ---------------------------------------------------------- */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, MyConstants.protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));
        /* ---------------------------------------------------------- */

        while (FEL.getInstance().getSimTime() < MyConstants.simulationTime) {
            nextEvent = fel.getNextEvent(); // Ottengo il prossimo evento
            nextEvent.solveEvent();
        }
        
        System.out.println("Fine simulazione");
        
        Statistics.printStatistics();
        Statistics.closeStream();
    }
}
