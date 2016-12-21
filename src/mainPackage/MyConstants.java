package mainPackage;

public class MyConstants {
    public static final int K = 4; //Numero utenti
    public enum SegmentType {
        ACK,
        DATA        
    }
    public enum TCPProtocolType {
        RENO,
        TAHOE,
        AIMD
    }
    public static final int T = 25; //Lunghezza coda
    public static final int N = 1; //Numero segmenti per utente
    public static final int MSS = 1; //Maximum Segment Size
    public static final double TIMEOUT = 0.06;
}
