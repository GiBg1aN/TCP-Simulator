package components;

import GUI.Chart;
import java.util.HashMap;
import java.util.Map;
import mainPackage.MyConstants;
import static mainPackage.MyConstants.G;
import static mainPackage.MyConstants.P;
import statistics.Statistics;
import umontreal.ssj.probdist.GeometricDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class Monitor {
    private final Map<Thread, FEL> FELMap = new HashMap<>();
    private final Map<Thread, Channel> channelMap = new HashMap<>();
    private final Map<Thread, Statistics> statisticsMap = new HashMap<>();
    private final Map<Thread, RandomStream> randomStreamsMap = new HashMap<>();
    
    private int checked;
    private double checkTime = 3;
    
    private static final Monitor monitorInstance = new Monitor();

    
    
    private Monitor() {};
    
    public static Monitor getInstance() { return monitorInstance; }
    
    public synchronized void checkPoint(double simTime) {
        try {
            if (simTime >= checkTime) {
                checked++;
                wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized boolean gatherInformation() {
        return checked == MyConstants.N_THREAD;
    }
    
    
    
    /* ADDER */
    public synchronized void addFEL(Thread t) { FELMap.put(t, new FEL()); }

    public synchronized void addChannel(Thread t) { channelMap.put(t, new Channel(t)); }

    public synchronized void addStatistic(Thread t) { statisticsMap.put(t, new Statistics()); }

    public synchronized void addRandomStream(Thread t) { randomStreamsMap.put(t, new LFSR113()); }

    
    /* GLOBAL STATISTICS */
    public synchronized double campionaryMean() { 
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).average().getAsDouble();
    }

    public synchronized double campionaryVariance() {
        return statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().meanDevStan() * x.getValue().meanDevStan()).average().getAsDouble();
    }

    public synchronized double minMean() {
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).min().getAsDouble();
    }

    public synchronized double maxMean() { 
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).max().getAsDouble();
    }

    public int generateSegmentsToSend(Thread t) {
        double gap = UniformGen.nextDouble(randomStreamsMap.get(t), 0, G);
        int segmentsToSend = 1;

        while (GeometricDist.prob(G, segmentsToSend) > gap) {
            segmentsToSend++;
        }
        return segmentsToSend;
    }

    public boolean isSegmentNotCorrupted(Thread t) { return (UniformGen.nextDouble(randomStreamsMap.get(t), 0, 1) < P); }

    public synchronized boolean checkConfidentialRange(double d) {
        double min = -d * campionaryVariance() + campionaryMean();
        double max = d * campionaryVariance() + campionaryMean();
        int counter = 0;
        for (Statistics s : statisticsMap.values()) {
            if (s.mean() > min && s.mean() < max) {
                counter++;
            }
        }
        Chart.getInstance().addValue(minMean(), campionaryMean(), maxMean());
        boolean res =  counter / (double) statisticsMap.size() >= 0.95;
        
        checked = 0;
        checkTime += 0.5;
        notifyAll();
        
        return res;
    }

    
    /* GETTER */
    public synchronized Map<Thread, FEL> getFELs() { return FELMap; }

    public synchronized Map<Thread, Statistics> getSTATISTICs() { return statisticsMap; }
    
    public synchronized FEL getFEL(Thread t) { return FELMap.get(t); }

    public synchronized Channel getChannel(Thread t) { return channelMap.get(t); }

    public synchronized Statistics getStatistic(Thread t) { return statisticsMap.get(t); }

    public synchronized RandomStream getRandomStream(Thread t) { return randomStreamsMap.get(t); }    
}
