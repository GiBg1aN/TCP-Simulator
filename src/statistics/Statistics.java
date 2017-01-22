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
    private int segmentCounter;
    private int timeout;
    private int corruptedSegmentsNumber;
    private double sum;
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
        
        if (firstValue) {
            max = d;
            min = d;
            firstValue = false;
        } else {
            max = (max > d) ? max : d;
            min = (min < d) ? min : d;
        }
        meanDevStanCounter += (d * d);
        
        Monitor.getInstance().checkPoint(Monitor.getInstance().getFEL(Thread.currentThread()).getSimTime());
    }
    
    public void increaseTimeout() { timeout++; }
    
    public void increaseCorruptedSegmentsNumber() { corruptedSegmentsNumber++; }
    
    public double mean() { return sum / segmentCounter; }
    
    public double meanDevStan() { return (Math.sqrt((segmentCounter * meanDevStanCounter) - (sum * sum)) / segmentCounter); }
    
    public double ERTT() { return mean(); }

    public double devRTT(double devRTT, DataSegment item) {
        return (3/4 * devRTT) + (1/4 * Math.abs(ERTT() - (item.getReceivedTimestamp() - item.getSentTimestamp())));
    }
    
    public double throughput(Thread t) { return segmentCounter / (Monitor.getInstance().getFEL(t).getSimTime()); }
    
    public double maxSimTime() {
        return Monitor.getInstance().getFELs().entrySet().stream()
                .mapToDouble(x -> x.getValue().getSimTime())
                .max()
                .getAsDouble();
    }

    
    /* FORMATTED PRINTS */
    public void printTimes(double minMean, double campionaryMean, double maxMean) {
        if (!Double.isNaN(minMean)  && !Double.isNaN(campionaryMean) && !Double.isNaN(maxMean)) {
            times.append(minMean + "," + campionaryMean + "," + maxMean + "\n");            
        }
    }
    
    public void printGlobalStatistics() {
        writer.println("-GLOBALS-");
        printProtocol();
        printConstants();
        double meanMeanDevStan = Monitor.getInstance().getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().meanDevStan())
                .average()
                .getAsDouble();
        int meanTimeout = (int) Monitor.getInstance().getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().timeout)
                .average()
                .getAsDouble();
        int meanCorruptedSegmentsNumber = (int) Monitor.getInstance().getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().corruptedSegmentsNumber)
                .average()
                .getAsDouble();
        double meanThroughput = Monitor.getInstance().getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().throughput(x.getKey()))
                .average()
                .getAsDouble();
        int meanSegmentsSent = (int) Monitor.getInstance().getSTATISTICs().entrySet().stream()
                .mapToDouble(x -> x.getValue().segmentCounter)
                .average()
                .getAsDouble();
        
        writer.append("Mean Response time: " + Monitor.getInstance().campionaryMean() +
                "\nMin response time: " + Monitor.getInstance().minMean() +
                "\nMax response time: " + Monitor.getInstance().maxMean() +
                "\nStandard Deviation: " + meanMeanDevStan +
                "\n#Timeout: " + meanTimeout +
                "\n#Corrupted Segments: " + meanCorruptedSegmentsNumber +
                "\nThroughput: " + meanThroughput +
                "\nSegments sent: " + meanSegmentsSent +
                "\nSim. Time: " + maxSimTime() + "\n");
    }
    
    public void printProtocol() { writer.append(MyConstants.protocolType.toString() + "\n"); }
    
    public void printConstants() { writer.append("T: " + T + "\nP: " + P + "\nG: " + G + "\nK: " + K + "\n"); }
        
    
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
                    f = new File("times_" + i + ".csv");
                    if (!f.exists() || f.isDirectory()) {
                        flag = false;
                        filename = ("out/times_" + i + ".csv");
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
