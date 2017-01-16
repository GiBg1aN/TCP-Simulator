package mainPackage;

import components.Event;
import components.FEL;
import components.User;
import java.io.IOException;
import java.io.PrintWriter;
import statistics.Statistics;


public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        final int N_USERS = MyConstants.K;
        Event nextEvent;
        FEL fel = FEL.getInstance();
        //Channel channel = Channel.getInstance();
        PrintWriter writer = Statistics.getWriterInstance();
        TCPProtocolType protocolType = TCPProtocolType.AIMD;
        
        if (args.length == 1) {
            if (args[0].equals("-aimd")) {
                writer.println("PROTOCOL TYPE: AIMD");
                protocolType = TCPProtocolType.AIMD;
            } else if (args[0].equals("-tahoe")) {
                writer.println("PROTOCOL TYPE: TAHOE");
                protocolType = TCPProtocolType.TAHOE;            
            } else if (args[0].equals("-reno")) {
                writer.println("PROTOCOL TYPE: RENO");
                protocolType = TCPProtocolType.RENO;            
            } else {
                System.out.println("Argomenti non validi.");
                System.exit(1);
            }
        } else {
            System.out.println("Numero di argomenti non valido.");
            System.exit(1);
        }
        
        System.out.println("Inizio simulazione");

        /* ---------------------------------------------------------- */
        for (int i = 0; i < N_USERS; i++) {
            fel.scheduleNextEvent(new Event(0.0, new User(i, protocolType)));
        }
        fel.scheduleNextEvent(new Event(0.0, EventType.CH_SOLVING));
        /* ---------------------------------------------------------- */

        while (FEL.getInstance().getSimTime() < 1000) {
            nextEvent = fel.getNextEvent(); // Ottengo il prossimo evento
            nextEvent.solveEvent();
        }
        
        System.out.println("Fine simulazione");
        
        Statistics.printStatistics();
        Statistics.closeStream();
    }
}
