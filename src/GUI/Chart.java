package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.Timer;
import mainPackage.MyConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;


public class Chart extends ApplicationFrame {
    static Chart demo;
    private static final String TITLE = "Dynamic Series";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final double MAXRANGE = 0.5;
    private static final int COUNT = 2 * 60;
    private static final int FAST = 100;
    private static final int SLOW = FAST * 5;
    private static final Random random = new Random();
    private Timer timer;
    private DynamicTimeSeriesCollection dataset;
    private static Chart instance;
    float[] newData = new float[3];

    public Chart(final String title) {
        super(title);
        dataset = new DynamicTimeSeriesCollection(3, COUNT, new Second());
        dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));
        dataset.addSeries(gaussianData(), 0, "Min");
        dataset.addSeries(gaussianData(), 1, "Mean");
        dataset.addSeries(gaussianData(), 2, "Max");
        
        JFreeChart chart = createChart(dataset);

        final JButton run = new JButton(STOP);
        run.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (STOP.equals(cmd)) {
                    timer.stop();
                    run.setText(START);
                } else {
                    timer.start();
                    run.setText(STOP);
                }
            }
        });

        final JComboBox combo = new JComboBox();
        combo.addItem("Fast");
        combo.addItem("Slow");
        combo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Fast".equals(combo.getSelectedItem())) {
                    timer.setDelay(FAST);
                } else {
                    timer.setDelay(SLOW);
                }
            }
        });

        this.add(new ChartPanel(chart), BorderLayout.CENTER);
        //JPanel btnPanel = new JPanel(new FlowLayout());
        //btnPanel.add(run);
        //btnPanel.add(combo);
        //this.add(btnPanel, BorderLayout.SOUTH);

        timer = new Timer(SLOW, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private float randomValue() {
        return (float) (random.nextGaussian() * MAXRANGE / 3);
    }

    private float[] gaussianData() {
        float[] a = new float[1];
        for (int i = 0; i < a.length; i++) {
            a[i] = 1;
        }
        return a;
    }

    private float[] gaussianData2() {
        float[] a = new float[1];
        for (int i = 0; i < a.length; i++) {
            a[i] = MyConstants.SSTHRESH;
        }
        return a;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Congestion window of user0", "hh:mm:ss", "Size of congestion window", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        domain.setVisible(false);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(0.0205, 0.0225);
        NumberAxis y = (NumberAxis) plot.getRangeAxis();

        return result;
    }

    public void start() {
        timer.start();
    }
    

    public float[] addValue(double max, double mean, double min) {

        this.newData[0] = (float) min;
        this.newData[1] = (float) mean;
        this.newData[2] = (float) max;
        dataset.advanceTime();
        dataset.appendData(newData);
        try {
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
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
        demo = new Chart("User");
        demo.pack();
        demo.setVisible(true);
        demo.start();
        return demo;
    }
    
}
