package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * @see http://stackoverflow.com/questions/5048852
 */
public class Chart extends ApplicationFrame {

    private static final String TITLE = "Dynamic Series";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final float MINMAX = 20;
    private static final int COUNT = 2 * 60;
    private static final int FAST = 100;
    private static final int SLOW = FAST * 5;
    private static final Random random = new Random();
    private Timer timer;
    private DynamicTimeSeriesCollection dataset;
    private float dataSegm = 0;
    private static Chart instance;

    public Chart(final String title) {
        super(title);
        dataset
                = new DynamicTimeSeriesCollection(2, COUNT, new Second());
        dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2011));
        dataset.addSeries(gaussianData(), 0, "Gaussian data");
        dataset.addSeries(gaussianData2(), 1, "Gaussian dataasd");
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
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(run);
        btnPanel.add(combo);
        this.add(btnPanel, BorderLayout.SOUTH);

        timer = new Timer(SLOW, new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
  

            }
        });
    }

    private float randomValue() {
        return (float) (random.nextGaussian() * MINMAX / 3);
    }

    private float[] gaussianData() {
        float[] a = new float[1];
        for (int i = 0; i < a.length; i++) {
            a[i] = 1;
        }
        return a;
    }

    private float[] gaussianData2() {
        float[] a = new float[100];
        for (int i = 0; i < a.length; i++) {
            a[i] = 10;
        }
        return a;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Congestion window", "hh:mm:ss", "size", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        domain.setVisible(false);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(0, MINMAX);
        NumberAxis y = (NumberAxis) plot.getRangeAxis();
        y.setTickUnit(new NumberTickUnit(1));
        //XYLineAndShapeRenderer renderer
           //     = (XYLineAndShapeRenderer) plot.getRenderer();
        //renderer.setBaseShapesVisible(true);

        return result;
    }

    public void start() {
        timer.start();
    }

    public float[] addValue(int size) {
        float[] newData = new float[2];
        newData[0] = size;
        newData[1] = 10;
        dataSegm++;
        dataset.advanceTime();
        dataset.appendData(newData);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newData;
    }
    
    public void reset() {
        float[] newData = new float[2];
        for(int i = 0; i < 5; i++) {
            newData[0] = 0;
            newData[1] = 10;
            dataSegm++;
            dataset.advanceTime();
            dataset.appendData(newData);
        }
        newData[0] = 1;
        newData[1] = 10;
        
        dataSegm++;
        dataset.advanceTime();
        dataset.appendData(newData);
    }

    public static Chart getInstance() {
        if (instance == null) {
            instance = initialize();
        }
        return instance;
    }

    public static Chart initialize() {
        Chart demo = new Chart("User");
        demo.pack();
        demo.setVisible(true);
        demo.start();
        return demo;
    }
    /*
     public static void main(final String[] args) {
     EventQueue.invokeLater(new Runnable() {

     @Override
     public void run() {
     Graph a = Graph.initialize();            
     }
     });
     }*/
}
