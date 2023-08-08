package com.scitegic.proxy.examples;

import javax.swing.*;
import java.awt.*;

public class JobPane {

    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> showJobPane("sampleFile"));
    }

    public static void showJobPane(String filename) {
        // Logic to fetch the job info from the server based on the filename
        // And then display the job pane using that info
        JobInfo jobInfo = fetchJobInfoFromServer(filename);
        createAndShowGUI(jobInfo);
    }

    private static void createAndShowGUI(JobInfo jobInfo) {
        JFrame frame = new JFrame("XY Scatter Plot Utility");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Display name and details
        JLabel nameLabel = new JLabel(jobInfo.getName());
        frame.add(nameLabel);

        JLabel detailsLabel = new JLabel(jobInfo.getDetails());
        frame.add(detailsLabel);

        // Source field
        JLabel sourceLabel = new JLabel("Source:");
        frame.add(sourceLabel);
        JTextField sourceField = new JTextField(jobInfo.getSource());
        frame.add(sourceField);

        // X Property field
        JLabel xPropertyLabel = new JLabel("X Property:");
        frame.add(xPropertyLabel);
        JTextField xPropertyField = new JTextField(jobInfo.getXProperty());
        frame.add(xPropertyField);

        // Y Property field
        JLabel yPropertyLabel = new JLabel("Y Property:");
        frame.add(yPropertyLabel);
        JTextField yPropertyField = new JTextField(jobInfo.getYProperty());
        frame.add(yPropertyField);

        // File Type dropdown
        JLabel fileTypeLabel = new JLabel("File Type:");
        frame.add(fileTypeLabel);
        JComboBox<String> fileTypeSelector = new JComboBox<>(new String[]{jobInfo.getFileType()});
        frame.add(fileTypeSelector);

        // Tooltip field
        JLabel tooltipLabel = new JLabel("Tooltip:");
        frame.add(tooltipLabel);
        JTextField tooltipField = new JTextField(jobInfo.getTooltip());
        frame.add(tooltipField);

        // Submit button
        JButton submitButton = new JButton("Submit");
        frame.add(submitButton);

        frame.pack();
        frame.setVisible(true);
    }

    // Mock method to simulate fetching job info from the server based on the filename
    private static JobInfo fetchJobInfoFromServer(String filename) {
        // For demonstration purposes, this method returns a hardcoded JobInfo object
        // In a real-world scenario, you would replace this method to actually fetch the info from the server

        JobInfo jobInfo = new JobInfo();
        jobInfo.setName("Sample Job for " + filename);
        jobInfo.setDetails("Details for job corresponding to " + filename);
        jobInfo.setSource("./data/" + filename + ".txt");
        jobInfo.setXProperty("XProp_" + filename);
        jobInfo.setYProperty("YProp_" + filename);
        jobInfo.setFileType("PDF");
        jobInfo.setTooltip("Tooltip for " + filename);

        return jobInfo;
    }
}

class JobInfo {
    private String name;
    private String details;
    private String source;
    private String xProperty;
    private String yProperty;
    private String fileType;
    private String tooltip;

    // Getters
    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getSource() {
        return source;
    }

    public String getXProperty() {
        return xProperty;
    }

    public String getYProperty() {
        return yProperty;
    }

    public String getFileType() {
        return fileType;
    }

    public String getTooltip() {
        return tooltip;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setXProperty(String xProperty) {
        this.xProperty = xProperty;
    }

    public void setYProperty(String yProperty) {
        this.yProperty = yProperty;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public String toString() {
        return "JobInfo{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", source='" + source + '\'' +
                ", xProperty='" + xProperty + '\'' +
                ", yProperty='" + yProperty + '\'' +
                ", fileType='" + fileType + '\'' +
                ", tooltip='" + tooltip + '\'' +
                '}';
    }
}
