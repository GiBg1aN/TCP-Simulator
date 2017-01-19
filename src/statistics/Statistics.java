package statistics;

import components.DataSegment;
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
    private double max;
    private double min;
    private double meanDevStanCounter;
    private boolean firstValue = true;
    private PrintWriter writer;
    private PrintWriter times;

    
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
    
    public double evalThroughput() { return segmentCounter / (Monitor.getFEL(Thread.currentThread()).getSimTime()); } // TODO: probabilmente si può togliere.
    
    public double evalThroughput(Thread t) { return segmentCounter / (Monitor.getFEL(t).getSimTime()); }

 
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
    
    public void printTimes(double minMean, double campionaryMean, double maxMean) { 
        times.append(minMean + "," + campionaryMean + "," + maxMean + "\n");
    }
    
    public void printGlobalStatistics() {
        writer.println("-GLOBALS-");
        printProtocol();
        printConstants();
        double meanResponseTime = Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().evalMean())
                .average()
                .getAsDouble();
        double maxMaxResponseTime = Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().max)
                .max()
                .getAsDouble();
        double minMinResponseTime = Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().min)
                .min()
                .getAsDouble();
        double meanMeanDevStan = Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().evalMeanDevStan())
                .average()
                .getAsDouble();
        int meanTimeout = (int) Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().timeout)
                .average()
                .getAsDouble();
        int meanCorruptedSegmentsNumber = (int) Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().corruptedSegmentsNumber)
                .average()
                .getAsDouble();
        double meanThroughput = Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().evalThroughput(x.getKey()))
                .average()
                .getAsDouble();
        int meanSegmentsSent = (int) Monitor.getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().segmentCounter)
                .average()
                .getAsDouble();
        Double maxSimTime = Monitor.getFELs().entrySet().stream()
                .mapToDouble(x -> x.getValue().getSimTime())
                .max()
                .getAsDouble();
        writer.append("Mean Response time: " + meanResponseTime +
                "\nMin response time: " + minMinResponseTime +
                "\nMax response time: " + maxMaxResponseTime +
                "\nStandard Deviation: " + meanMeanDevStan +
                "\n#Timeout: " + meanTimeout +
                "\n#Corrupted Segments: " + meanCorruptedSegmentsNumber +
                "\nThroughput: " + meanThroughput +
                "\nSegments sent: " + meanSegmentsSent +
                "\nSim. Time: " + maxSimTime);
    }
    
    public void printProtocol() { writer.append(MyConstants.protocolType.toString() + "\n"); }
    
    public void printConstants() { writer.append("T: " + T + "\nP: " + P + "\nG: " + G + "\nK: " + K + "\n"); }
    
    public void printResponseTimeStatistics() { 
        writer.append("Mean response time: "+ evalMean() +
                "\nMin response time: " + min +
                "\nMax response time: " + max +
                "\nStandard Deviation: " + evalMeanDevStan() + "\n"); 
    }
    
    public void printTimeout() { writer.append("#Timeout: " + timeout + "\n"); }
    
    public void printCorruptedSegmentsNumber() { writer.append("#Corrupted Segments: " + corruptedSegmentsNumber + "\n"); }
    
    public void printThroughput() { writer.append("Throughput: " + evalThroughput() + "\n"); }
    
    public void printSegmentsSent() { writer.append("Segments sent: "+ segmentCounter + "\n"); }
    
    
    /* STREAMS */
    public PrintWriter openStream() {
        try {
            String filename = "out/output_header";
            
            File f = new File(filename);
            if (f.exists() && !f.isDirectory()) { 
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
            String filename = "out/times.csv";
            
            File f = new File(filename);
            if (f.exists() && !f.isDirectory()) { 
                boolean flag = true;
                int i = 0;
                while (flag) {
                    f = new File("times" + "_" + i + ".csv");
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
    
    public void closeStream() { 
        writer.close(); 
        times.close();
    }
    
    public PrintWriter getWriterInstance() {
        if (writer == null) {
            return openStream();
        }
        return writer;
    }
}
