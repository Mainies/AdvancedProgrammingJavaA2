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
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.restaurant.*;

interface IConnect{
	
	//IOrderInput
	boolean checkValidOrder(int orderNumber, String username);
	
	void pickUpOrder(int orderNumber);
	
	void cancelOrder(int orderNumber);
	
	void deleteOrders(int OrderNumber);
	
	void newOrder(String userName, Order order);
	
	ArrayList<Integer> fetchVipOrders();
	
	boolean checkIfReadyForPickUp(int orderNumber);
	
	ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders);
	
	ArrayList<Integer> getListOfUserOrderNumbers(String userName);
	
	ObservableList<Order> getActiveOrders(String username);
	
	ObservableList<Order> getOrdersForExport(String username);
	
	void MaxValue();
	
	void logoutPoints(String username, int points);
	
	boolean createUser(String userName, String password, String firstName, String lastName);
	
	boolean createVIPUser(String userName, String password, String firstName, String lastName, String email, int points);
	
	boolean deleteUser(String userName);
	
	void addUserOrder(String userName, int orderNumber);
	
	void updateDetails(String condition, String newValue, String currentUser);
	
	void updatePointsInDB(VIPUser vipUser);
	
	void insertMissingVipOrders(ArrayList<Integer> numsToPopulate);
	
	void updatePoints(String currentUser);
	
	void collectPoints(int orderNumber);
	
	boolean isVIP(String userName);
	
	boolean checkPassword(String user, String inputPassword);
	
	boolean isUser(String user);
	
	User getUserFromDatabase(String username);
}

public class Connect implements IConnect{
	
