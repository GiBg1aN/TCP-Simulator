package components;

import GUI.Chart;
import java.util.HashMap;
import java.util.Map;
import static mainPackage.MyConstants.G;
import static mainPackage.MyConstants.P;
import statistics.Statistics;
import umontreal.ssj.probdist.GeometricDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class Monitor {
    private static final Map<Thread, FEL> FELMap = new HashMap<>();
    private static final Map<Thread, Channel> channelMap = new HashMap<>();
    private static final Map<Thread, Statistics> statisticsMap = new HashMap<>();
    private static final Map<Thread, RandomStream> randomStreamsMap = new HashMap<>();

    
    /* ADDER */
    public synchronized static void addFEL(Thread t) { FELMap.put(t, new FEL()); }

    public synchronized static void addChannel(Thread t) { channelMap.put(t, new Channel(t)); }

    public synchronized static void addStatistic(Thread t) { statisticsMap.put(t, new Statistics()); }

    public synchronized static void addRandomStream(Thread t) { randomStreamsMap.put(t, new LFSR113()); }

    
    /* GLOBAL STATISTICS */
    public synchronized static double campionaryMean() { 
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).average().getAsDouble();
    }

    public synchronized static double campionaryVariance() {
        return statisticsMap.entrySet().stream()
                .mapToDouble(x -> x.getValue().meanDevStan() * x.getValue().meanDevStan()).average().getAsDouble();
    }

    public synchronized static double minMean() {
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).min().getAsDouble();
    }

    public synchronized static double maxMean() { 
        return statisticsMap.entrySet().stream().mapToDouble(x -> x.getValue().mean()).max().getAsDouble();
    }

    public static int generateSegmentsToSend(Thread t) {
        double gap = UniformGen.nextDouble(randomStreamsMap.get(t), 0, G);
        int segmentsToSend = 1;

        while (GeometricDist.prob(G, segmentsToSend) > gap) {
            segmentsToSend++;
        }
        return segmentsToSend;
    }

    public static boolean isSegmentNotCorrupted(Thread t) { return (UniformGen.nextDouble(randomStreamsMap.get(t), 0, 1) < P); }

    public static boolean isInConfidentialRange(double d) {
        double min = -d * campionaryVariance() + campionaryMean();
        double max = d * campionaryVariance() + campionaryMean();
        int counter = 0;
        for (Statistics s : statisticsMap.values()) {
            if (s.mean() > min && s.mean() < max) {
                counter++;
            }
        }
        Chart.getInstance().addValue(minMean(), campionaryMean(), maxMean());

        return counter / (double) statisticsMap.size() >= 0.95;
    }

    
    /* GETTER */
    public static Map<Thread, FEL> getFELs() { return FELMap; }

    public static Map<Thread, Statistics> getSTATISTICs() { return statisticsMap; }
    
    public synchronized static FEL getFEL(Thread t) { return FELMap.get(t); }

    public synchronized static Channel getChannel(Thread t) { return channelMap.get(t); }

    public synchronized static Statistics getStatistic(Thread t) { return statisticsMap.get(t); }

    public synchronized static RandomStream getRandomStream(Thread t) { return randomStreamsMap.get(t); }    
}
