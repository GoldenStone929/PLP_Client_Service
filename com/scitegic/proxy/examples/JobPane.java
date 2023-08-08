package com.scitegic.proxy.examples;

import javax.swing.*;

public class JobPane {

    private JFrame frame;

    public JobPane(String fileName) {
        frame = new JFrame("Job Pane for " + fileName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // You might replace this dummy data with actual data fetched from the server based on the file name.
        frame.add(new JLabel("Name: " + fileName));
        frame.add(new JLabel("Details: Example Details"));
        frame.add(new JLabel("Source: Example Source"));
        frame.add(new JLabel("X Property: Example X"));
        frame.add(new JLabel("Y Property: Example Y"));
        frame.add(new JLabel("File Type: Example File Type"));
        frame.add(new JLabel("Tooltip: Example Tooltip"));

        frame.pack();
        frame.setVisible(true);
    }
}
