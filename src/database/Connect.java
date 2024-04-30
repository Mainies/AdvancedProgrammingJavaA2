package database;

import restaurant.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
	
    private Connection connection; // Made it a class member so it's accessible in listTables()
    private String dbURL = "jdbc:sqlite:C:\\Users\\Sam\\eclipse-workspace\\A2_s3939120\\src\\Restaurant.db";
    private int orderNumber;
    
    public void connect() {
        try {
            // db parameters - fixed path for stability
            // create a connection to the database
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }
    
    public int getOrderNumber() {
    	// method created for ease in testing
    	return this.orderNumber;
    }
    private void MaxValue() {
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            // Query to find the maximum value in the numbers column
            String query = "SELECT MAX(OrderNumber) AS max_number FROM Orders";
            
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                
                if (rs.next()) {
                    this.orderNumber = rs.getInt("max_number");
                } else {
                    System.out.println("No data found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        connector.closeConnection(); 
    }
    
    public boolean createUser(String userName, String password, String firstName, String lastName) {
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return false;
        }
        
        String query = "INSERT INTO Users (userName, password, firstName, lastName) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } 
        return true; 
    }
    
    public boolean deleteUser(String userName){
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return false;
        }
        
        String query = "DELETE FROM Users WHERE userName = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User removed successfully.");
            } else {
                System.out.println("No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } 
        return true; 
    }
    
    public boolean createVIPUser(String userName, String password, String firstName, String lastName, String email) {
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return false;
        }
        
        String query = "INSERT INTO Users (userName, password, firstName, lastName, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, email);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } 
        return false; 
    }
    
    public boolean isVIP(String userName) {
    	String query = "SELECT email FROM users WHERE userName = ?";
    	try(PreparedStatement pstmt = connection.prepareStatement(query)){
    		pstmt.setString(1, userName);
    		try(ResultSet results = pstmt.executeQuery()){
    			if (results.next()) {
    				String email = results.getString("email");
    				return email != null && !email.isEmpty();
    			}
    			else {
    				return false;
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public void newOrder(Order order, PointOfService pos, String userName) {
    	int burritos = order.getBurritos();
    	int fries = order.getFries();
    	int sodas = order.getSodas(); 
    	int meals = order.getMeals();
    	double price = pos.calculateSale(order);
    	this.MaxValue();
    	this.orderNumber++; 
    	String query = "INSERT INTO Orders(OrderNumber, Burritos, Fries, Sodas, Meals, Collected, Price) VALUES(?, ?, ?, ?, ?, ?, ?)";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, this.orderNumber);
            pstmt.setInt(2, burritos);
            pstmt.setInt(3, fries);
            pstmt.setInt(4, sodas);
            pstmt.setInt(5, meals);
            pstmt.setBoolean(6, false);
            pstmt.setDouble(7, price);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } 
    	addUserOrder(userName);
    }
    
    public void addUserOrder(String userName) {
    	String query = "INSERT INTO UserOrders(Username, OrderNumber) VALUES (?, ?)";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setInt(2, this.orderNumber);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void pickUpOrder(int orderNumber) {
    	String query = "UPDATE Orders SET Collected = ? WHERE OrderNumber = ?";
    	boolean pickup = true;
    	try (PreparedStatement pstmt = connection.prepareStatement(query)){
    		pstmt.setBoolean(1, pickup);
    		pstmt.setInt(2, orderNumber);
    		pstmt.executeUpdate();
    		    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void deleteOrders(int OrderNumber) {
		String removeTestOrder = "DELETE FROM Orders WHERE orderNumber = ?";
		String removeFromUserOrders = "DELETE FROM UserOrders WHERE orderNumber = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(removeFromUserOrders)){
				pstmt.setInt(1, OrderNumber);
				pstmt.executeUpdate();
			} catch (Exception e) {
		}
		try(PreparedStatement pstmt = connection.prepareStatement(removeTestOrder)){
				pstmt.setInt(1, OrderNumber);
				pstmt.executeUpdate();
			} catch (Exception e) {
		}
    }
    

	public boolean isUser(String user) {
		Connect connector = new Connect();
		connector.connect();
	    String query = "SELECT 1 FROM Users WHERE UserName = ?"; 
	    try {
	        connect(); 
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, user);
	            ResultSet results = stmt.executeQuery();
	            return results.next(); 
	        } 
	    } catch (SQLException e) {
	        System.err.println("Database error: " + e.getMessage());
	        return false;          	
	    } 
	} 

	public boolean checkPassword(String user, String inputPassword) {
	    String query = "SELECT Password FROM Users WHERE UserName = ?";
	    try {
	        connect(); 
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, user);
	            ResultSet results = stmt.executeQuery();
	            if (results.next()) {
	                String storedPassword = results.getString("Password");
	                return storedPassword.equals(inputPassword); 
	            }
	            return false; 
	        } 
	    } catch (SQLException e) {
	        System.err.println("Database error: " + e.getMessage());
	        return false;
	    }
	}

	public User getUserFromDatabase(String username) {
		User user = null;
        String query = "SELECT UserName, Password, FirstName, LastName, Email FROM Users WHERE UserName = ?";
        try {
            connect(); 
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    String user1 = results.getString("UserName");
                    String pass = results.getString("Password");
                    String first = results.getString("FirstName");
                    String last = results.getString("LastName");
                    String email = results.getString("Email");
                    if (email != null) {
                        return new User(user1, pass, first, last, email); 
                    } else {
                        return new User(user1, pass, first, last); 
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return user;
    }
}