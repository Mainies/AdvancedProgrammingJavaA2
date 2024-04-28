package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {

    private Connection connection; // Made it a class member so it's accessible in listTables()

    public void connect() { // Changed to non-static to modify the instance variable 'connection'
        try {
            // db parameters - fixed path
            String url = "jdbc:sqlite:C:\\Users\\Sam\\eclipse-workspace\\A2_s3939120\\src\\Restaurant.db";
            // create a connection to the database
            connection = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }

    public void listTables() {
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return;
        }
        String query = "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%';";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            System.out.println("List of tables:");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Connect connector = new Connect();
        connector.connect();
        connector.listTables();
        connector.closeConnection(); // Close connection after use
    }
}