	@Override
	public boolean checkValidOrder(int orderNumber, String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pickUpOrder(int orderNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelOrder(int orderNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteOrders(int OrderNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newOrder(String userName, Order order) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Integer> fetchVipOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkIfReadyForPickUp(int orderNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getListOfUserOrderNumbers(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<Order> getActiveOrders(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<Order> getOrdersForExport(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void MaxValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logoutPoints(String username, int points) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean createUser(String userName, String password, String firstName, String lastName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createVIPUser(String userName, String password, String firstName, String lastName, String email,
			int points) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUser(String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addUserOrder(String userName, int orderNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDetails(String condition, String newValue, String currentUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePointsInDB(VIPUser vipUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertMissingVipOrders(ArrayList<Integer> numsToPopulate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePoints(String currentUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collectPoints(int orderNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVIP(String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkPassword(String user, String inputPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUser(String user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User getUserFromDatabase(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	/* This class is intended to be a facade to make connection with program easier to understand*/
}

interface IOrderInput {
	boolean checkValidOrder(int orderNumber, String username);
	
	void MaxValue();

	void pickUpOrder(int orderNumber, String formattedDate);
	
	void cancelOrder(int orderNumber, String formattedDate);
	
	void deleteOrders(int OrderNumber);
	
	void newOrder(String userName, Order order);
}

interface DateTime{
	public String getNow();
}

class OrderInput extends DBConnect implements IOrderInput{
	private int orderNumber;
	
    public int getOrderNumber() {
    	// method created for ease in testing
    	return this.orderNumber;
    }
        
    public void nextOrderNumber() {
    	//order number incremented so that the next order number is unique
    	this.orderNumber++;
    }

	
	@Override
    public boolean checkValidOrder(int orderNumber, String username) {
    	connect();
        String query = "SELECT OrderNumber FROM Orders WHERE Status = 'await for collection' AND OrderNumber = ? AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, orderNumber);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }
	
	@Override	
    public void pickUpOrder(int orderNumber, String formattedDate) {
    	/*Method to update an order to picked up. Called in OrderManager. 
    	 * Sets an orders picked up time and collected status to collected if ready to be collected
    	 */
    	connect();
        String query = "UPDATE Orders SET Status = ?, dateCollected = ? WHERE OrderNumber = ?";
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
	
	@Override
    public void cancelOrder(int orderNumber, String formattedDate) {
    	connect();
    	// Method to cancel an order if user want to cancel.
        //query to update cancelled
        String cancelQuery = "UPDATE Orders SET Status = ?, dateCollected = ? WHERE OrderNumber = ?";
        String cancelledStatus = "cancelled";
        //execute query
        try (PreparedStatement cancelOrder = connection.prepareStatement(cancelQuery)) {
            cancelOrder.setString(1, cancelledStatus);
            cancelOrder.setString(2, formattedDate);
            cancelOrder.setInt(3, orderNumber);
            int rowsAffected = cancelOrder.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order cancelled successfully.");
            } else {
                System.out.println("No rows affected. Check if the order number exists and is correct.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
	
	@Override
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
	
	@Override
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
	
	 @Override
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
}


interface IOrderOutput{
	ArrayList<Integer> fetchVipOrders();
	
	boolean checkIfReadyForPickUp(int orderNumber);
	
	ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders);
	
	ArrayList<Integer> getListOfUserOrderNumbers(String userName);
	
	ObservableList<Order> getActiveOrders(String username);
	
	ObservableList<Order> getOrdersForExport(String username);
	
}

class OrderOutput extends DBConnect implements IOrderOutput{
	
	@Override
    public ArrayList<Integer> fetchVipOrders() {
    	//Query database and return list of orders
    	connect();
        ArrayList<Integer> vipOrders = new ArrayList<>();
        String query = "SELECT OrderNumber FROM VIPPoints";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vipOrders.add(rs.getInt("OrderNumber"));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return vipOrders;
    }
    
    @Override
    public ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders) {
    	//Compare user orders with vip list of orders. Return those that havent' been added to VIP points table
        ArrayList<Integer> numsToPopulate = new ArrayList<>();
        for (Integer orderNum : orderNumbersList) {
            if (!vipOrders.contains(orderNum)) {
                numsToPopulate.add(orderNum);
            }
        }
        return numsToPopulate;
    }

    @Override    
    public boolean checkIfReadyForPickUp(int orderNumber) {
    	connect();
    	/* back end method that checks if a user order is ready. Called in order manager controller*/      
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


    @Override
    public ArrayList<Integer> getListOfUserOrderNumbers(String userName) {
    	//returns a list of order numbers that a user has ordered to be checked if they have already been claimed
        connect();
        //Query to return all ordernumbers by username
        String query = "SELECT OrderNumber FROM UserOrders WHERE UserName = ?";
        ArrayList<Integer> orderNumbers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            //executing query
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orderNumbers.add(rs.getInt("OrderNumber"));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return orderNumbers;
    }

    @Override
	public ObservableList<Order> getActiveOrders(String username){
    	//Observable list allows for iteration through the tableview
		connect();
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        //query that filters for orders that are awaiting collection
        String activeOrdersQuery = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price FROM Orders WHERE Status = 'await for collection' AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";
        try (
            PreparedStatement orderStatement = connection.prepareStatement(activeOrdersQuery)) {
        	orderStatement.setString(1, username);
            //execute update
            ResultSet rs = orderStatement.executeQuery();
            while (rs.next()) {
            	int orderNum = rs.getInt("OrderNumber");
                int burritos = rs.getInt("Burritos");
                int fries = rs.getInt("Fries");
                int sodas = rs.getInt("Sodas");
                double price = rs.getDouble("Price");
                Order order = new Order(burritos, fries, sodas);
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

    @Override
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
                Order order = new Order(burritos, fries, sodas);
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
	
	
}

//Done
interface IUserInput{
	void logoutPoints(String username, int points);
	
	boolean createUser(String userName, String password, String firstName, String lastName);
	
	boolean createVIPUser(String userName, String password, String firstName, String lastName, String email, int points);
	
	boolean deleteUser(String userName);
	
	void addUserOrder(String userName, int orderNumber);
	
	void updateDetails(String condition, String newValue, String currentUser);
}



interface IVIPUserInput{
	void updatePointsInDB(VIPUser vipUser);
	
	void insertMissingVipOrders(ArrayList<Integer> numsToPopulate);
	
	void updatePoints(String currentUser);
}

interface IVIPUserOutput{
	void collectPoints(int orderNumber);
}


//Done
interface IUserOutput{
	boolean isVIP(String userName);
	
	boolean checkPassword(String user, String inputPassword);
	
	boolean isUser(String user);
	
	User getUserFromDatabase(String username);
}

class VIPUserInput extends DBConnect implements IVIPUserInput{
	@Override
    public void updatePointsInDB(VIPUser vipUser) {
    	String query = "SELECT ord.OrderNumber, ord.Price, v.Collected " +
                "FROM Orders ord " +
                "JOIN UserOrders uo ON ord.OrderNumber = uo.OrderNumber " +
                "JOIN VipPoints v ON ord.OrderNumber = v.OrderNumber " +
                "WHERE ord.Status = 'collected' AND v.Collected = false " +
                "AND uo.UserName = ?";
        //Connect to DB and execute query
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vipUser.getUsername());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	//Getting variables for interaction with program
                double price = rs.getDouble("Price");
                boolean collected = rs.getBoolean("Collected");
                int orderNumber = rs.getInt("OrderNumber");
                if (!collected) {
                	//Floor per dollar spent
                    int pointsToAdd = (int) Math.floor(price);
                    //update current loyalty points
                    vipUser.setLoyaltyPoints(vipUser.getLoyaltyPoints() + pointsToAdd);
                    //change collected to true in VIP points so it cannot be redeemed again
                    collectPoints(orderNumber);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
	
	 @Override
	    public void insertMissingVipOrders(ArrayList<Integer> numsToPopulate) {
	    	connect();
	    	//Put missing orders into VIP orders and set their collected value to false
	        String query = "INSERT INTO VIPPoints (OrderNumber, Collected) VALUES (?, false)";
	        for (Integer missingOrder : numsToPopulate) {
	            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	                pstmt.setInt(1, missingOrder);
	                //execute query
	                pstmt.executeUpdate();
	            } catch (SQLException e) {
	                System.err.println("SQL Error: " + e.getMessage());
	            }
	        }
	    }
	
	 @Override
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
}

class UserInput extends DBConnect implements IUserInput{
	@Override
	public void logoutPoints(String username, int points) {
		connect();
		String updateQuery = "UPDATE Users SET Points = ? WHERE username = ?";
        // update points in database for next log in time
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, points);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
	}
	
	@Override
	public boolean createUser(String userName, String password, String firstName, String lastName) {
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
	
	@Override
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
	
	@Override
	public boolean deleteUser(String userName) {
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
	
	
	@Override
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
	
	@Override
	public
	void updateDetails(String condition, String newValue, String currentUser) {
		String detailsQuery = "UPDATE Users SET " + condition + " = ? WHERE UserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(detailsQuery)) {
	    	pstmt.setString(1, newValue);
	        pstmt.setString(2, currentUser);
	        System.out.println("success fully set up details");
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	System.out.println("made it here");
	            System.out.println(condition + " updated successfully.");
	        } else {
	            System.out.println("No rows affected.");
	        }
	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}
}

class UserOutput extends DBConnect implements IUserOutput{
	
	@Override
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
	
    @Override
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
	
    @Override
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
    
    @Override
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
}

abstract class DBConnect {
	/* Major class to implement connection via the JDBC
	allows various other objects to communicate and interact with the sql lite database
	methods to update values in tables, get values from tables and execute queries from other objects
	*/
	
    protected Connection connection; // Made it a class member so it's accessible in listTables()
    private String dbURL = "jdbc:sqlite:src/Restaurant.db";
                
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
       
    
   
    
    	
    
    
    
    
    private String getStringNow() {
    	//Method for returning the current date and time to put into the database
    	LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String formattedDate = currentDateTime.format(formatter);
        return formattedDate;
    }
    
    
    
	
	public void updateFirstName(String newName, String currentUser) {
		// database connection to update first name for user. Performed as programming is running for stability
		updateDetails("FirstName", newName, currentUser); 
	}

	public void updateLastName(String newName, String currentUser) {
		// database connection to update last name for user. Performed as programming is running for stability
		updateDetails("LastName", newName, currentUser); 
	}
		
	public void updatePassword(String newPass, String currentUser) {
		// database connection to update password for user. Performed as programming is running for stability
		updateDetails("Password", newPass, currentUser);
	}
	
	public void updateEmail(String email, String currentUser) {
		// database connection to update email for user. Performed as programming is running for stability. Called for when creating a VIP user
		updateDetails("Email", email, currentUser);
	}
	
	@Override
	public void updateDetails(String condition, String newValue, String currentUser) {
		connect();
	}	
	
	public void processOrder(String userName, Order order) {
		newOrder(userName, order);
		addUserOrder(userName, this.getOrderNumber());
	}
		
	@Override
    public void addUserOrder(String userName, int orderNumber) {
    }   
    
    public void checkIfPopulated(ArrayList<Integer> orderNumbersList) {
    	//Get list of VIP user orders
        ArrayList<Integer> vipOrders = fetchVipOrders();
        //Compare VIP orders to all orders
        ArrayList<Integer> numsToPopulate = getMissingVipOrders(orderNumbersList, vipOrders);
        //Populate VIP list with orders that are not in VIPUser Table
        insertMissingVipOrders(numsToPopulate);
    }
    
    
    
    
    @Override
    public void collectPoints(int orderNumber) {
    	String updateQuery = "UPDATE VipPoints SET Collected = true WHERE OrderNumber = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, orderNumber);
            updateStmt.executeUpdate();
        } catch(SQLException e) {
        	System.err.println("SQL Error: " + e.getMessage());
        }
        
    }
    
    public void updatePointsFull(VIPUser vipUser) {
    	String username = vipUser.getUsername();
    	ArrayList<Integer> userOrderNumbers = getListOfUserOrderNumbers(username);
    	checkIfPopulated(userOrderNumbers);
    	updatePointsInDB(vipUser);
    }
    
    @Override
    public void logoutPoints(String username, int points) {
    	connect();
    }  
    
   
}








