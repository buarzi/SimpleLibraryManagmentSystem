package pl.buarzi.gui;

import pl.buarzi.Start;
import pl.buarzi.databaseConnection.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminWindow extends JFrame {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 550;
    private static final int buttonWidth = 120;
    private static final int buttonHeight = 30;
    private static JPanel rightButtonPanel;
    private static JScrollPane topPanelPane;
    private static JScrollPane displayPanelPane;
    private static JFrame mainFrame;
    private static JTable displayTable;

    private static void cleanTableAndShowNewData(String sql) {
        DefaultTableModel model1 = (DefaultTableModel) displayTable.getModel();
        model1.setColumnCount(0);
        displayTable.setModel(DatabaseConnection.getData(sql));
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
                            cleanTableAndShowNewData("SELECT * FROM books ORDER BY author ASC");
                            break;
                        case "Title":
                            cleanTableAndShowNewData("SELECT * FROM books ORDER BY title ASC");
                            break;
                        case "Year":
                            cleanTableAndShowNewData("SELECT * FROM books ORDER BY year ASC");
                            break;
                        case "Status":
                            cleanTableAndShowNewData("SELECT * FROM books ORDER BY status ASC");
                            break;
                        case "":
                            cleanTableAndShowNewData("SELECT * FROM books");
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
        JButton addBookButton = new JButton("Add book");
        JButton removeBookButton = new JButton("Remove book");
        JButton editBookButton = new JButton("Edit book");
        JButton logoutButton = new JButton("Log out");
        JButton exitButton = new JButton("Exit");

        //Set maximum size
        addBookButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField id = new JTextField(2);
                JTextField author = new JTextField(10);
                JTextField title = new JTextField(10);
                JTextField year = new JTextField(5);
                JComboBox<String> status = new JComboBox<>(new String[]{"free", "checked"});

                JPanel addBookPanel = new JPanel();
                addBookPanel.add(new JLabel("Id"));
                addBookPanel.add(id);
                addBookPanel.add(new JLabel("Author"));
                addBookPanel.add(author);
                addBookPanel.add(new JLabel("Title"));
                addBookPanel.add(title);
                addBookPanel.add(new JLabel("Year"));
                addBookPanel.add(year);
                addBookPanel.add(new JLabel("Status"));
                addBookPanel.add(status);

                int result = JOptionPane.showConfirmDialog(mainFrame, addBookPanel, "Enter new book", JOptionPane.OK_CANCEL_OPTION);
                if (result == 0) {
                    String inputId = id.getText();
                    String inputAuthor = author.getText();
                    String inputTitle = title.getText();
                    String inputYear = year.getText();
                    String inputStatus = (String) status.getSelectedItem();

                    if (DatabaseConnection.addToDatabase(inputId, inputAuthor, inputTitle, inputYear, inputStatus)) {
                        cleanTableAndShowNewData("SELECT * FROM books");
                        JOptionPane.showMessageDialog(mainFrame, "The book has been inserted into database");
                    } else {
                        JOptionPane.showInputDialog(mainFrame, "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        removeBookButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        removeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = displayTable.getSelectedRow();
                if (selected == -1) {
                    JOptionPane.showMessageDialog(mainFrame, "No entry has been selected.", "Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    int option = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want delete this entry?.", "Question", JOptionPane.OK_CANCEL_OPTION);
                    int idFromDatabase = (int) displayTable.getValueAt(selected, 0);
                    if (option == JOptionPane.OK_OPTION) {
                        if (DatabaseConnection.removeFromDatabase(idFromDatabase)) {
                            cleanTableAndShowNewData("SELECT * FROM books");
                            JOptionPane.showMessageDialog(mainFrame, "Done.");
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Failed.", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
        editBookButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = displayTable.getSelectedRow();
                if (selected == -1) {
                    System.out.println(selected);
                    JOptionPane.showMessageDialog(mainFrame, "No entry has been selected.", "Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    JPanel editPanel = new JPanel();

                    //Get data from database
                    int idFromDatabase = (int) displayTable.getValueAt(selected, 0);

                    String[] data = DatabaseConnection.getAnElement(idFromDatabase);
                    System.out.println(selected);

                    JTextField newAuthor = new JTextField(15);
                    newAuthor.setText(data[1]);
                    JTextField newTitle = new JTextField(15);
                    newTitle.setText(data[2]);
                    JTextField newId = new JTextField(2);
                    newId.setText(data[0]);
                    JTextField newYear = new JTextField(4);
                    newYear.setText(data[3]);
                    JComboBox<String> newStatus = new JComboBox<>(new String[]{"free", "checked"});
                    newStatus.setSelectedItem((String) data[4]);

                    editPanel.add(new JLabel("Id"));
                    editPanel.add(newId);
                    editPanel.add(new JLabel("Author"));
                    editPanel.add(newAuthor);
                    editPanel.add(new JLabel("Title"));
                    editPanel.add(newTitle);
                    editPanel.add(new JLabel("Year"));
                    editPanel.add(newYear);
                    editPanel.add(new JLabel("Status"));
                    editPanel.add(newStatus);

                    int result = JOptionPane.showConfirmDialog(mainFrame, editPanel, "Edit entry", JOptionPane.OK_CANCEL_OPTION);

                    if (result == 0) {
                        String newIdText = newId.getText();
                        String newAuthorText = newAuthor.getText();
                        String newTitleText = newTitle.getText();
                        String newYearText = newYear.getText();
                        String newStatusText = (String) newStatus.getSelectedItem();

                        if (DatabaseConnection.updateEntry(idFromDatabase, newIdText, newAuthorText, newTitleText, newYearText, newStatusText)) {
                            JOptionPane.showMessageDialog(mainFrame, "Done.");
                            cleanTableAndShowNewData("SELECT * FROM books");
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Failed.", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

            }
        });

        //Log out button
        logoutButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConnection.disconnectDatabase();
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
        rightButtonPanel.add(addBookButton);
        rightButtonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightButtonPanel.add(removeBookButton);
        rightButtonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightButtonPanel.add(editBookButton);
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
                cleanTableAndShowNewData(sql);
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
        displayTable = new JTable(DatabaseConnection.getData("SELECT * FROM books"));
        displayTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane displayTablePane = new JScrollPane(displayTable);
        displayTablePane.setViewportView(displayTable);
        displayTablePane.setPreferredSize(new Dimension(490, 400));
        displayTablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Add table to display panel
        displayPanel.add(displayTablePane);
    }

    private AdminWindow() {
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

        //Show main window
        mainFrame.setVisible(true);
    }

    public static void main() {
        new AdminWindow();
    }
}
