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
        
        while (flag) {
            try {
                Thread.sleep(250);
                if (Monitor.getInstance().gatherInformation()) {
                    double minMean = Monitor.getInstance().minMean();
                    double campionaryMean = Monitor.getInstance().campionaryMean();
                    double maxMean = Monitor.getInstance().maxMean();

                    statistics.printTimes(minMean, campionaryMean, maxMean);

                    if (Monitor.getInstance().checkConfidentialRange(1.96) && 
                            minMean > campionaryMean * 0.95 && maxMean < campionaryMean * 1.05) {
                        System.out.println("FINE SIMULAZIONE");
                        flag = false;
                        /*for (RunPilota r : runPilota) {
                            r.stop();
                        }*/
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Simulazione stoppata");
            }
        }
        statistics.printGlobalStatistics();
        statistics.closeStream();
    }
}
