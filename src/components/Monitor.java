package components;

import GUI.Chart;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static mainPackage.MyConstants.G;
import static mainPackage.MyConstants.P;
import statistics.Statistics;
import umontreal.ssj.probdist.GeometricDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

/**
 *
 * @author andrea
 */
public class Monitor {

    private static Map<Thread, FEL> FELs = new HashMap<>();

    private static Map<Thread, Channel> CHANNELs = new HashMap<>();

    private static Map<Thread, Statistics> STATISTICs = new HashMap<>();

    private static Map<Thread, RandomStream> RANDOMSTREAMs = new HashMap<>();

    public synchronized static void addFEL(Thread t) {
        FELs.put(t, new FEL());
    }

    public synchronized static void addCHANNEL(Thread t) {
        CHANNELs.put(t, new Channel(t));
    }

    public synchronized static void addSTATISTIC(Thread t) {
        STATISTICs.put(t, new Statistics());
    }

    public synchronized static void addRANDOMSTREAM(Thread t) {
        RANDOMSTREAMs.put(t, new LFSR113());
    }

    public synchronized static FEL getFEL(Thread t) {
        return FELs.get(t);
    }

    public synchronized static Channel getCHANNEL(Thread t) {
        return CHANNELs.get(t);
    }

    public synchronized static Statistics getSTATISTIC(Thread t) {
        return STATISTICs.get(t);
    }

    public synchronized static RandomStream getRANDOMSTREAM(Thread t) {
        return RANDOMSTREAMs.get(t);
    }

    public synchronized static double evalCampionaryMean() {
        return STATISTICs.entrySet().stream().map(x -> x.getValue().evalMean()).reduce(0.0, (x, y) -> x + y) / STATISTICs.size();
    }

    public synchronized static double evalCampionaryVariance() {
        return STATISTICs.entrySet().stream().map(x -> x.getValue().evalMeanDevStan() * x.getValue().evalMeanDevStan()).reduce(0.0, (x, y) -> x + y) / STATISTICs.size();
    }

    public synchronized static double minMean() {
        return STATISTICs.entrySet().stream().map(x -> x.getValue().evalMean()).min(Double::compare).get();
    }

    public synchronized static double maxMean() {
        return STATISTICs.entrySet().stream().map(x -> x.getValue().evalMean()).max(Double::compare).get();
    }

    public static int generateSegmentsToSend(Thread t) {
        double gap = UniformGen.nextDouble(RANDOMSTREAMs.get(t), 0, G);
        int segmentsToSend = 1;

        while (GeometricDist.prob(G, segmentsToSend) > gap) {
            segmentsToSend++;
        }
        return segmentsToSend;
    }

    public static boolean isSegmentNotCorrupted(Thread t) {
        return (UniformGen.nextDouble(RANDOMSTREAMs.get(t), 0, 1) < P);
    }

    public static boolean isInConfidentialRange(double d) {
        double min = -d*evalCampionaryVariance() + evalCampionaryMean();
        double max = d*evalCampionaryVariance() + evalCampionaryMean();
        int counter = 0;
        for(Statistics s : STATISTICs.values()){
            if(s.evalMean() > min && s.evalMean() < max)
                counter++;
        }
        Chart.getInstance().addValue(minMean(), evalCampionaryMean(), maxMean());
        
        if(counter/STATISTICs.size() >= 0.95)
            return true;
        return false;
    }

}
