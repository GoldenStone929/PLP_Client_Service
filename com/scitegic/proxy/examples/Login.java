package com.scitegic.proxy.examples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import com.scitegic.proxy.*;

public class Login extends JFrame {

    private JTextField serverAddressField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private PipelinePilotServer globalPPServer;

    public static boolean isUserValid = false;

    private final Object lock = new Object();

    public void closeLogin() {
        this.dispose();
    }
    public Login() {
        super("Login");

        serverAddressField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    globalPPServer = new PipelinePilotServer(serverAddress, username, password);
                    System.out.println("Server connection established!");

                    try {
                        PipelinePilotServerConfig conf = globalPPServer.getServerConfig();
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
                    Global.serverAddress = serverAddress;
                    Global.username = username;
                    Global.password = password;
                    synchronized (lock) {
                        lock.notify();
                    }
                } else {
                    System.out.println("Login failed!");
                }
            }
        });


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (lock) {
                    lock.notify();
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
        panel.add(loginButton);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void waitForLogin() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
