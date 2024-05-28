package model.database;

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
import model.restaurant.Order;


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
    	super.connect();
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
    	super.connect();
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
    	super.connect();
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
    	super.connect();
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
			super.connect();
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
	    	super.connect();
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

//Done
interface IOrderInput {
	boolean checkValidOrder(int orderNumber, String username);
	
	void MaxValue();

	void pickUpOrder(int orderNumber, String formattedDate);
	
	void cancelOrder(int orderNumber, String formattedDate);
	
	void deleteOrders(int OrderNumber);
	
	void newOrder(String userName, Order order);
}



//Done
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
  	super.connect();
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
  	super.connect();
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
      super.connect();
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
		super.connect();
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
  	super.connect();
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
