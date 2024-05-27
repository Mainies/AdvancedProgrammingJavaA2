package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectTestMethods extends DBConnect{
	 public Connection make_connect() {
	        try {
	            // db parameters - fixed path for stability
	            // create a connection to the database
	            connection = DriverManager.getConnection(dbURL);
	            System.out.println("Connection to SQLite has been established.");
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        } 
	        return connection;
	    }
	    public void closeConnection() {
	    	//closes connection for stability
	        if (connection != null) {
	            try {
	                connection.close();
	                System.out.println("Connection closed.");
	            } catch (SQLException e) {
	                System.out.println(e.getMessage());
	            }
	        }
	    }      
}
