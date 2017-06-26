package mainPackage;

import GUI.GUI;
import components.Monitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import statistics.Statistics;

/**
 * This class initialises the GUI and starts all pilot runs.
 */
public class Main {
    public static void main(String[] args) {
        GUI.runGui();
    }

    public static void run(PilotRun[] pilotRun) {
        boolean flag = true;
        Statistics statistics = new Statistics();
        
        while (flag) {
            try {
                Thread.sleep(250);
                if (Monitor.getInstance().gatherInformation()) {
                    if (Monitor.getInstance().checkConfidentialRange()) {
                        System.out.println("Simulation ended");
                        flag = false;
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Simulation stopped");
            }
        }
        
        statistics.getWriterInstance();
        statistics.openStreamForTimes();
        
        statistics.printGlobalStatistics();
        Monitor.getInstance().getGatheredMeans().forEach(x -> statistics.printTimes(x.getMin(), x.getMean(), x.getMax()));
        
        statistics.closeStream();
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }
}
