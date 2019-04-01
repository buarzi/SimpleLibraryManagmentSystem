package pl.buarzi.gui;

import pl.buarzi.databaseConnection.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserWindow extends JFrame {
    private static final int WINDOW_WIDTH = 980;
    private static final int WINDOW_HEIGHT = 550;
    private static final int buttonWidth = 120;
    private static final int buttonHeight = 30;
    private static JPanel rightButtonPanel;
    private static JScrollPane topPanelPane;
    private static JScrollPane displayPanelPane;
    private static JScrollPane userDisplayPanelPane;
    private static JFrame mainFrame;
    private static JTable displayTable;
    private static JTable userDisplayTable;

    private enum Table {
        USER, ADMIN
    }

    private static void cleanTableAndShowNewData(String sql, Table which) {
        DefaultTableModel model1 = null;
        switch (which) {
            case USER:
                model1 = (DefaultTableModel) displayTable.getModel();
                model1.setColumnCount(0);
                displayTable.setModel(DatabaseConnection.getData(sql));
                break;
            case ADMIN:
                model1 = (DefaultTableModel) userDisplayTable.getModel();
                model1.setColumnCount(0);
                userDisplayTable.setModel(DatabaseConnection.getData(sql));
        }
    }

    private static JComboBox createComboBox() {
        String[] sortByOptions = {"", "Author", "Title", "Year", "Status"};
        final JComboBox<String> sortByComboBox = new JComboBox<>(sortByOptions);
        sortByComboBox.setSelectedIndex(0);
        sortByComboBox.setPreferredSize(new Dimension(100, 25));
        sortByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) sortByComboBox.getSelectedItem();
                if (selectedItem != null) {
                    switch (selectedItem) {
                        case "Author":
                            cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\' ORDER BY author ASC", Table.ADMIN);
                            break;
                        case "Title":
                            cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\' ORDER BY title ASC", Table.ADMIN);
                            break;
                        case "Year":
                            cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\' ORDER BY year ASC", Table.ADMIN);
                            break;
                        case "Status":
                            cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\' ORDER BY status ASC", Table.ADMIN);
                            break;
                        case "":
                            cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\'", Table.ADMIN);
                            break;
                    }
                }
            }
        });
        return sortByComboBox;
    }

    private static void createRightPanel() {
        //Create a panel for buttons (right)
        rightButtonPanel = new JPanel();
        rightButtonPanel.setLayout(new BoxLayout(rightButtonPanel, BoxLayout.Y_AXIS));
        rightButtonPanel.setPreferredSize(new Dimension(140, 100));

        //Create buttons
        JButton checkBookButton = new JButton("Check book");
        checkBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = userDisplayTable.getSelectedRow();
                int idFromDatabase = (int) userDisplayTable.getValueAt(selected, 0);

                if (selected != -1) {
                    if (DatabaseConnection.checkOrReturnBookFromLibrary(idFromDatabase, DatabaseConnection.BookLibrary.CHECK)) {
                        cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'checked\'", Table.USER);
                        cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\'", Table.ADMIN);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Cannot return the book", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }

            }
        });
        JButton returnBookButton = new JButton("Return book");
        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = displayTable.getSelectedRow();
                int idFromDatabase = (int) displayTable.getValueAt(selected, 0);

                if (selected != -1) {
                    if (DatabaseConnection.checkOrReturnBookFromLibrary(idFromDatabase, DatabaseConnection.BookLibrary.RETURN)) {
                        cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'checked\'", Table.USER);
                        cleanTableAndShowNewData("SELECT * FROM books WHERE status = \'free\'", Table.ADMIN);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Cannot return the book", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        JButton logoutButton = new JButton("Log out");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConnection.disconnectDatabase();
                mainFrame.dispose();
                new StartWindow();
            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //Set maximum size
        checkBookButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));

        returnBookButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));

        //Log out button
        logoutButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                new StartWindow();
            }
        });

        //Exit button
        exitButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //Add buttons to panel and create blank vertical space between them
        rightButtonPanel.add(checkBookButton);
        rightButtonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightButtonPanel.add(returnBookButton);
        rightButtonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightButtonPanel.add(logoutButton);
        rightButtonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightButtonPanel.add(exitButton);
    }

    private static void createTopPanel() {
        //Create panel for search (top)
        JPanel topPanel = new JPanel();
        topPanelPane = new JScrollPane(topPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //Add components for searching (author, title, button)
        JLabel authorLabel = new JLabel("Author: ");
        final JTextField authorField = new JTextField();
        authorField.setPreferredSize(new Dimension(150, 25));

        JLabel titleLabel = new JLabel("Title: ");
        final JTextField titleField = new JTextField();
        titleField.setPreferredSize(new Dimension(150, 25));

        JButton searchButton = new JButton("Search");
        searchButton.setSize(buttonWidth, buttonHeight);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputAuthor = authorField.getText();
                String inputTitle = titleField.getText();
                String sql = "SELECT * FROM books WHERE author LIKE\'%" + inputAuthor + "%\' AND title LIKE\'%" + inputTitle + "%\';";
                cleanTableAndShowNewData(sql, Table.ADMIN);
            }
        });

        //Create comboBox for searching
        JLabel sortByLabel = new JLabel(" | Sort by: ");
        JComboBox sortByComboBox = createComboBox();

        //Add all components to panel
        topPanel.add(authorLabel);
        topPanel.add(authorField);
        topPanel.add(titleLabel);
        topPanel.add(titleField);
        topPanel.add(searchButton);
        topPanel.add(sortByLabel);
        topPanel.add(sortByComboBox);
    }

    private static void createDisplayPanel() {
        //Create panel for display (center/middle)
        JPanel displayPanel = new JPanel();
        displayPanelPane = new JScrollPane(displayPanel);
        displayPanelPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //Create a table for books
        displayTable = new JTable(DatabaseConnection.getData("SELECT * FROM books WHERE status = \'checked\'"));
        displayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane displayTablePane = new JScrollPane(displayTable);
        //displayTablePane.add(new JTable());
        displayTablePane.setViewportView(displayTable);
        displayTablePane.setPreferredSize(new Dimension(380, 400));
        displayTablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Add table to display panel
        displayPanel.add(displayTablePane);
    }

    private static void createUserDisplayPanel() {
        //Create panel for display (center/middle)
        JPanel displayPanel = new JPanel();
        userDisplayPanelPane = new JScrollPane(displayPanel);
        userDisplayPanelPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //Create a table for books
        userDisplayTable = new JTable(DatabaseConnection.getData("SELECT * FROM books WHERE status = \'free\'"));
        userDisplayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane displayTablePane = new JScrollPane(userDisplayTable);
        displayTablePane.setViewportView(userDisplayTable);
        displayTablePane.setPreferredSize(new Dimension(380, 400));
        displayTablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Add table to display panel
        displayPanel.add(displayTablePane);
    }


    private UserWindow() {
        //Create main window
        mainFrame = new JFrame();
        mainFrame.setTitle("Welcome to library!");
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(20, 20));
        mainFrame.setBounds(600, 200, WINDOW_WIDTH, WINDOW_HEIGHT);

        //Add panel to main window - position: east (right)
        createRightPanel();
        mainFrame.add(rightButtonPanel, BorderLayout.EAST);

        //Add panel to main window - position: north (top)
        createTopPanel();
        mainFrame.add(topPanelPane, BorderLayout.NORTH);

        //Add pane to main window - position: center
        createDisplayPanel();
        mainFrame.add(displayPanelPane, BorderLayout.CENTER);

        //Add panel to main window - position: west (left)
        createUserDisplayPanel();
        mainFrame.add(userDisplayPanelPane, BorderLayout.WEST);

        //Show main window
        mainFrame.setVisible(true);
    }

    public static void main() {
        new UserWindow();
    }
}
