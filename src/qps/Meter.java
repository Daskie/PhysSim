package qps;

import qps.sensor.SensorScene;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 * @since 6/3/2016
 */
public class Meter {

    JFrame frame;
    JPanel panel;

    JTextField eField;
    JTextField vField;
    JTextField xField;
    JTextField yField;
    JTextField zField;

    JLabel eLabel;
    JLabel vLabel;
    JLabel xLabel;
    JLabel yLabel;
    JLabel zLabel;

    public Meter() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
        frame.add(panel);

        eField = new JTextField(8);
        vField = new JTextField(8);
        xField = new JTextField(8);
        yField = new JTextField(8);
        zField = new JTextField(8);

        eLabel = new JLabel("E:");
        vLabel = new JLabel("V:");
        xLabel = new JLabel("x:");
        yLabel = new JLabel("y:");
        zLabel = new JLabel("z:");

        JPanel p1 = new JPanel();
        panel.add(p1, BorderLayout.PAGE_START);

        JPanel p2 = new JPanel();
        panel.add(p2, BorderLayout.PAGE_END);

        p1.add(eLabel);
        p1.add(eField);
        p1.add(vLabel);
        p1.add(vField);

        p2.add(xLabel);
        p2.add(xField);
        p2.add(yLabel);
        p2.add(yField);
        p2.add(zLabel);
        p2.add(zField);

        xField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vec3 loc = SensorScene.getSensorLoc();
                try {
                    float x = Float.parseFloat(xField.getText());
                    SensorScene.move(new Vec3(x - loc.x, 0.0f, 0.0f));
                } catch (NumberFormatException ex) {
                    xField.setText("" + loc.x);
                }
            }
        });

        yField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vec3 loc = SensorScene.getSensorLoc();
                try {
                    float y = Float.parseFloat(yField.getText());
                    SensorScene.move(new Vec3(0.0f, y - loc.y, 0.0f));
                } catch (NumberFormatException ex) {
                    yField.setText("" + loc.y);
                }
            }
        });

        zField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vec3 loc = SensorScene.getSensorLoc();
                try {
                    float z = Float.parseFloat(zField.getText());
                    SensorScene.move(new Vec3(0.0f, 0.0f, z - loc.z));
                } catch (NumberFormatException ex) {
                    zField.setText("" + loc.z);
                }
            }
        });

        frame.pack();
        frame.setLocation(128, 128);
        frame.setVisible(true);
    }

    public void setE(float e) {
        eField.setText("" + e);
    }

    public void setV(float v) {
        vField.setText("" + v);
    }

    public void cleanup() {
        frame.dispose();
    }

}
