package statistics;

import components.DataSegment;
import components.FEL;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import mainPackage.MyConstants;
import static mainPackage.MyConstants.G;
import static mainPackage.MyConstants.K;
import static mainPackage.MyConstants.P;
import static mainPackage.MyConstants.T;


public class Statistics {
    private static double sum;
    private static int segmentCounter;
    private static int timeout;
    private static int corruptedSegmentsNumber;
    private static PrintWriter writer;
    private static double max;
    private static double min = 1;
    private static double meanDevStanCounter;
    
    
    /* STATISTICS */
    public static void refreshResponseTimeStatistics(DataSegment item) {
        double d = item.getReceivedTimestamp() - item.getSentTimestamp();
        sum += d;
        segmentCounter++;
        
        max = (max > d) ? max : d;
        min = (min < d) ? min : d;
        meanDevStanCounter += (d * d);
    }
    
    public static void increaseTimeout() { timeout++; }
    
    public static void increaseCorruptedSegmentsNumber() { corruptedSegmentsNumber++; }
    
    public static double evalMean() { return (sum / segmentCounter); }
    
    public static double evalMeanDevStan() { return (Math.sqrt((segmentCounter * meanDevStanCounter) - (sum * sum)) / segmentCounter); }
    
    public static double getERTT() { return evalMean(); }

    public static double getDevRTT(double devRTT, DataSegment item) {
        //System.out.println((item.getReceivedTimestamp() - item.getSentTimestamp()));
        return (3/4 * devRTT) + (1/4 * Math.abs(getERTT() - (item.getReceivedTimestamp() - item.getSentTimestamp())));
    }
    
 
    /* FORMATTED PRINTS */
    public static void printStatistics() {
        printProtocol();
        printConstants();
        printResponseTimeStatistics();
        printTimeout();
        printCorruptedSegmentsNumber();
        printThroughput();
        printSegmentsSent();
    }
    
    public static void printProtocol() { writer.append( MyConstants.protocolType.toString() + "\n"); }
    
    public static void printConstants() { writer.append("T: " + T + "\nP: " + P + "\nG: " + G + "\nK: " + K + "\n"); }    
    
    public static void printResponseTimeStatistics() { 
        writer.append("Mean response time: "+ evalMean() +
                "\nMin  response time: " + min +
                "\nMax  response time: " + max +
                "\nStandard Deviation: " + evalMeanDevStan() + "\n"); 
    }
    
    public static void printTimeout() { writer.append("#Timeout: " + timeout + "\n"); }
    
    public static void printCorruptedSegmentsNumber() { writer.append("#CorruptedSegments: " + corruptedSegmentsNumber + "\n"); }
    
    public static void printThroughput() { writer.append("Throughput: " + (segmentCounter / (FEL.getInstance().getSimTime())) + "\n"); }
    
    public static void printSegmentsSent() { writer.append("Segments sent: "+ segmentCounter + "\n"); }
    
    
    /* STREAMS */
    public static PrintWriter openStream() {
        try {
            String filename = "out/output_header";
            
            File f = new File(filename);
            if(f.exists() && !f.isDirectory()) { 
                boolean flag = true;
                int i = 0;
                while (flag) {
                    f = new File(filename + "_" + i);
                    if (!f.exists() || f.isDirectory()) {
                        flag = false;
                        filename += ("_" + i);
                    }
                    i++;
                }
            }

            writer = new PrintWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return writer;
        }
    }
    
    public static void closeStream() { writer.close(); }
    
    public static PrintWriter getWriterInstance() {
        if (writer == null) {
            return openStream();
        }
        return writer;
    }
}
