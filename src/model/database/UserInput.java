package model.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class UserInput extends DBConnect implements IUserInput{
	@Override
	public void logoutPoints(String username, int points) {
		super.connect();
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
		super.connect();
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
		super.connect();
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
		super.connect();
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
		super.connect();
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
		super.connect();
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
    	super.connect();
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
		super.connect();
		/* check if password is the same as input string. Return true if correct. 
		 * called in login for authnetication
		 */
		System.out.println(inputPassword);
		
		// return password
	    String query = "SELECT Password FROM Users WHERE UserName = ?";
	    try {
	        super.connect(); 
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
   		super.connect();		
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
		super.connect();
		/* gets a user from a database based on username. Returns a VIP user if there is an email otherwise a regular user*/
		User user = null;
        String query = "SELECT UserName, Password, FirstName, LastName, Email, Points FROM Users WHERE UserName = ?";
        try {//SQL update
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



//Done
interface IUserInput{
	void logoutPoints(String username, int points);
	
	boolean createUser(String userName, String password, String firstName, String lastName);
	
	boolean createVIPUser(String userName, String password, String firstName, String lastName, String email, int points);
	
	boolean deleteUser(String userName);
	
	void addUserOrder(String userName, int orderNumber);
	
	void updateDetails(String condition, String newValue, String currentUser);
}


//Done
interface IVIPUser{
	void updatePointsInDB(VIPUser vipUser);
	
	void insertMissingVipOrders(ArrayList<Integer> numsToPopulate);
	
	void updatePoints(String currentUser);
	
	void collectPoints(int orderNumber);
}


//Done
interface IUserOutput{
	boolean isVIP(String userName);
	
	boolean checkPassword(String user, String inputPassword);
	
	boolean isUser(String user);
	
	User getUserFromDatabase(String username);
}

class VIPUserDB extends DBConnect implements IVIPUser{
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
	    	super.connect();
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
		    super.connect();  
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
}








