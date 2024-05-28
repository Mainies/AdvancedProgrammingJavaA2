package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class DBConnect {
	/* Major class to implement connection via the JDBC
	allows various other objects to communicate and interact with the sql lite database
	methods to update values in tables, get values from tables and execute queries from other objects
	*/
	
    protected Connection connection; // Made it a class member so it's accessible in listTables()
    protected String dbURL = "jdbc:sqlite:src/Restaurant.db";
                
    protected void connect() {
        try {
            // db parameters - fixed path for stability
            // create a connection to the database
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }
     
}

