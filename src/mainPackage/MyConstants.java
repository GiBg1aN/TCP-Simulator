package mainPackage;

import umontreal.ssj.probdist.GeometricDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class MyConstants {
    public static final int K = 25; // Numero utenti
    public static final int T = 100; // Lunghezza coda
    public static final int MSS = 1; // Maximum Segment Size
    public static final double TIMEOUT = 0.3;
    public static final int SSTHRESH = 32;
    public static final double G = 0.2;
    public static final double P = 0.98;
    public static final double MU = 0.00001;
    public static final double TRAVEL_TIME = 0.01;
    
    public static final RandomStream uniformRandomStream = new LFSR113();

    
    public static int generateSegmentsToSend() {
        double gap = UniformGen.nextDouble(uniformRandomStream, 0, G);
        int segmentsToSend = 1;
        
        while (GeometricDist.prob(G, segmentsToSend) > gap) {
            segmentsToSend++;
        }
        
        return segmentsToSend;
    }
    
    public static boolean segmentNotCorrupted() { return (UniformGen.nextDouble(uniformRandomStream, 0, 1) < P); }
}
