package com.scitegic.proxy.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.scitegic.proxy.*;

public class TestGUI_v0 {

    static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/BHT/PLP_for_DataWarrior/";
    static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "for_dev_test/01_Hello_World";
    static final String WHITESPACE = "                                                     ";

    private PipelinePilotServer pp = null;
    private PipelinePilotServerConfig conf = null;
    private Job protocol = null;
    private ComponentDatabase compdb = null;
    private XmldbItem rootFolder = null;

    private String selectedProtocolName = null;
    private XmldbItem selectedXmldbItem = null;
    private Job selectedJob = null;
    private ComponentInfo selectedCmptInfo = null;

    private JFrame frame;
    private JPanel panelLeft, panelLeftTop, panelLeftMiddle, panelLeftBottom;
    private JPanel panelRight, panelRightTop, panelRightMiddle, panelRightBottom;

    private JButton buttonLeftTop, buttonLeftBottom, buttonRightTop, buttonRightBottom;
    private JLabel labelLeft;
    private JLabel labelRight = new JLabel("Show which tree node is clicked by user.");

    private int width = 50;
    private int height = 200;
    private int shift = 5;

    private int countLeft, countRight;

    private JTree myTree;
    private DefaultMutableTreeNode rootTreeNode;

    private myActionListener myListner = new myActionListener();

    private JDialog loginDialog;
    private JTextField serverField;
    private JTextField userField;
    private JPasswordField passwordField;

    public TestGUI_v0() {
        // Display the login dialog when the application starts
        displayLoginDialog();
    }

    private void displayLoginDialog() {
        frame = new JFrame();
        loginDialog = new JDialog(frame, "Login", true);
        loginDialog.setLayout(new GridLayout(4, 2));

        JLabel serverLabel = new JLabel("Server Address:");
        serverField = new JTextField();

        JLabel userLabel = new JLabel("Username:");
        userField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new Login());

        loginDialog.add(serverLabel);
        loginDialog.add(serverField);
        loginDialog.add(userLabel);
        loginDialog.add(userField);
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordField);
        loginDialog.add(new JLabel());  // Empty label for spacing
        loginDialog.add(loginButton);

        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);  // Center on screen
        loginDialog.setVisible(true);
    }

    private class Login implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pp = new PipelinePilotServer(serverField.getText(), userField.getText(), new String(passwordField.getPassword()));
                loginDialog.dispose();
                JOptionPane.showMessageDialog(frame, "Connected successfully!");

                // Continue with your logic to show the tree structure and other UI components
                createTopGUI();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to connect. Please check your credentials and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // [All your other methods from the original TestGUI_ORI, excluding the main method]

    public static void main(String[] args) {
        new TestGUI_ORI();
    }

    private class myActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (e.getSource().equals(buttonLeftTop)) {
                System.out.println("buttonLeftTop was clicked");
                buttonLeftTop.setText("You click me to refresh the JTree.");
                refreshPLPTree();
                return;
            }

            if (e.getSource().equals(buttonRightBottom)) {
                submitJob();
                return;
            }
            return;
        }
    }
}
