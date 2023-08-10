package com.scitegic.proxy.examples;

import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import com.scitegic.proxy.*;

public class Login extends JFrame {

    private JTextField serverAddressField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JButton loginButton;
    private PipelinePilotServer globalPPServer;

    public Login() {
        super("Login");

        serverAddressField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

//        private JTextField nameField = new JTextField(20);
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean isUserValid = false;


                try {
                    globalPPServer = new PipelinePilotServer(serverAddress, username, password);
                    // If the connection is successful, you can proceed.
                    // Note: You may need to add additional logic to truly verify the connection.
                    System.out.println("Server connection established!");

                    try {
                        PipelinePilotServerConfig conf = globalPPServer.getServerConfig();
                        // If this call is successful, the credentials are valid.
                        isUserValid = true;
                        System.out.println("Login successful!");
                    } catch (Exception ex) {
                        isUserValid = false;
                        System.out.println("Login failed: " + ex.getMessage());
                    }

                } catch (Exception ex) {
                    System.out.println("Error establishing server connection: " + ex.getMessage());
                }



                if (isUserValid) {
                    System.out.println("Login successful!");

                    // Store the user information in a global variable
                    Global.serverAddress = serverAddress;
                    Global.username = username;
                    Global.password = password;

                    // Run following code here
                } else {
                    System.out.println("Login failed!");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Server address:"));
        panel.add(serverAddressField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
//        panel.add(new JLabel("Name:"));
//        panel.add(nameField);
        panel.add(loginButton);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }

    static class Global {
        public static String serverAddress;
        public static String username;
        public static String password;
    }
}
