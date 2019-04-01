package pl.buarzi.databaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DatabaseConnection {
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public enum BookLibrary {
        CHECK, RETURN;
    }

    public enum User {
        ADMIN, USER;
    }

    public static boolean addToDatabase(String id, String author, String title, String year, String status) {
        String sql = "INSERT INTO books (id, author, title, year, status) VALUES (?,?,?,?,?);";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.valueOf(id));
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, title);
            preparedStatement.setInt(4, Integer.valueOf(year));
            preparedStatement.setString(5, status);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeFromDatabase(int id) {
        String sql = "DELETE FROM books WHERE id=?;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, (id));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String[] getAnElement(int id) {
        String sql = "SELECT * FROM books WHERE id=?;";
        String[] output = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, (id));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String oldId = resultSet.getString("Id");
                String oldAuthor = resultSet.getString("Author");
                String oldTitle = resultSet.getString("Title");
                String oldYear = resultSet.getString("Year");
                String oldStatus = resultSet.getString("Status");
                output = new String[]{oldId, oldAuthor, oldTitle, oldYear, oldStatus};
            }
        } catch (SQLException e) {
            System.out.println("Cannot get the data.");
        }
        return output;
    }

    public static boolean updateEntry(int idFromDatabase, String newIdText, String newAuthorText, String newTitleText, String newYearText, String newStatusText) {
        String sql = "UPDATE books SET Id=?, Author=?, Title=?, Year=?, Status=? WHERE Id=?;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.valueOf(newIdText));
            preparedStatement.setString(2, newAuthorText);
            preparedStatement.setString(3, newTitleText);
            preparedStatement.setString(4, newYearText);
            preparedStatement.setString(5, newStatusText);
            preparedStatement.setInt(6, idFromDatabase);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Cannot update.");
            return false;
        }
        return true;
    }

    public static void disconnectDatabase() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Cannot close the connection");
        }
    }

    public static boolean connectToDatabase(String url, String username, String password, User user) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //TODO: validation
            switch (user) {
                case ADMIN:
                    if (!username.equals("admin") || !password.equals("admin")) {
                        System.out.println("Incorrect data for admin.");
                        return false;
                    } else {
                        connection = DriverManager.getConnection(url, username, password);
                    }
                    break;
                case USER:
                    if (!username.equals("user") || !password.equals("user")) {
                        System.out.println("Incorrect data for user.");
                        return false;
                    } else {
                        connection = DriverManager.getConnection(url, username, password);
                    }
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Cannot connect to database.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static DefaultTableModel getData(String sql) {
        Vector<String> columnNames = null;
        Vector<Vector<Object>> data = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            // data of the table
            data = new Vector<Vector<Object>>();
            while (resultSet.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(resultSet.getObject(columnIndex));
                }
                data.add(vector);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DefaultTableModel model1 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        model1.setDataVector(data, columnNames);
        return model1;
    }

    public static boolean checkOrReturnBookFromLibrary(int id, BookLibrary operation) {
        String sql = null;
        switch (operation) {
            case CHECK:
                sql = "UPDATE books SET status=\'checked\' WHERE id = ?";
                break;
            case RETURN:
                sql = "UPDATE books SET status=\'free\' WHERE id = ?";
                break;
        }
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Cannot check the book.");
            return false;
        }
        return true;
    }
}
