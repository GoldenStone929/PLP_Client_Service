package com.scitegic.proxy.examples;
import javax.swing.ImageIcon;
import java.awt.*;
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
    private JTextArea infoTextArea;
    private JButton loginButton;
    private PipelinePilotServer globalPPServer;
    private ImageIcon BHTIcon;
    private JFrame frame;
    private java.awt.Image image;
    public static boolean isUserValid = false;
    private final Object lock = new Object();

    public void closeLogin() {
        this.dispose();
    }

    public Login() {

        super("Blossomhill therapeutics Login");

        this.setLocationRelativeTo(null); // center the window




        BHTIcon = new ImageIcon(".\\data\\logo-bht.png");
        image = BHTIcon.getImage();
        this.setIconImage(image);
//        frame.setIconImage(image);

//        this.setSize(new Dimension(450, 220));
        this.setMinimumSize(new Dimension(420, 220));

        // Setting background image using JLabel
//        JLabel backgroundLabel = new JLabel(BHTIcon);
//        setLayout(new BorderLayout());
//        add(backgroundLabel);
//        backgroundLabel.setLayout(new FlowLayout());



        serverAddressField = new JTextField(20);
        //set server Address default to "http://10.30.50.184:9944/"
        serverAddressField.setText("http://10.30.50.184:9944/");


        usernameField = new JTextField(20);
        //set username default to "?"
        usernameField.setText("ShawnCui");

        passwordField = new JPasswordField(20);
        infoTextArea = new JTextArea(5, 20);
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    globalPPServer = new PipelinePilotServer(serverAddress, username, password);
                    infoTextArea.append("Server connection established!\n");

                    try {
                        PipelinePilotServerConfig conf = globalPPServer.getServerConfig();
                        isUserValid = true;
                        infoTextArea.append("Login successful!\n");
                    } catch (Exception ex) {
                        isUserValid = false;
                        infoTextArea.append("Login failed: " + ex.getMessage() + "\n");
                    }

                } catch (Exception ex) {
                    infoTextArea.append("Error establishing server connection: " + ex.getMessage() + "\n");
                }

                if (isUserValid) {
                    Global.serverAddress = serverAddress;
                    Global.username = username;
                    Global.password = password;
                    synchronized (lock) {
                        lock.notify();
                    }
                } else {
                    infoTextArea.append("Login failed!\n");
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

//        frame.setIconImage(image);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverPanel.add(new JLabel("Server address:"));
        serverPanel.add(serverAddressField);

        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordField);

        mainPanel.add(serverPanel);
        mainPanel.add(usernamePanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(loginButton);
        mainPanel.add(new JScrollPane(infoTextArea));



        add(mainPanel);
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
