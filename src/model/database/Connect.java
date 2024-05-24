package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.restaurant.*;

public class Connect {
	/* Major class to implement connection via the JDBC
	allows various other objects to communicate and interact with the sql lite database
	methods to update values in tables, get values from tables and execute queries from other objects
	*/
	
    private Connection connection; // Made it a class member so it's accessible in listTables()
    private String dbURL = "jdbc:sqlite:src/Restaurant.db";
    private int orderNumber;
    
    public static void main(String[] args) {
    	// main method used for debugging and testing
        Connect connector = new Connect();
        connector.connect();
        //connector.listTables();
        connector.closeConnection(); 
    }
        
    private void connect() {
        try {
            // db parameters - fixed path for stability
            // create a connection to the database
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }
    
    
    //this method might be void due to encapsulation within the connection class. connect() does the same thing
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
    
    public int getOrderNumber() {
    	// method created for ease in testing
    	return this.orderNumber;
    }
    
    public void MaxValue() {
    	connect();
        String maxQuery = "SELECT MAX(OrderNumber) AS max_number FROM Orders";
    	//returns the highest value in the database so that the number can be incremented to create a unique order number as the primary key
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(maxQuery);
            // ResultSet returns a list type object not a stream
            if (rs.next()) {
                this.orderNumber = rs.getInt("max_number");
                System.out.println(this.orderNumber);
                System.out.println("order number updated");
            } else {
                System.out.println("No data found.");
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }
    
    public void nextOrderNumber() {
    	//order number incremented so that the next order number is unique
    	this.orderNumber++;
    }
    /*
    public void listTables() {
    	///* method initially created to test connection to database and get information regarding the database without performing manipulation/
        if (connection == null) {
        	//print debugging 
            System.out.println("No connection to the database. Please connect first.");
            return;
        }
        //returns all tables
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
     */


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
   
    public boolean createUser(String userName, String password, String firstName, String lastName) {
    	//creates user, returns true if successful
        connect();
        
        //query to create new simple user
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
    	connect();
    	//method to delete user by looking up user name
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return false;
        }
        
        //deleting query
        String query = "DELETE FROM Users WHERE userName = ?";
        
        //connection and execution of query
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
    
    //create user if the user is a vip separate method for handling extra input fields
    public boolean createVIPUser(String userName, String password, String firstName, String lastName, String email, int points) {
    	connect();
        if (connection == null) {
            System.out.println("No connection to the database. Please connect first.");
            return false;
        }
        
        //separates from normal user by points and email
        String query = "INSERT INTO Users (userName, password, firstName, lastName, email, points) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, email);
            pstmt.setInt(6, points);
            
            //execute query
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
    	connect();
    	//simple boolean query to check if user is VIP or not by email look up.
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
   
    
    
    public boolean checkIfReadyForPickUp(int orderNumber) {
    	connect();
    	/* back end method that checks if a user order is ready. Called in order manager controller*/
    	
    	//connect ot db
        connect();
        
        //query to return ready date and time
        String orderQuery = "SELECT dateCollected FROM Orders WHERE OrderNumber = ?";
        
        //current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        
        //execute statement
        try (PreparedStatement pstmt = connection.prepareStatement(orderQuery)) {
            pstmt.setInt(1, orderNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pickUpTime = rs.getString("dateCollected");
                LocalDateTime pickUpDateTime = LocalDateTime.parse(pickUpTime, formatter);
                //return condtion for whether the order is ready
                return currentDateTime.isAfter(pickUpDateTime) || currentDateTime.isEqual(pickUpDateTime);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.err.println("Date Parsing Error: " + e.getMessage());
        }
        return false;
    }
    	
    	
    public void pickUpOrder(int orderNumber) {
    	connect();
    	/*Method to update an order to picked up. Called in OrderManager. 
    	 * Sets an orders picked up time and collected status to collected if ready to be collected
    	 */
    	connect();
        String query = "UPDATE Orders SET Status = ?, dateCollected = ? WHERE OrderNumber = ?";
        //current time
        String formattedDate = getStringNow();
           
        //execute query
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "collected");  
            pstmt.setString(2, formattedDate); 
            pstmt.setInt(3, orderNumber);    
            int rowsAffected = pstmt.executeUpdate(); 
            if (rowsAffected > 0) {
                System.out.println("Order updated successfully.");
            } else {
                System.out.println("No order was updated. Please check the order number."); 
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    
    public void cancelOrder(int orderNumber) {
    	connect();
    	// Method to cancel an order if user want to cancel.
        //query to update cancelled
        String cancelQuery = "UPDATE Orders SET Status = ?, dateCollected = ? WHERE OrderNumber = ?";
        String cancelledStatus = "cancelled";
        //execute query
        try (PreparedStatement cancelOrder = connection.prepareStatement(cancelQuery)) {
            cancelOrder.setString(1, cancelledStatus);
            cancelOrder.setString(2, getStringNow());
            cancelOrder.setInt(3, orderNumber);
            int rowsAffected = cancelOrder.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order cancelled successfully.");
            } else {
                System.out.println("No rows affected. Check if the order number exists and is correct.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();  
        }
    }
    
    private String getStringNow() {
    	//Method for returning the current date and time to put into the database
    	LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String formattedDate = currentDateTime.format(formatter);
        return formattedDate;
    }
    
    public void deleteOrders(int OrderNumber) {
    	connect();
    	 /* method implemented for testing purposes to connect to the database*/
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
		/*validation that a user exists in the database*/
		connect();		
		//username is unique so user should be returned at position 1 if found
	    String query = "SELECT 1 FROM Users WHERE UserName = ?"; 
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, user);
	            ResultSet results = stmt.executeQuery();
	            return results.next(); 
	        }  catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	        return false;          	
	    } 
	} 

	public boolean checkPassword(String user, String inputPassword) {
		connect();
		/* check if password is the same as input string. Return true if correct. 
		 * called in login for authnetication
		 */
		System.out.println(inputPassword);
		
		// return password
	    String query = "SELECT Password FROM Users WHERE UserName = ?";
	    try {
	        connect(); 
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, user);
	            ResultSet results = stmt.executeQuery();
	            if (results.next()) {
	                String storedPassword = results.getString("Password");
	                System.out.println("Password: " + storedPassword);
	                return storedPassword.equals(inputPassword); 
	            }
	            return false; 
	        } 
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	        return false;
	    }
	}

	public User getUserFromDatabase(String username) {
		connect();
		/* gets a user from a database based on username. Returns a VIP user if there is an email otherwise a regular user*/
		
		User user = null;
        String query = "SELECT UserName, Password, FirstName, LastName, Email, Points FROM Users WHERE UserName = ?";
        try {
            connect(); 
            
            //SQL update
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    String user1 = results.getString("UserName");
                    String pass = results.getString("Password");
                    String first = results.getString("FirstName");
                    String last = results.getString("LastName");
                    String email = results.getString("Email");
                    int points =  results.getInt("Points");
                    if (email != null) {
                    	//if an email exists it is a VIp user
                    	
                    	//use factory?
                        return new VIPUser(user1, pass, first, last, email, points); 
                    } else {
                        return new NormalUser(user1, pass, first, last); 
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return user;
    }
	
	
	public void updateFirstName(String newName, String currentUser) {
		// database connection to update first name for user. Performed as programming is running for stability
		connect();  
	    String query = "UPDATE Users SET FirstName = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newName);
	        pstmt.setString(2, currentUser);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("First name updated successfully.");
	        } else {
	            System.out.println("No rows affected.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	    }
	}

	public void updateLastName(String newName, String currentUser) {
		// database connection to update last name for user. Performed as programming is running for stability
		connect();  
	    String query = "UPDATE Users SET LastName = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newName);
	        pstmt.setString(2, currentUser);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	//execute update
	            System.out.println("Last name updated successfully.");
	        } else {
	            System.out.println("No rows affected.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	    }
	}
		
	public void updatePassword(String newPass, String currentUser) {
		// database connection to update password for user. Performed as programming is running for stability
		connect();  
	    
	    //change password to new string
	    String query = "UPDATE Users SET Password = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPass);
	        pstmt.setString(2, currentUser);
	        int rowsAffected = pstmt.executeUpdate();
	        //execute update
	        if (rowsAffected > 0) {
	            System.out.println("Password updated successfully.");
	        } else {
	            System.out.println("No rows affected.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	    }
	}
	
	public void updateEmail(String email, String currentUser) {
		// database connection to update email for user. Performed as programming is running for stability. Called for when creating a VIP user
		connect();  
	    String query = "UPDATE Users SET Email = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, currentUser);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Email updated successfully.");
	        } else {
	            System.out.println("No rows affected.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	    }
	}
	
	public void updatePoints(String currentUser) {
		// database connection to update Points for user. Called when creating a normal user to not have a null field
	    connect();  
	    //Query to set points
	    String query = "UPDATE Users SET Points = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	//sets points to 0
	        pstmt.setInt(1, 0);
	        pstmt.setString(2, currentUser);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Points updated successfully.");
	        } else {
	            System.out.println("No rows affected. User may not exist.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	public ObservableList<Order> getOrdersForExport(String username){
		//Same as landing page, logic to return orders based on 
    	connect();
    	//create observable list for property value factory and cell value factory
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        
        //Query to return ALL orders
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price, status FROM Orders WHERE OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	int orderNum = rs.getInt("OrderNumber");
                int burritos = rs.getInt("Burritos");
                int fries = rs.getInt("Fries");
                int sodas = rs.getInt("Sodas");
                double price = rs.getDouble("Price");
                String status = rs.getString("Status");
                Order order = new Order(
                    burritos,
                    fries,
                    sodas);
                order.setDateCreated(rs.getString("dateCreated"));
                order.setOrderNum(orderNum);
                order.setPrice(price); 
                order.setStatus(status);
                ordersList.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return ordersList;
	}
	
	public ObservableList<Order> getActiveOrders(String username){
    	//Observable list allows for iteration through the tableview
		connect();
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        //query that filters for orders that are awaiting collection
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price FROM Orders WHERE Status = 'await for collection' AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";
        try (
            PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            
            //execute update
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	int orderNum = rs.getInt("OrderNumber");
                int burritos = rs.getInt("Burritos");
                int fries = rs.getInt("Fries");
                int sodas = rs.getInt("Sodas");
                double price = rs.getDouble("Price");
                Order order = new Order(
                    burritos,
                    fries,
                    sodas);
                order.setDateCreated(rs.getString("dateCreated"));
                order.setOrderNum(orderNum);
                order.setPrice(price); 
                ordersList.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return ordersList;
	}
	
	public void processOrder(String userName, Order order) {
		newOrder(userName, order);
		addUserOrder(userName, this.getOrderNumber());
	}
	
	public void newOrder(String userName, Order order) {
			connect();
	    	//Use connector to get the next unique order number
	    	MaxValue();
	    	int orderNumber = getOrderNumber();
	    	orderNumber++;
	    	//query to insert full order into database
	    	String query = "INSERT INTO Orders(dateCreated, OrderNumber, Burritos, Fries, Sodas, Meals, Status, Price, dateCollected) VALUES(?, ?, ?, ?, ?, ?, ?, ?,?)";
	    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    		pstmt.setString(1, order.getDateCreated());
	            pstmt.setInt(2, orderNumber);
	            pstmt.setInt(3, order.getBurritos());
	            pstmt.setInt(4, order.getFries());
	            pstmt.setInt(5, order.getSodas());
	            pstmt.setInt(6, order.getMeals());
	            String collection = "await for collection";
	            pstmt.setString(7, collection);
	            pstmt.setDouble(8, order.getPrice());
	            pstmt.setString(9, order.getDatePickedUp());
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("Order added successfully.");
	            } else {
	                System.out.println("No rows affected.");
	            }
	        } catch (SQLException e) {
	            System.out.println("SQL Error: " + e.getMessage());
	        } 
	    }

    public void addUserOrder(String userName, int orderNumber) {
    	connect();
    	//adds user and current order number into the userorders table to maintain relationship
    	String query = "INSERT INTO UserOrders(Username, OrderNumber) VALUES (?, ?)";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setInt(2, orderNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
	
}