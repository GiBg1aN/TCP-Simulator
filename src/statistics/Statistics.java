package statistics;

import components.DataSegment;
import components.FEL;
import components.Monitor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import mainPackage.MyConstants;
import static mainPackage.MyConstants.G;
import static mainPackage.MyConstants.K;
import static mainPackage.MyConstants.P;
import static mainPackage.MyConstants.T;


public class Statistics {
    private double sum;
    private int segmentCounter;
    private int timeout;
    private int corruptedSegmentsNumber;
    private PrintWriter writer;
    private double max;
    private double min;
    private double meanDevStanCounter;
    private PrintWriter times;
    private boolean firstValue = true;
    
    /* STATISTICS */
    public void refreshResponseTimeStatistics(DataSegment item) {
        double d = item.getReceivedTimestamp() - item.getSentTimestamp();
        
        sum += d;
        segmentCounter++;
        //times.append(d + "\n");
        if (firstValue) {
            max = d;
            min = d;
            firstValue = false;
        } else {
            max = (max > d) ? max : d;
            min = (min < d) ? min : d;
        }
        meanDevStanCounter += (d * d);
    }
    
    public void increaseTimeout() { timeout++; }
    
    public void increaseCorruptedSegmentsNumber() { corruptedSegmentsNumber++; }
    
    public double evalMean() { return (sum / segmentCounter); }
    
    public double evalMeanDevStan() { return (Math.sqrt((segmentCounter * meanDevStanCounter) - (sum * sum)) / segmentCounter); }
    
    public double getERTT() { return evalMean(); }

    public double getDevRTT(double devRTT, DataSegment item) {
        //System.out.println((item.getReceivedTimestamp() - item.getSentTimestamp()));
        return (3/4 * devRTT) + (1/4 * Math.abs(getERTT() - (item.getReceivedTimestamp() - item.getSentTimestamp())));
    }
    
 
    /* FORMATTED PRINTS */
    public void printStatistics() {
        printProtocol();
        printConstants();
        printResponseTimeStatistics();
        printTimeout();
        printCorruptedSegmentsNumber();
        printThroughput();
        printSegmentsSent();
    }
    
    
    
    public void printProtocol() { writer.append( MyConstants.protocolType.toString() + "\n"); }
    
    public void printConstants() { writer.append("T: " + T + "\nP: " + P + "\nG: " + G + "\nK: " + K + "\n"); }    
    
    public void printResponseTimeStatistics() { 
        writer.append("Mean response time: "+ evalMean() +
                "\nMin  response time: " + min +
                "\nMax  response time: " + max +
                "\nStandard Deviation: " + evalMeanDevStan() + "\n"); 
    }
    
    public void printTimeout() { writer.append("#Timeout: " + timeout + "\n"); }
    
    public void printCorruptedSegmentsNumber() { writer.append("#CorruptedSegments: " + corruptedSegmentsNumber + "\n"); }
    
    public void printThroughput() { writer.append("Throughput: " + (segmentCounter / (Monitor.getFEL(Thread.currentThread()).getSimTime())) + "\n"); }
    
    public void printSegmentsSent() { writer.append("Segments sent: "+ segmentCounter + "\n"); }
    
    
    /* STREAMS */
    public PrintWriter openStream() {
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
    
    public PrintWriter openStreamForTimes() {
        try {
            String filename = "out/times";
            
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

            times = new PrintWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return times;
        }
    }
    
    
    public void closeStream() { writer.close(); }
    
    public PrintWriter getWriterInstance() {
        if (writer == null) {
            return openStream();
        }
        return writer;
    }
}
