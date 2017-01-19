package mainPackage;

import GUI.GUI;
import components.Monitor;
import statistics.Statistics;

public class Main {

    public static void main(String[] args) {
        GUI.runGui();
    }

    public static void run(RunPilota[] runPilota) {
        boolean flag = true;
        Statistics statistics = new Statistics();
        statistics.getWriterInstance();
        statistics.openStreamForTimes();
        
        while(flag){
            try {
                Thread.sleep(0);
                
                if(Monitor.minMean() > Monitor.evalCampionaryMean()*0.95 &&
                   Monitor.maxMean() < Monitor.evalCampionaryMean()*1.05 &&
                   Monitor.isInConfidentialRange(1.96)){
                    System.out.println("BANANE TANTO TANTO DUREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    flag = false;
                    for(RunPilota r : runPilota){
                        r.stop();
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Simulazione stoppata");
            } 
        }
    }
}
