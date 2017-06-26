package mainPackage;


public class MyConstants {
    public static int K = 4; // Users
    public static int T = 100; // Channel lenght
    public static final int MSS = 1; // Maximum Segment Size
    public static final double TIMEOUT = 0.03;
    public static final int SSTHRESH = 32;
    public static double G = 0.2; // Geometric distribution probability
    public static double P = 0.98; // Segment integrity probability
    public static final double MU = 0.00001;
    public static final double TRAVEL_TIME = 0.01;
    public static TCPProtocolType protocolType = TCPProtocolType.AIMD;
    public static int simulationTime = 100;
    public static int N_THREAD = 15;  
    public static double WARM_UP = 10;
    public static double minERROR = 0.70;
    public static double maxERROR = 1.30;
}
