package GUI;

import java.awt.GridLayout;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GUI {
    public static void runGui() {
        JFrame frame = new JFrame("TCP Simulation");
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(100);
        layout.setVgap(20);
        panel.setLayout(layout);
        panel.setSize(800, 400);
        
        JLabel protocolLabel = new JLabel("Seleziona protocollo: ");
        JLabel userNoLabel = new JLabel("Numero utenti:");
        JLabel queueLengthLabel = new JLabel("Lunghezza della coda:");
        JLabel segmentCorruptionInverseLabel = new JLabel("Probabilità che un segmento sia integro:");
        JLabel casualNumberLabel = new JLabel("Probabilità di successo geometrica:");
        
        JComboBox protocolComboBox = new JComboBox(new String[] {"AIMD", "Tahoe", "Reno"});
        JTextField userNoTextField = new JTextField("1", 3);
        JTextField queueLengthTextField = new JTextField("100", 3);
        
        int start = 0;
        int end = 1000;
        int value = 980;

        JTextField segmentCorruptionInverseTextField = new JTextField(String.valueOf(0.98), 5);
        JSlider segmentCorruptionInverseSlider = new JSlider(start, end, value);
        segmentCorruptionInverseSlider.setPaintLabels(true);
        segmentCorruptionInverseSlider.setPaintTicks(true);
        segmentCorruptionInverseSlider.setMajorTickSpacing(60);
        segmentCorruptionInverseSlider.setMinorTickSpacing(10);
        
        Dictionary<Integer, JLabel> labels = new Hashtable<>();
        for (int i = start; i <= end; i += 300) {
           String text = String.format("%4.2f", i / 1000.0);
           labels.put(i, new JLabel(text));
        }

        segmentCorruptionInverseSlider.setLabelTable(labels);

        segmentCorruptionInverseSlider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
              int value = segmentCorruptionInverseSlider.getValue();
              double sliderValue = value / 1000.0;
              segmentCorruptionInverseTextField.setText(String.valueOf(sliderValue));
           }
        });
        
        value = 200;
        JTextField casualNumberTextField = new JTextField(String.valueOf(0.2), 5);
        JSlider casualNumberSlider = new JSlider(start, end, value);
        casualNumberSlider.setPaintLabels(true);
        casualNumberSlider.setPaintTicks(true);
        casualNumberSlider.setMajorTickSpacing(60);
        casualNumberSlider.setMinorTickSpacing(10);
        
        Dictionary<Integer, JLabel> labels1 = new Hashtable<>();
        for (int i = start; i <= end; i += 300) {
           String text = String.format("%4.2f", i / 1000.0);
           labels1.put(i, new JLabel(text));
        }

        casualNumberSlider.setLabelTable(labels);

        casualNumberSlider.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
              int value = casualNumberSlider.getValue();
              double sliderValue = value / 1000.0;
              casualNumberTextField.setText(String.valueOf(sliderValue));
           }
        });
        
        JButton play = new JButton("Play");
        JButton stop = new JButton("Stop");
                
        
        panel.add(protocolLabel);
        panel.add(protocolComboBox);
        
        panel.add(userNoLabel);
        panel.add(userNoTextField);
        panel.add(queueLengthLabel);
        panel.add(queueLengthTextField);
        
        
        /*protocolComboBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        segmentCorruptionInverseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 130));
        casualNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 130));*/
        
                
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();     
        JPanel panel5 = new JPanel();        
        JPanel panel6 = new JPanel();   
        
        panel1.setLayout(new GridLayout(0, 1));
        panel3.setLayout(new GridLayout(0, 1));
        
        panel1.add(segmentCorruptionInverseLabel);
        panel2.add(segmentCorruptionInverseSlider);
        panel2.add(segmentCorruptionInverseTextField);
        panel3.add(casualNumberLabel);
        panel4.add(casualNumberSlider);
        panel4.add(casualNumberTextField);
        panel5.add(play);
        panel5.add(stop);
        
        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);
        panel.add(panel5);
        panel.add(panel6);
        
        
        panel.setBounds(10, 10, 10, 10);
        panel.setVisible(true);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
