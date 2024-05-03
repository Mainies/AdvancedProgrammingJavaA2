package service;
import restaurant.Order;

public class OrderService implements ApplicationService<Order>{
    private static OrderService instance;
    private Order order;
    
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
