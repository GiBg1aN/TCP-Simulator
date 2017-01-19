package mainPackage;

import umontreal.ssj.probdist.GeometricDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class MyConstants {
    public static int K = 4; // Numero utenti
    public static int T = 100; // Lunghezza coda
    public static final int MSS = 1; // Maximum Segment Size
    public static final double TIMEOUT = 0.03;
    public static final int SSTHRESH = 32;
    public static double G = 0.2; // Probabilità di successo di una Geometrica.
    public static double P = 0.98; // Probabilità che un segmento non si corrompa.
    public static final double MU = 0.00001;
    public static final double TRAVEL_TIME = 0.01; // tempo che il pacchetto impiega nella trasmissione.
    public static TCPProtocolType protocolType = TCPProtocolType.AIMD;
    public static int simulationTime = 100;
    public static int N_THREAD = 15;    
}
