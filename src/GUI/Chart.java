package GUI;

import components.Monitor;
import java.awt.BorderLayout;
import javax.swing.Timer;
import mainPackage.MyConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

/**
 * This class allow to show the real time trend of the throughput during the
 * simulation. The graph shows the minimum, the maximum and the mean of
 * throughput collecting data directly from <code>Statistics</code>.
 */
public class Chart extends ApplicationFrame {
    static Chart demo;
    private final int COUNT = 2 * 60;
    private final int FAST = 100;
    private final int SLOW = FAST * 5;
    private final Timer timer;
    private final DynamicTimeSeriesCollection dataset;
    private static Chart instance;
    float[] newData = new float[4];
    public ValueAxis range;

    public Chart(final String title) {
        super(title);
        dataset = new DynamicTimeSeriesCollection(4, COUNT, new Second());
        dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));

        dataset.addSeries(gaussianData(), 0, "Max");
        dataset.addSeries(gaussianData(), 1, "Mean");
        dataset.addSeries(gaussianData(), 2, "Min");
        dataset.addSeries(gaussianData(), 3, "SteadyState");

        JFreeChart chart = createChart(dataset);

        this.add(new ChartPanel(chart), BorderLayout.CENTER);

        timer = new Timer(SLOW, null);
    }

    private float[] gaussianData() {
        float[] a = new float[1];
        for (int i = 0; i < a.length; i++) {
            a[i] = 1;
        }
        return a;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Throughput variation during simulation", "hh:mm:ss", "Throughput", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        domain.setVisible(false);
        range = plot.getRangeAxis();
        range.setRange(0.0205, 0.0225);

        return result;
    }

    public void start() {
        timer.start();
    }


    public float[] addValue(double max, double mean, double min, double warmUp) {
        try {
            double minRange;
            double maxRange;
            double error;
            
            if (Monitor.getInstance().getCheckTime() > MyConstants.WARM_UP) {
                error = 5;
            } else {
                error = 100;
            }
            
            maxRange = Math.max(min + error, max + error);
            minRange = Math.min(min - error, max - error);
            range.setRange(minRange, maxRange);
            this.newData[0] = (float) min;
            this.newData[1] = (float) mean;
            this.newData[2] = (float) max;
            this.newData[3] = (float) warmUp;
            dataset.advanceTime();
            dataset.appendData(newData);
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newData;
    }

    public void reset(int ID) {
        if (ID == 0) {
            this.newData[0] = 1;
            this.newData[1] = MyConstants.SSTHRESH;
            
            for (int i = 0; i < 10; i++) {
                dataset.advanceTime();
                dataset.appendData(newData);
            }
        }
    }

    public static Chart getInstance() {
        if (instance == null) {
            instance = initialize();
        }
        return instance;
    }

    public static Chart initialize() {
        demo = new Chart("TCPSimulation");
        demo.pack();
        demo.setVisible(true);
        demo.start();
        return demo;
    }
}
