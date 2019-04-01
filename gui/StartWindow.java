package pl.buarzi.gui;

import pl.buarzi.databaseConnection.DatabaseConnection;
import pl.buarzi.databaseConnection.DatabaseUrlConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartWindow extends JFrame {
    private JFrame mainFrame;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 200;
    private static final int buttonWidth = 100;
    private static final int buttonHeight = 30;
    private static final String dataBaseUrl = "jdbc:mysql://" + new DatabaseUrlConfig().getDatabaseAdress() + "?useSSL=false";

    private void logWindow(DatabaseConnection.User user) {
        JTextField userName = new JTextField(10);
        JPasswordField password = new JPasswordField(10);

        JPanel logPanel = new JPanel();
        logPanel.add(new JLabel("Username: "));
        logPanel.add(userName);
        logPanel.add(Box.createVerticalBox());
        logPanel.add(new JLabel("Password: "));
        logPanel.add(password);

        int result = JOptionPane.showConfirmDialog(mainFrame, logPanel, "Enter username and password", JOptionPane.OK_CANCEL_OPTION);
        if (result == 0) {
            String inputUsername = userName.getText();
            String inputPassword = new String(password.getPassword());
            if (DatabaseConnection.connectToDatabase(dataBaseUrl, inputUsername, inputPassword, user)) {
                switch (user) {
                    case ADMIN:
                        AdminWindow.main();
                        mainFrame.dispose();
                        System.out.println("Connected to database: admin");
                        break;
                    case USER:
                        UserWindow.main();
                        mainFrame.dispose();
                        System.out.println("Connected to database: user");
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Cannot connect to database", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private JButton setAdminButton() {
        JButton adminButton = new JButton("Admin");
        adminButton.setSize(buttonWidth, buttonHeight);
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logWindow(DatabaseConnection.User.ADMIN);
            }
        });
        return adminButton;
    }

    private JButton setUserButton() {
        JButton userButton = new JButton("User");
        userButton.setBounds(20, 80, buttonWidth, buttonHeight);
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logWindow(DatabaseConnection.User.USER);
            }
        });
        return userButton;
    }

    public StartWindow() {
        //Create main frame
        mainFrame = new JFrame();
        mainFrame.setBounds(800, 200, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setLayout(new GridLayout(2, 1));

        //Create title label
        JLabel titleLabel = new JLabel("Library Managment System", JLabel.CENTER);
        mainFrame.add(titleLabel);

        //Create buttonPanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(setAdminButton());
        buttonPanel.add(setUserButton());
        mainFrame.add(buttonPanel);

        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setTitle("Simple Library Managment System");
        mainFrame.setVisible(true);
    }
}
