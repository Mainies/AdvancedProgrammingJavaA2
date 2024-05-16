package service;
import restaurant.Order;

public class OrderService implements ApplicationService<Order>{
	/* Application service for Orders. Allows clearing to only work on current order in program*/
    private static OrderService instance;

    //volatile apparently is a good keyword if you expect this to change a lot throughout the program
    private volatile Order order;
    
    private OrderService() {
 	   this.order = null;
    }
    
	   public static OrderService getInstance() {
	        if (instance == null) {
	            instance = new OrderService();
	        }
	        return instance;
	    }
	   
	   public void setObject(Order newOrder) {
	    	this.order = newOrder;
	    }

	    public Order getObject() {
	    	return this.order;
	    }
	       
	    public void clearObject() {
	    	this.order = null;
	    }
}
