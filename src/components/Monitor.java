package components;

import GUI.Chart;
import java.util.HashMap;
import java.util.LinkedList;
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
    private final LinkedList<Range> gatheredMeans = new LinkedList<>();
    private int checked;
    private double checkTime = 0.05;

    private static final Monitor monitorInstance = new Monitor();

    
    private Monitor() {}
    
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

    public synchronized boolean gatherInformation() { return checked == MyConstants.N_THREAD; }
    
    public int generateSegmentsToSend(Thread t) {
        double gap = UniformGen.nextDouble(randomStreamsMap.get(t), 0, G);
        int segmentsToSend = 1;

        while (GeometricDist.prob(G, segmentsToSend) > gap) {
            segmentsToSend++;
        }
        return segmentsToSend;
    }

    public boolean isSegmentNotCorrupted(Thread t) { return (UniformGen.nextDouble(randomStreamsMap.get(t), 0, 1) < P); }

    public synchronized boolean checkConfidentialRange() {
        double mean = campionaryThroughputMean();
        int counter = 0;
        boolean res = false;

        if (gatheredMeans.size() >= 100) {
            System.out.println(gatheredMeans.size() + " - BANANA RIMOSSA");
            gatheredMeans.removeFirst();
        }
        
        if (Monitor.getInstance().minThroughput() > mean * MyConstants.minERROR && 
                Monitor.getInstance().maxThroughput() < mean * MyConstants.maxERROR &&
                Monitor.getInstance().checkTime >= MyConstants.WARM_UP) {
            gatheredMeans.addLast(new Range(minThroughput(), mean, maxThroughput()));
            System.out.println(gatheredMeans.size() + " - BANANA AGGIUNTA");
        }
        
        if (gatheredMeans.size() >= 50) {
            for (Range range : gatheredMeans) {
                if (range.containsMean(mean)) {
                    counter++;
                }
            }
            res = counter / gatheredMeans.size() >= 0.95;
        }

        double warmUp = (checkTime > MyConstants.WARM_UP) ? 100000 : 0;
        Chart.getInstance().addValue(minThroughput(), mean, maxThroughput(), warmUp, 0, 0);

        checked = 0;
        checkTime += 0.05;
        notifyAll();

        return res;
    }

    
    /* ADDER */
    public synchronized void addFEL(Thread t) { FELMap.put(t, new FEL()); }

    public synchronized void addChannel(Thread t) { channelMap.put(t, new Channel(t)); }

    public synchronized void addStatistic(Thread t) { statisticsMap.put(t, new Statistics()); }

    public synchronized void addRandomStream(Thread t) { randomStreamsMap.put(t, new LFSR113()); }

    
    /* GLOBAL STATISTICS */
    public synchronized double campionaryThroughputMean() {
        return statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().throughput(x.getKey()))
                .average()
                .getAsDouble();
    }

    public synchronized double ThroughputStd() {
        int n = MyConstants.N_THREAD;

        Double squareSum = statisticsMap.entrySet().stream()
                .mapToDouble(x -> Math.pow(x.getValue().throughput(x.getKey()), 2))
                .sum();
        Double sum = statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().throughput(x.getKey()))
                .sum();
        double res = Math.sqrt((n * squareSum) - (sum * sum)) / n;
        return res;
    }

    public synchronized double minThroughput() {
        return statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().throughput(x.getKey()))
                .min()
                .getAsDouble();
    }

    public synchronized double maxThroughput() {
        return statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().throughput(x.getKey()))
                .max()
                .getAsDouble();
    }
    

    /* GETTER */
    public double getCheckTime() { return checkTime; }
    
    public synchronized Map<Thread, FEL> getFELs() { return FELMap; }

    public synchronized Map<Thread, Statistics> getStatistics() { return statisticsMap; }

    public synchronized FEL getFEL(Thread t) { return FELMap.get(t); }

    public synchronized Channel getChannel(Thread t) { return channelMap.get(t); }

    public synchronized Statistics getStatistic(Thread t) { return statisticsMap.get(t); }

    public synchronized RandomStream getRandomStream(Thread t) { return randomStreamsMap.get(t); }

    public LinkedList<Range> getGatheredMeans() { return gatheredMeans; }
}
